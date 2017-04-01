package com.tomas.musicplayer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Tomas on 31. 3. 2017.
 */

public interface LololyricsService {
    @GET("{artist}/{title}")
    Call<Lyrics> getLyrics(@Path("artist") String artist, @Path("title") String title);
}
