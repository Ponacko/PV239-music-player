package com.tomas.musicplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PlayActivity extends AppCompatActivity {
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);

        Realm realm = Realm.getDefaultInstance();
        String currentSongPath = (String) getIntent().getSerializableExtra("currentSongPath");
        currentSong = realm.where(Song.class).equalTo("path", currentSongPath).findFirst();
        TextView lyric = (TextView)findViewById(R.id.lyric);
        lyric.setText(currentSong.lyrics);
        setTitle(currentSong.getTitle());
        getSupportActionBar().setTitle(currentSong.getTitle());
    }

}
