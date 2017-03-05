package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieGridAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieGridAdapter.class.getSimpleName();

    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(
                R.layout.grid_item_posters, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int posterColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String moviePosterPath = cursor.getString(posterColumnIndex);

        ImageView posterImage = (ImageView) view.findViewById(R.id.poster_thumbnail);
        Picasso
                .with(context)
                .load("https://image.tmdb.org/t/p/w185/" + moviePosterPath)
                .into(posterImage);
//        Log.v(LOG_TAG, "Setting image " + movie.getMoviePosterURL());


    }
}
