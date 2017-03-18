package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;


/**
 * Created by noahkim on 2/21/17.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;
    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int FAVE_MOVIES = 102;
    static final int FAVE_MOVIES_WITH_ID = 103;

    //movies.movie_id = ?
    private static final String sMovieIdSelection =
            MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    //favorites.movie_id = ?
    private static final String sFaveMovieIdSelection =
            FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", FAVE_MOVIES_WITH_ID);
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
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            case FAVE_MOVIES:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVE_MOVIES_WITH_ID:
                retCursor = getFaveMovieById(uri, projection, sortOrder);
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
            case MOVIES_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case FAVE_MOVIES:
                return FavoritesEntry.CONTENT_DIR_TYPE;
            case FAVE_MOVIES_WITH_ID:
                return FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case MOVIES: {
                _id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVE_MOVIES: {
                _id = db.insertWithOnConflict(FavoritesEntry.TABLE_NAME, null, contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0)
                    returnUri = FavoritesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID:
                long id = MovieEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME,
                        sMovieIdSelection, new String[]{Long.toString(id)});
                break;
            case FAVE_MOVIES:
                rowsDeleted = db.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVE_MOVIES_WITH_ID:
                id = FavoritesEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(FavoritesEntry.TABLE_NAME,
                        sFaveMovieIdSelection, new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case FAVE_MOVIES:
                rowsUpdated = db.update(FavoritesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        mMovieDbHelper.close();
        super.shutdown();
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
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

    private Cursor getFaveMovieById(Uri uri, String[] projection, String sortOrder) {
        long id = FavoritesEntry.getIdFromUri(uri);
        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{Long.toString(id)};
        return mMovieDbHelper.getReadableDatabase().query(
                FavoritesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

}
