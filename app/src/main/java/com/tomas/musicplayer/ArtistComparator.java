package com.tomas.musicplayer;

import java.util.Comparator;

/**
 * Created by Tomas on 13. 3. 2017.
 */

public class ArtistComparator implements Comparator<Artist> {
    @Override
    public int compare(Artist o1, Artist o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
