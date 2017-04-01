package com.tomas.musicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getParentFile().getParentFile().getPath();
    private static final String SD_PATH2 = Environment.getExternalStorageDirectory().getParentFile().getPath();
    private static final String playSymbol = "▶";
    private static final String pauseSymbol = "❚❚";
    private List<String> songPaths = new ArrayList<>();
    private List<Song> songs = new ArrayList<Song>();
    private Set<Artist> artists = new HashSet<>();
    final MediaPlayer mp = new MediaPlayer();
    Button pl = null;
    TextView artistText;
    TextView songText;
    ImageView artwork;
    Realm realm;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SongAdapter songList;
    private Song currentSong;

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
        if (results.isEmpty()){
            updatePlaylist();
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
                if (currentSong != null){
                    intent.putExtra("currentSongPath", currentSong.getPath());
                    startActivity(intent);
                }

            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying()){
                    mp.start();
                    play.setText(pauseSymbol);
                }
                else {
                    mp.pause();
                    play.setText(playSymbol);
                }

            }
        });
    }

    public void play(int position){
        try{
            mp.reset();
            currentSong = songs.get(position);
            mp.setDataSource(currentSong.getPath());


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://lyric-api.herokuapp.com/api/find/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HerokuappService service = retrofit.create(HerokuappService.class);
            Call<Lyrics> lyricsCall = service.getLyrics(currentSong.getArtist(), currentSong.getTitle());
            lyricsCall.enqueue(new Callback<Lyrics>() {
                @Override
                public void onResponse(Call<Lyrics> call, Response<Lyrics> response) {
                    realm.beginTransaction();
                    currentSong.lyrics = response.body().lyric;
                    realm.commitTransaction();
                }

                @Override
                public void onFailure(Call<Lyrics> call, Throwable t) {
                    //t.getMessage();
                    //t.printStackTrace();

                }
            });
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


    private void findSongs(File folder) {
        File[] files = folder.listFiles(new Mp3Filter());
        if (files != null && files.length > 0) {
            for (File f : files) {
                songPaths.add(f.getAbsolutePath());
            }
        }
        File[] dirs = folder.listFiles(new DirFilter());
        if (dirs != null && dirs.length > 0) {
            for (File f : dirs) {
                findSongs(f);
            }
        }
    }


    private void updatePlaylist() {

        File home = new File(SD_PATH);
        if (!home.exists()){
            home = new File(SD_PATH2);
        }
        final MediaMetadataRetriever md = new MediaMetadataRetriever();
        findSongs(home);
        for (String s : songPaths){
            Song song = new Song();
            song.init(s, md);
            String artistName = song.getArtist();
            if (artistName != Song.NO_ARTIST){
                Artist artist = new Artist();
                artist.init(artistName);
                artists.add(artist);
            }
            songs.add(song);
        }
        realm.beginTransaction();
        List<Artist> realmArtists = realm.copyToRealm(artists);
        List<Song> realmSongs = realm.copyToRealm(songs);
        realm.commitTransaction();
    }
}
