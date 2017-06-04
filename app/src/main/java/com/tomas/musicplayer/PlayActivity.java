package com.tomas.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayActivity extends AppCompatActivity {
    private Song currentSong;
    private Button forwardButton, backwardButton, playButton;
    private Button imageButton;
    private TextView currentTimeText, durationTimeText;
    private TextView lyric;
    private SeekBar seekBar;
    private int currentTime;
    private int durationTime;
    private static final int skipSeconds = 5000;
    private static final String pauseSymbol = "❚❚";
    private static final String playSymbol = "▶";
    final MediaPlayer mp = MpWrapper.createMp();
    final Player player = Player.getPlayer();
    private ImageView image;
    private String imageURL;
    private Realm realm;

    private LinearLayout picture;

    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        picture = (LinearLayout) findViewById(R.id.centralArea);
        realm = Realm.getDefaultInstance();
        Current current = realm.where(Current.class).findFirst();
        currentSong = realm.where(Song.class).equalTo("path", current.path).findFirst();
        player.setCurrentSong(currentSong);

        currentSong = player.getCurrentSong();

        lyric = (TextView)findViewById(R.id.lyric);
        lyric.setText(currentSong.getLyrics());
        setTitle(currentSong.getTitle());
        getSupportActionBar().setTitle(currentSong.getTitle());
        setSupportActionBar(toolbar);

        currentTimeText = (TextView) findViewById(R.id.currentTimeText);
        durationTimeText = (TextView) findViewById(R.id.durationTimeText);

        imageButton = (Button) findViewById(R.id.imageButton);
        image = (ImageView) findViewById(R.id.image);
        forwardButton = (Button) findViewById(R.id.forwardButton);
        backwardButton = (Button)findViewById(R.id.backwardButton);
        playButton = (Button)findViewById(R.id.playButton);
        if (mp.isPlaying())
            playButton.setText(pauseSymbol);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        updateLyrics();
        updateImage();

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

        final GestureDetector gestureDetector = new GestureDetector(this, new SingleTapConfirm());


        forwardButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    player.SwitchToNext();
                    currentSong = player.getCurrentSong();
                    playButton.setText(pauseSymbol);
                    updateLyrics();
                    updateImage();
                    updateDuration();
                    getSupportActionBar().setTitle(currentSong.getTitle());
                    //ugly fix yet to be improved
                    player.setCurrentSong(currentSong);
                    player.Play();
                } else {
                    // code for move and drag
                    mp.seekTo(mp.getCurrentPosition() + 100);
                }
                return false;
            }
        });

        backwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    player.SwitchToPrevious();
                    currentSong = player.getCurrentSong();
                    playButton.setText(pauseSymbol);
                    updateLyrics();
                    updateImage();
                    updateDuration();
                    getSupportActionBar().setTitle(currentSong.getTitle());
                    //ugly fix yet to be improved
                    player.setCurrentSong(currentSong);
                    player.Play();
                } else {
                    // code for move and drag
                    mp.seekTo(mp.getCurrentPosition() - 100);
                }
                return false;
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

    private void updateImage() {
        picture.setBackgroundColor(0x00000000);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(currentSong.getPath());
        byte[] pic = mmr.getEmbeddedPicture();
        if (pic != null) {
            //Log.d("pic: ", "is in metadata");
            //pic is in metadata
            Bitmap bm = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            BitmapDrawable background = new BitmapDrawable(getResources(), bm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                picture.setBackground(background);
            }
        }
        else {
            realm = Realm.getDefaultInstance();
            final Current current = realm.where(Current.class).findFirst();
            Song s = realm.where(Song.class).equalTo("path", current.path).findFirst();
            Song song = realm.copyFromRealm(s);
            song.init(s.getPath(), mmr);
            if (!song.getArtwork().isEmpty() && ImageStorage.checkifImageExists(song.getArtwork())) {
                //Log.d("pic: ", "is sure in file");
                //Log.d("artwork ", song.getArtwork());
                File file = ImageStorage.getImage("/" + song.getArtwork() + ".jpg");
                String path = file.getAbsolutePath();
                if (path != null) {
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    BitmapDrawable background = new BitmapDrawable(getResources(), bm);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        picture.setBackground(background);
                    }
                }
            } else if (!song.isArtLoaded()) {
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    //Log.d("pic: ", "no pic, try to download, save and show");
                    ArtworkLoader ir = new ArtworkLoader(this);
                    ir.execute();
                    if (!song.getArtwork().isEmpty() && ImageStorage.checkifImageExists(song.getArtwork())) {
                        File file = ImageStorage.getImage("/" + song.getArtwork() + ".jpg");
                        String path = file.getAbsolutePath();
                        if (path != null) {
                            Bitmap bm = BitmapFactory.decodeFile(path);
                            BitmapDrawable background = new BitmapDrawable(getResources(), bm);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                picture.setBackground(background);
                            }
                        }
                    }
                }
            }
            else {
                //picture.setBackgroundColor(0x00000000);
            }
        }

    }

    private void updateLyrics() {
        if ((currentSong.getLyrics().isEmpty())
                && currentSong.getArtist() != Song.NO_ARTIST) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://lyric-api.herokuapp.com/api/find/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HerokuappService service = retrofit.create(HerokuappService.class);
                Call<Lyrics> lyricsCall = service.getLyrics(currentSong.getArtist(), currentSong.getTitle());
                lyricsCall.enqueue(new Callback<Lyrics>() {
                    @Override
                    public void onResponse(Call<Lyrics> call, Response<Lyrics> response) {
                        if (response.body() != null){
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            currentSong.setLyrics(response.body().lyric);
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
        lyric.setText(currentSong.getLyrics());
    }


    private void updateDuration() {
        durationTime = mp.getDuration();
        durationTimeText.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) durationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) durationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationTime)))
        );
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

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }



}
