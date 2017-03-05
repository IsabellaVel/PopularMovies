package com.noahkim.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.noahkim.android.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    /* Inner class that defines the table contents of the Movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        // Column names
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        private static final String[] COLUMNS = {
                COLUMN_MOVIE_ID,
                COLUMN_ORIGINAL_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_OVERVIEW,
                COLUMN_VOTE_AVERAGE,
                COLUMN_RELEASE_DATE,
                COLUMN_BACKDROP_PATH};

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_ORIGINAL_TITLE = 1;
        public static final int COL_POSTER_PATH = 2;
        public static final int COL_OVERVIEW = 3;
        public static final int COL_VOTE_AVERAGE = 4;
        public static final int COL_RELEASE_DATE = 5;
        public static final int COL_BACKDROP_PATH = 6;
    }
}

