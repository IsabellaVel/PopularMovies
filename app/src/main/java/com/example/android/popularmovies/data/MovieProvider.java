package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.MostPopularMovies;
import com.example.android.popularmovies.data.MovieContract.TopRatedMovies;
import com.example.android.popularmovies.data.MovieContract.FavoriteMovies;

/**
 * Created by noahkim on 2/21/17.
 */

public class MovieProvider extends ContentProvider {
    
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIES_ID = 101;
    private static final int MOST_POPULAR_MOVIES = 201;
    private static final int TOP_RATED_MOVIES = 202;
    private static final int FAVORITE_MOVIES = 300;

    private static final String sMovieIdSelection =
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ? ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIES_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_MOST_POPULAR, MOST_POPULAR_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_TOP_RATED, TOP_RATED_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_FAVORITES, FAVORITE_MOVIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIES_ID:
                retCursor = getMoviesFromId(uri, projection, sortOrder);
                break;
            case MOST_POPULAR_MOVIES:
                 retCursor = getMoviesFromReferenceTable(MostPopularMovies.TABLE_NAME,
                         projection, selection, selectionArgs, sortOrder);
                break;
            case TOP_RATED_MOVIES:
                retCursor = getMoviesFromReferenceTable(TopRatedMovies.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case FAVORITE_MOVIES:
                retCursor = getMoviesFromReferenceTable(FavoriteMovies.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_DIR_TYPE;
            case MOVIES_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOST_POPULAR_MOVIES:
                return MostPopularMovies.CONTENT_DIR_TYPE;
            case TOP_RATED_MOVIES:
                return TopRatedMovies.CONTENT_DIR_TYPE;
            case FAVORITE_MOVIES:
                return FavoriteMovies.CONTENT_DIR_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Cursor getMoviesFromId(Uri uri, String[] projection, String sortOrder) {
        long id = MovieEntry.getIdFromUri(uri);
        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{Long.toString(id)};
        return mMovieDbHelper.getReadableDatabase().query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesFromReferenceTable(String table, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // table INNER JOIN movies ON table.movie_id = movies._id
        queryBuilder.setTables(
                table + " INNER JOIN " + MovieEntry.TABLE_NAME +
                        " ON " + table + "." + MovieContract.COLUMN_MOVIE_ID_KEY +
                        " = " + MovieEntry.TABLE_NAME + "." + MovieEntry._ID
        );

        return queryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
