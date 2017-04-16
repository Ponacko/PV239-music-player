package com.tomas.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String playSymbol = "▶";
    private static final String pauseSymbol = "❚❚";
    private List<Song> songs = new ArrayList<Song>();
    final MediaPlayer mp = MpWrapper.createMp();
    Button pl = null;
    TextView artistText;
    TextView songText;
    ImageView artwork;
    Realm realm;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    SongAdapter songList;
    private Song currentSong;
    private PlaylistUpdater playlistUpdater;
    private String[] selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        final Button play = (Button) findViewById(R.id.playButton);
        pl = play;
        artistText = (TextView)findViewById(R.id.artistText);
        songText = (TextView)findViewById(R.id.songText);
        artwork = (ImageView)findViewById(R.id.artwork);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<Song> results = realm.where(Song.class).findAll();
        final Current current = realm.where(Current.class).findFirst();

        if (current != null){
            currentSong = realm.where(Song.class).equalTo("path", current.path).findFirst();
        }

        if (results.isEmpty()){
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            properties.selection_type = DialogConfigs.DIR_SELECT;
            properties.root = new File("/");
            properties.error_dir = new File(DialogConfigs.STORAGE_DIR);
            properties.offset = new File(DialogConfigs.STORAGE_DIR);
            properties.extensions = null;
            FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
            dialog.setTitle("Select your music folder");
            dialog.show();
            dialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    selectedFiles = files;
                    loadFromFolders(files);
                }
            });

        }
        else {
            songs = results.subList(0, results.size() - 1);
        }

        songList = new SongAdapter(this, R.layout.song_item, songs);

        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                   startActivity(intent);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying() && currentSong!= null){
                    play(currentSong);
                }
                else {
                    mp.pause();
                    play.setText(playSymbol);
                }
            }
        });
    }


    private void loadFromFolders(String[] files){
        for(int i = 0; i< files.length; i++){
            PlaylistUpdater playlistUpdater = new PlaylistUpdater();
            playlistUpdater.setContext(MainActivity.this);
            playlistUpdater.execute(files[i]);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        Current current = realm.where(Current.class).findFirst();
        if (current != null){
            currentSong = realm.where(Song.class).equalTo("path", current.path).findFirst();

        artistText.setText(currentSong.getArtist());
        songText.setText(currentSong.getTitle());
        }
        if(mp.isPlaying()){
            pl.setText(pauseSymbol);
        }
    }

    public void play(Song song){
        try{
            mp.reset();
            switchCurrentSong(song);
            mp.setDataSource(currentSong.getPath());


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://lyric-api.herokuapp.com/api/find/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HerokuappService service = retrofit.create(HerokuappService.class);
            if ((currentSong.lyrics == null || currentSong.lyrics.isEmpty())
                    && currentSong.getArtist() != Song.NO_ARTIST) {
                Call<Lyrics> lyricsCall = service.getLyrics(currentSong.getArtist(), currentSong.getTitle());
                lyricsCall.enqueue(new Callback<Lyrics>() {
                    @Override
                    public void onResponse(Call<Lyrics> call, Response<Lyrics> response) {
                        if (response.body() != null){
                            realm.beginTransaction();
                            currentSong.lyrics = response.body().lyric;
                            realm.commitTransaction();
                        }
                    }

                    @Override
                    public void onFailure(Call<Lyrics> call, Throwable t) {
                        //t.getMessage();
                        //t.printStackTrace();

                    }
                });
            }

            artistText.setText(currentSong.getArtist());
            songText.setText(currentSong.getTitle());
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    pl.setText(pauseSymbol);
                }
            });
            mp.prepareAsync();

        } catch (IOException e) {
            Log.v(getString(R.string.app_name),e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ArtistFragment(), "ARTISTS");
        adapter.addFragment(new SongFragment(), "SONGS");
        viewPager.setAdapter(adapter);
    }

    public SongAdapter getSongAdapter(){
        return songList;
    }

    private void switchCurrentSong(Song to){
        currentSong = to;
        Current current = realm.where(Current.class).findFirst();
        realm.beginTransaction();
        if (current == null){
            current = new Current();
            current.path = currentSong.getPath();
            realm.copyToRealm(current);
        }
        else {
            current.path = currentSong.getPath();
        }
        realm.commitTransaction();
    }
}
