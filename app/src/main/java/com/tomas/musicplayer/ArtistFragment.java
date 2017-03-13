package com.tomas.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import io.realm.RealmResults;


public class ArtistFragment extends Fragment{
    protected ListView list;
    protected ArtistAdapter artistList;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        List<Artist> artists = ((MainActivity)getActivity()).realm.where(Artist.class).findAll();
        artistList = new ArtistAdapter(getContext(), R.layout.artist_item, artists);
        list.setAdapter(artistList);
        //list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           // @Override
            //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  ((MainActivity)getActivity()).play(position);
            //}
        //});

    }

}