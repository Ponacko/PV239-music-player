package com.tomas.musicplayer;

import android.media.MediaMetadataRetriever;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static java.lang.Integer.parseInt;

/**
 * Created by Tomas on 27. 2. 2017.
 */

public class Song extends RealmObject{
    public static String NO_ARTIST = "Missing artist";
    private boolean artLoaded = false;
    @PrimaryKey
    private String path;
    private String title;
    private String album;
    private String artist;
    private int trackNumber;
    private int discNumber;
    private int duration;
    private String artwork; //name of image file
    private String lyrics;
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

        if (artist != null) {
            if (album != null) {
                this.artwork = artist + "-" + album;
            }
            else {
                this.artwork = artist;
            }
        }
        else {
            this.artwork = this.getTitle();
        }

        //this.trackNumber = parseInt(
          //      md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));


    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        String t = "";
        if (title == null){
            int i = path.lastIndexOf('/');
            t =  path.substring(i+1);

            if (t.endsWith(".mp3")) {
                t = t.substring(0, t.length()-4);
            }
            return t;
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

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        if (lyrics==null)
            return "";
        else
            return lyrics;
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
        if (artwork == null)
            return "";
        else
            return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public void setArtLoaded(boolean bool) {
        this.artLoaded = bool;
    }

    public boolean isArtLoaded() {
        return artLoaded;
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
