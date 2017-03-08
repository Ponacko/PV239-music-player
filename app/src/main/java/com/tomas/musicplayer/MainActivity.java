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
import java.util.List;



class Mp3Filter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        return (name.endsWith(".mp3"));
    }
}

class DirFilter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        return (file.isDirectory());
    }
}

public class MainActivity extends ListActivity {
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getParentFile().getParentFile().getPath();
    private static final String SD_PATH2 = Environment.getExternalStorageDirectory().getParentFile().getPath();
    private static final String playSymbol = "▶";
    private static final String pauseSymbol = "❚❚";
    private List<String> songPaths = new ArrayList<>();
    private List<Song> songs = new ArrayList<Song>();
    final MediaPlayer mp = new MediaPlayer();
    Button pl = null;
    TextView artistText;
    TextView songText;
    ImageView artwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button play = (Button) findViewById(R.id.playButton);
        pl = play;
        artistText = (TextView)findViewById(R.id.artistText);
        songText = (TextView)findViewById(R.id.songText);
        artwork = (ImageView)findViewById(R.id.artwork);
        updatePlaylist();

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
            mp.setDataSource(songPaths.get(position));
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    Song currentSong = songs.get(position);
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
            songs.add(new Song(s, md));
        }

        SongAdapter songList = new SongAdapter(this, R.layout.song_item, songs);
        setListAdapter(songList);

    }
}
