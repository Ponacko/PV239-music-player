package com.tomas.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tomas on 28. 2. 2017.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(Context context, int resource,  List<Song> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.song_item, parent, false);
        }

        Song song = getItem(position);
        if (song!= null) {
            TextView text = (TextView)convertView.findViewById(R.id.songName);
            TextView artistText = (TextView)convertView.findViewById(R.id.artistName);
            text.setText(song.getTitle());
            artistText.setText(song.getArtist());
        }

        return convertView;
    }
}
