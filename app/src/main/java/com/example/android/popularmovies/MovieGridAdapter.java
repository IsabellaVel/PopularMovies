package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieGridAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieGridAdapter.class.getSimpleName();
    private final String BASE_URL = "http://image.tmdb.org/t/p/w185";

    ArrayList<Movie> mMovies;

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

        String moviePosterPath = cursor.getString(MovieEntry.COL_POSTER_PATH);
        ImageView posterView = (ImageView) view.findViewById(R.id.poster_thumbnail);
        Picasso
                .with(context)
                .load(BASE_URL + moviePosterPath)
                .into(posterView);
    }

//    public void add(Cursor cursor) {
//        mMovies.clear();
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                long id = cursor.getLong(MovieEntry.COL_MOVIE_ID);
//                String title = cursor.getString(MovieEntry.COL_ORIGINAL_TITLE);
//                String posterPath = cursor.getString(MovieEntry.COL_POSTER_PATH);
//                String overview = cursor.getString(MovieEntry.COL_OVERVIEW);
//                double voterAverage = cursor.getDouble(MovieEntry.COL_VOTE_AVERAGE);
//                String mReleaseDateView = cursor.getString(MovieEntry.COL_RELEASE_DATE);
//                String backdropPath = cursor.getString(MovieEntry.COL_BACKDROP_PATH);
//                Movie movie = new Movie(id, title, posterPath, overview, voterAverage,
//                        mReleaseDateView, backdropPath);
//                mMovies.add(movie);
//            } while (cursor.moveToNext());
//        }
//        notifyDataSetChanged();
//    }

}
