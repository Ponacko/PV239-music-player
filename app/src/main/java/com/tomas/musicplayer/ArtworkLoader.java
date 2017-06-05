package com.tomas.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.LinearLayout;

import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import de.umass.lastfm.*;
import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Image;
import io.realm.Realm;

/**
 * Created by Golombiatko on 6/4/2017.
 */

public class ArtworkLoader extends AsyncTask<Object, Object, Object> {
    private Bitmap bitmap;
    private URL url;
    private Song song;
    private Realm realm;
    private String artwork;
    private PlayActivity pa;
    private String key = "4707e0238889a50ea5a420bee1939a8c";
    //API key to Last.fm

    public ArtworkLoader(PlayActivity pa) {
        this.pa = pa;
        realm = Realm.getDefaultInstance();
        final Current current = realm.where(Current.class).findFirst();
        if (current != null){
            Song s = realm.where(Song.class).equalTo("path", current.path).findFirst();
            this.song = realm.copyFromRealm(s);
            this.song.init(s.getPath(), new MediaMetadataRetriever());
        }
        this.artwork = song.getArtwork();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            Caller.getInstance().setUserAgent("tst");
            Caller.getInstance().setCache(null);

            String surl = null;
            if (song.getArtist() != Song.NO_ARTIST) {
                if (!(song.getAlbum() == null || song.getAlbum().isEmpty())) {
                    de.umass.lastfm.Album a = de.umass.lastfm.Album.getInfo(song.getArtist(), song.getAlbum(), key);
                    surl = a.getImageURL(ImageSize.LARGE);
                }
                else {
                    de.umass.lastfm.Artist a = Artist.getInfo(song.getArtist(), key);
                    surl = a.getImageURL(ImageSize.LARGE);
                }
            }
            else {
                /*Collection<Track> tracks = Track.search(null, artwork, 1, key);
                if (tracks != null && tracks.iterator().hasNext()) {
                    surl = tracks.iterator().next().getImageURL(ImageSize.LARGE);
                }*/
                Map<String, String> m = new HashMap<>();
                m.put("track", artwork);
                Result r = Caller.getInstance().call("track.search", key, m);
                surl = r.getContentElement().getChild("trackmatches").getChild("track").getChildren("image").get(2).getText();
            }
                Log.d("image url:", String.valueOf(surl));

                url = new URL(surl);
                song.setArtLoaded(true);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception ex) {
            //Log.d("error code", Caller.getInstance().getLastResult().getErrorMessage());
            //Log.d("except", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        if(song.isArtLoaded() && !ImageStorage.checkifImageExists(song.getArtwork()) && bitmap != null) {
            Log.d("artwork to be saved", artwork);
            ImageStorage.save(bitmap, artwork);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(song);
            realm.commitTransaction();
        }
    }
}
