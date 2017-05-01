package com.tomas.musicplayer;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Tomas on 13. 3. 2017.
 */

public class SongFragment extends Fragment{
    protected ListView list;
    protected SongAdapter songList;
    private Realm realm;
    private static SongFragment fragment;

    public SongFragment(){
        fragment = this;
    }

    public static SongFragment getFragment(){
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        list = (ListView) view.findViewById(R.id.songList);
        // Inflate the layout for this fragment
        return view;
    }

    public void update(){
        List<Song> songs = realm.where(Song.class).findAll();
        songList = new SongAdapter(getContext(), R.layout.song_item, songs);
        list.setAdapter(songList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.getItem(position);
                ((MainActivity)getActivity()).play(song);
            }
        });
    }

    public void updateByArtist(String artist) {
        final List<Song> songs = realm.where(Song.class).equalTo("artist", artist).findAll();
        Player.getPlayer().setPlaylist(songs);
        songList = new SongAdapter(getContext(), R.layout.song_item, songs);
        list.setAdapter(songList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.getItem(position);
                ((MainActivity)getActivity()).setSongs(songs);
                ((MainActivity)getActivity()).play(song);
            }
        });
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        List<Song> songs = realm.where(Song.class).findAll();
        songList = new SongAdapter(getContext(), R.layout.song_item, songs);
        songList.notifyDataSetChanged();
        list.setAdapter(songList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.getItem(position);
                ((MainActivity)getActivity()).play(song);
            }
        });
    }

}

