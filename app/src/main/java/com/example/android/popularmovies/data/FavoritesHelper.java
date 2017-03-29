package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;

/**
 * Created by noahkim on 3/16/17.
 */

public class FavoritesHelper {

    private final Context context;

    public FavoritesHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public void addToFavorites(Movie movie) {
        context.getContentResolver().insert(FavoritesEntry.CONTENT_URI, movie.toFaveContentValues());
        Log.d("DetailFragment", "Movie added to favorites");
    }

    public void deleteFromFavorites(long movieId) {
        int deleted = context.getContentResolver().delete(
                FavoritesEntry.buildMovieUri(movieId),
                null,
                null);
        Log.d("DetailFragment", "deleteFaveMovie: Deleted: " + deleted);
    }

    public boolean isFavorite(long movieId) {
        Cursor faveMovie = context.getContentResolver().query(
                FavoritesEntry.buildMovieUri(movieId),
                null,
                null,
                null,
                null);
        assert faveMovie != null;
        if (faveMovie.moveToNext()) {
            Log.d("DetailFragment", "Movie already in favorites");
            faveMovie.close();
            return true;
        } else {
            Log.d("DetailFragment", "Movie not in favorites");
            faveMovie.close();
            return false;
        }
    }
}
