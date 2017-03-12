package com.tomas.musicplayer;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tomas on 12. 3. 2017.
 */

public class Artist extends RealmObject {
    @PrimaryKey
    private String name;

    public Artist(){

    }

    public void init(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return name != null ? name.equals(artist.name) : artist.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
