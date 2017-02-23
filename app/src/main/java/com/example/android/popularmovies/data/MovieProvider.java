package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.MovieContract.FavoriteMovies;
import com.example.android.popularmovies.data.MovieContract.MostPopularMovies;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.TopRatedMovies;

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
                        sortOrder);
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
            case MOST_POPULAR_MOVIES: {
                _id = db.insert(MostPopularMovies.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MostPopularMovies.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_RATED_MOVIES: {
                _id = db.insert(TopRatedMovies.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = TopRatedMovies.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_MOVIES: {
                _id = db.insert(FavoriteMovies.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FavoriteMovies.CONTENT_URI;
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
            case MOVIES_ID:
                long id = MovieEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, sMovieIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case MOST_POPULAR_MOVIES:
                rowsDeleted = db.delete(MostPopularMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED_MOVIES:
                rowsDeleted = db.delete(TopRatedMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIES:
                rowsDeleted = db.delete(FavoriteMovies.TABLE_NAME, selection, selectionArgs);
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
