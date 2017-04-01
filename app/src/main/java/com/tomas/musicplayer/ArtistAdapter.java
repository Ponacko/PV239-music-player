package com.tomas.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tomas on 13. 3. 2017.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    public ArtistAdapter(Context context, int resource, List<Artist> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.artist_item, parent, false);
        }

        Artist artist = getItem(position);
        if (artist!= null) {
            TextView text = (TextView)convertView.findViewById(R.id.songName);
            text.setText(artist.getName());
        }

        return convertView;
    }
}
