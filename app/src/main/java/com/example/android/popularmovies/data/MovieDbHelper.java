package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by noahkim on 2/21/17.
 */

/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create a table to hold movies
    final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY, " +
            MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
            MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL " + " );";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
