package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOST_POPULAR = "most_popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITES = "favorites";
    public static final String COLUMN_MOVIE_ID_KEY = "movie_id";

    /* Inner class that defines the table contents of the Movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        // Column names
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        private static final String[] COLUMNS = {
                _ID,
                COLUMN_ORIGINAL_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_OVERVIEW,
                COLUMN_USER_RATING,
                COLUMN_RELEASE_DATE,
                COLUMN_BACKDROP_PATH};

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);

        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class MostPopularMovies implements BaseColumns {

        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_MOST_POPULAR).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES + "/" +
                        PATH_MOST_POPULAR;

        public static final String TABLE_NAME = "most_popular_movies";

        private static final String[] COLUMNS = {
                _ID,
                COLUMN_MOVIE_ID_KEY};

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class TopRatedMovies implements BaseColumns {

        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES + "/" +
                        PATH_TOP_RATED;

        public static final String TABLE_NAME = "top_rated_movies";

        private static final String[] COLUMNS = {
                _ID,
                COLUMN_MOVIE_ID_KEY};

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class FavoriteMovies implements BaseColumns {

        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES + "/" +
                        PATH_FAVORITES;

        public static final String TABLE_NAME = "favorite_movies";

        private static final String[] COLUMNS = {
                _ID,
                COLUMN_MOVIE_ID_KEY};

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }
}

