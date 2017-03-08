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

import static java.lang.Integer.parseInt;

/**
 * Created by Tomas on 27. 2. 2017.
 */

public class Song {
    private final String path;
    private final String title;
    private final String album;
    private final String artist;
    private final int trackNumber;
    private final int discNumber;
    private final int duration;
    private String artwork;
    private int elapsedTime;

    public Song(String path, MediaMetadataRetriever md) {
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
        return  "Missing artist";
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

    // Constant with a file name
    public static String fileName = "songList.ser";

    // Serializes an object and saves it to a file
    public void saveToFile(List<Song> obj, Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Creates an object by reading it from a file
    public static List<Song> readFromFile(Context context) {
        List<Song> songList = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            songList = (List<Song>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return songList;
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
