package com.tomas.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static java.lang.Integer.parseInt;

/**
 * Created by Tomas on 27. 2. 2017.
 */

public class Song extends RealmObject{
    public static String NO_ARTIST = "Missing artist";
    @PrimaryKey
    private String path;
    private String title;
    private String album;
    private String artist;
    private int trackNumber;
    private int discNumber;
    private int duration;
    private String artwork;
    public String lyrics;
    private int elapsedTime;

    public Song() {

    }

    public void init(String path, MediaMetadataRetriever md){
        md.setDataSource(path);
        this.path = path;
        this.title =
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        this.album =
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        this.artist =
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        this.trackNumber = parseInt(
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
        this.duration = parseInt(
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        String discNumberStr =
                md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
        if (discNumberStr != null) {
            this.discNumber = parseInt(discNumberStr);
        } else {
            this.discNumber = -1;
        }
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        if (title== null){
            int i = path.lastIndexOf('/');
            return  path.substring(i+1);
        }
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        if (artist== null){
        return  NO_ARTIST;
    }
        return artist;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public int getDuration() {
        return duration;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int time) {
        this.elapsedTime = time;
    }

    private static int parseInt(String intish) {
        if (intish == null){
            return -1;
        }
        int idx = intish.indexOf("/");
        if (idx == -1) {
            idx = intish.indexOf("-");
        }
        return Integer.parseInt(
                (idx >= 0) ? intish.substring(0, idx) : intish);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Song)){
            return false;
        }
        Song s = (Song)obj;
        return s.getPath().equals(getPath());
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
