package com.tomas.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class PlayActivity extends AppCompatActivity {
    private Song currentSong;
    private Button forwardButton, backwardButton, playButton;
    private TextView currentTimeText, durationTimeText;
    private SeekBar seekBar;
    private int currentTime;
    private int durationTime;
    private static final int skipSeconds = 5000;
    private static final String pauseSymbol = "❚❚";
    private static final String playSymbol = "▶";
    final MediaPlayer mp = MpWrapper.createMp();

    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);

        currentTimeText = (TextView) findViewById(R.id.currentTimeText);
        durationTimeText = (TextView) findViewById(R.id.durationTimeText);

        forwardButton = (Button) findViewById(R.id.forwardButton);
        backwardButton = (Button)findViewById(R.id.backwardButton);
        playButton = (Button)findViewById(R.id.playButton);
        if (mp.isPlaying())
            playButton.setText(pauseSymbol);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        Realm realm = Realm.getDefaultInstance();
        Current current = realm.where(Current.class).findFirst();
        currentSong = realm.where(Song.class).equalTo("path", current.path).findFirst();
        TextView lyric = (TextView)findViewById(R.id.lyric);
        lyric.setText(currentSong.lyrics);
        setTitle(currentSong.getTitle());
        getSupportActionBar().setTitle(currentSong.getTitle());


        durationTime = mp.getDuration();
        currentTime = mp.getCurrentPosition();
        durationTimeText.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) durationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) durationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationTime)))
        );
        seekBar.setMax((int) 100);
        seekBar.setProgress((int) currentTime);
        myHandler.postDelayed(UpdateSongTime,100);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying() && currentSong!= null){
                    mp.start();
                    playButton.setText(pauseSymbol);
                }
                else {
                    mp.pause();
                    playButton.setText(playSymbol);
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                currentTime = mp.getCurrentPosition();
                if(currentTime + skipSeconds <= mp.getDuration()){
                    mp.seekTo(currentTime + skipSeconds);
                }else{
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                currentTime = mp.getCurrentPosition();
                if(currentTime - skipSeconds >= 0){
                    mp.seekTo(currentTime - skipSeconds);
                }else{
                    mp.seekTo(0);
                }
           }
        });

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                myHandler.removeCallbacks(UpdateSongTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
                durationTime = mp.getDuration();
                currentTime = progressToTimer(seekBar.getProgress(), durationTime);
                mp.seekTo(currentTime);
                // update timer progress again
                myHandler.postDelayed(UpdateSongTime,100);
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            currentTime = mp.getCurrentPosition();
            currentTimeText.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) currentTime)))
            );
            int progress = (int)(getProgressPercentage(currentTime, durationTime));
            seekBar.setProgress(progress);
            myHandler.postDelayed(this, 100);
        }
    };

    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        currentTime = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentTime = (int) ((((double)progress) / 100) * totalDuration);
        // return current duration in milliseconds
        return currentTime * 1000;
    }

}
