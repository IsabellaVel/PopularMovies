package com.noahkim.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieAdapter extends CursorAdapter {

    Movie movie;
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Adapters recycle views to AdapterViews
        View gridItemView = LayoutInflater.from(context).inflate(
                R.layout.grid_item_posters, parent, false);

        ImageView posterImage = (ImageView) gridItemView.findViewById(R.id.poster_thumbnail);
        Picasso.with(context).load(movie.getMoviePosterURL()).into(posterImage);
        Log.v(LOG_TAG, "Setting image " + movie.getMoviePosterURL());

        return gridItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // read movie ID from cursor

    }
}
