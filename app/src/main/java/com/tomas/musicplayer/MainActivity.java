package com.tomas.musicplayer;

import android.app.ListActivity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class MainActivity extends ListActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        SongAdapter songList = new SongAdapter(this, R.layout.song_item, songs);
        setListAdapter(songList);


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

    @Override
    protected void onListItemClick(ListView list, View v, final int position, long id) {
        try{

            mp.reset();
            final Song currentSong = songs.get(position);
            mp.setDataSource(currentSong.getPath());
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    artistText.setText(currentSong.getArtist());
                    songText.setText(currentSong.getTitle());
                    Picasso.with(MainActivity.this).load(currentSong.getArtwork()).into(artwork);
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
