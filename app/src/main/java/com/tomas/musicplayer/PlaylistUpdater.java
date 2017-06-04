package com.tomas.musicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import android.support.v4.app.Fragment;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by Tomas on 16. 4. 2017.
 */

public class PlaylistUpdater extends AsyncTask<String, Integer, Void> {
    private List<String> songPaths = new ArrayList<>();
    private Set<Artist> artists = new HashSet<>();
    private List<Song> songs = new ArrayList<Song>();
    private MainActivity activity;
    private ProgressDialog progress;
    private int currentDirCount;
    private int dirCount;
    private File home;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(activity);
        progress.setMessage("Finding music");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCancelable(true);
        progress.setProgress(0);
        progress.show();
    }

    public void setContext(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        for (int i = 0; i < params.length; i++) {
            progress.setProgress(0);
            Realm realm = Realm.getDefaultInstance();

            home = new File(params[i]);

            final MediaMetadataRetriever md = new MediaMetadataRetriever();
            dirCount = home.listFiles(new DirFilter()).length;
            currentDirCount = 0;
            findSongs(home);
            for (int j = 0; j< songPaths.size(); j++) {
                Song song = new Song();
                song.init(songPaths.get(j), md);
                String artistName = song.getArtist();
                if (artistName != Song.NO_ARTIST) {
                    Artist artist = new Artist();
                    artist.init(artistName);
                    artists.add(artist);
                }
                songs.add(song);
                int progress = 10 + (int)((j / (float) songPaths.size())*85);
                publishProgress(progress);
            }
            realm.beginTransaction();
            List<Artist> realmArtists = realm.copyToRealm(artists);
            publishProgress(96);
            List<Song> realmSongs = realm.copyToRealm(songs);
            publishProgress(98);
            realm.commitTransaction();
            publishProgress(100);
        }
        return null;
    }

    private void findSongs(File folder) {
        File[] files = folder.listFiles(new Mp3Filter());
        if (files != null && files.length > 0) {
            for (File f : files) {
                songPaths.add(f.getAbsolutePath());
            }
        }
        File[] dirs = folder.listFiles(new DirFilter());
        if (dirs != null && dirs.length > 0) {
            for (File f : dirs) {
                findSongs(f);
                if (folder.equals(home)){
                    currentDirCount++;
                    publishProgress((int)((currentDirCount / (float) dirCount)*10));
                }

            }
        }
    }

    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        progress.setProgress(values[0]);
        //progress.show();



    }

    protected void onPostExecute(Void Result) {
        progress.dismiss();
        ArtistFragment artistFrag = ArtistFragment.getFragment();
        SongFragment songFrag = SongFragment.getFragment();
        artistFrag.update();
        songFrag.update();

    }



}
