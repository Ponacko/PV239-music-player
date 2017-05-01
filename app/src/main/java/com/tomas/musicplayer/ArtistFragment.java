package com.tomas.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class ArtistFragment extends Fragment{
    private static ArtistFragment fragment;
    protected ListView list;
    protected ArtistAdapter artistList;
    private Realm realm;

    public ArtistFragment() {
        // Required empty public constructor
        fragment = this;
    }

    public static ArtistFragment getFragment(){
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        list = (ListView) view.findViewById(R.id.artistList);
        // Inflate the layout for this fragment
        return view;
    }

    public void update(){
        List<Artist> artists = realm.where(Artist.class).findAllSorted("name");
        artistList = new ArtistAdapter(getContext(), R.layout.artist_item, artists);
        artistList.notifyDataSetChanged();
        list.setAdapter(artistList);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        List<Artist> artists = realm.where(Artist.class).findAllSorted("name");
        artistList = new ArtistAdapter(getContext(), R.layout.artist_item, artists);
        artistList.notifyDataSetChanged();
        list.setAdapter(artistList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistList.getItem(position);
                SongFragment songFrag = SongFragment.getFragment();
                songFrag.updateByArtist(artist.getName());
            }
        });
    }

}