package com.tomas.musicplayer;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tomas on 12. 3. 2017.
 */

public class Album extends RealmObject {
    private RealmList<Song> songs;
    private String artist;
    private String name;
    public Album(){

    }

    public void init(String artist, String name){
        this.artist = artist;
        this.name = name;
    }



}
