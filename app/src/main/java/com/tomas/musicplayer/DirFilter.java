package com.tomas.musicplayer;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Tomas on 12. 3. 2017.
 */
class DirFilter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        return (file.isDirectory());
    }
}
