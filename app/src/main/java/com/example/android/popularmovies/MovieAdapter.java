package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Provides a view for GridView
     * @param position
     * @param convertView
     * @param parent
     * @return the view for the position in the GridView
     */

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the Movie object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        //Adapters recycle views to AdapterViews
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }

        ImageView posterImage = (ImageView) gridItemView.findViewById(R.id.poster_thumbnail);


        return gridItemView;
    }
}
