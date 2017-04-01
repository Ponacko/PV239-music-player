package com.tomas.musicplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Song currentSong = (Song)getIntent().getSerializableExtra("currentSong");
        TextView lyric = (TextView)findViewById(R.id.lyric);
        lyric.setText(currentSong.lyrics);
    }

}
