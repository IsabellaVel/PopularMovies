package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.popularmovies.pojo.Movie;
import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;

/**
 * Created by noahkim on 3/16/17.
 */

public class FavoritesHelper {

    private final Context mContext;
    private static final String LOG_TAG = FavoritesHelper.class.getSimpleName();


    public FavoritesHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void addToFavorites(Movie movie) {
        mContext.getContentResolver().insert(FavoritesEntry.CONTENT_URI, movie.toFaveContentValues());
    }

    public void deleteFromFavorites(long movieId) {
        mContext.getContentResolver().delete(
                FavoritesEntry.buildMovieUri(movieId),
                null,
                null);
    }

    public boolean isFavorite(long movieId) {
        Cursor faveMovie = mContext.getContentResolver().query(
                FavoritesEntry.buildMovieUri(movieId),
                null,
                null,
                null,
                null);
        assert faveMovie != null;
        if (faveMovie.moveToNext()) {
            Log.d(LOG_TAG, "Movie stored in favorites");
            faveMovie.close();
            return true;
        } else {
            Log.d(LOG_TAG, "Movie not stored in favorites");
            faveMovie.close();
            return false;
        }
    }
}
