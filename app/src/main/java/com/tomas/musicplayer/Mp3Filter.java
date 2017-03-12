package com.tomas.musicplayer;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Tomas on 12. 3. 2017.
 */
class Mp3Filter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        return (name.endsWith(".mp3"));
    }
}
