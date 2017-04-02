package com.tomas.musicplayer;

import android.media.MediaPlayer;

/**
 * Created by Tomas on 2. 4. 2017.
 */

public class MpWrapper {
    private static MediaPlayer mp;
    public static MediaPlayer createMp(){
        if (mp == null){
            mp = new MediaPlayer();
        }
        return mp;
    }
}
