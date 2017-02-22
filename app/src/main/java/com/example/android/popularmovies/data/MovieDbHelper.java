package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.MostPopularMovies;
import com.example.android.popularmovies.data.MovieContract.TopRatedMovies;
import com.example.android.popularmovies.data.MovieContract.FavoriteMovies;

/**
 * Created by noahkim on 2/21/17.
 */

/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold movies
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL" +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL" +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
                MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL" +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL" +
                MovieEntry.BACKDROP_PATH + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_MOST_POPULAR_MOVIES_TABLE = "CREATE TABLE " + MostPopularMovies.TABLE_NAME + " (" +
                MostPopularMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " + " );";

        final String SQL_CREATE_TOP_RATED_MOVIES_TABLE = "CREATE TABLE" + TopRatedMovies.TABLE_NAME + " (" +
                TopRatedMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " + " );";

        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + FavoriteMovies.TABLE_NAME + " (" +
                FavoriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " + " );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_MOST_POPULAR_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TOP_RATED_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MostPopularMovies.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TopRatedMovies.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovies.TABLE_NAME);
        onCreate(db);
    }
}
