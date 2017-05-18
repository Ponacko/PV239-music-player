package com.tomas.musicplayer;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;

/**
 * Created by Golombiatko on 4/30/2017.
 */

public class Player {
    private List<Song> playlist;
    private Song currentSong;
    final MediaPlayer mp = MpWrapper.createMp();
    private static Player player;
    private MainActivity activity;

    public Player(MainActivity activity) {
        player = this;
        this.activity = activity;
    }

    public static Player getPlayer() {
        return player;
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Song> playlist) {
        this.playlist = playlist;
    }

    public void setCurrentSong(Song s) {
        currentSong = s;
    }

    public Song getCurrentSong() {
        return currentSong;
    }



    public void Play() {
        try {
            if (!mp.isPlaying() && currentSong!= null){
                mp.reset();
                mp.setDataSource(currentSong.getPath());
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mp.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Pause() {
        if(mp.isPlaying()) {
            mp.pause();
        }
    }

    public void SwitchToNext() {
        Pause();
        currentSong = playlist.get((playlist.indexOf(currentSong) + 1) % playlist.size());
        activity.switchCurrentSong(currentSong);
        Play();
    }

    public void SwitchToPrevious() {
        Pause();
        currentSong = playlist.get((playlist.indexOf(currentSong) + playlist.size() - 1) % playlist.size());
        activity.switchCurrentSong(currentSong);
        Play();
    }

    /*public void RewindForward() {

    }

    public void RewindBackward() {

    }*/


}
