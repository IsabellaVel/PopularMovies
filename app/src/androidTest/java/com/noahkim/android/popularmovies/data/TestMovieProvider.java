package com.noahkim.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import com.noahkim.android.popularmovies.Movie;
import com.noahkim.android.popularmovies.data.MovieContract.MovieEntry;

public class TestMovieProvider extends AndroidTestCase {

    private static final Uri INVALID_URI = new Uri.Builder()
            .scheme("http")
            .authority("example.com")
            .appendPath("test")
            .build();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MoviesProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MoviesProvider registered with wrong authority " + providerInfo.authority,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.noahkim.android.popularmovies.app/movies
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.noahkim.android.popularmovies.app/movies
        assertEquals("Error: the MOVIES CONTENT URI should return MovieEntry.CONTENT_DIR_TYPE",
                MovieEntry.CONTENT_DIR_TYPE, type);

        assertTrue(mContext.getContentResolver().getType(INVALID_URI) == null);
    }

    public void testMovieQuery() {
        ContentValues testValues = insertTestValues();

        Cursor movies = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    movies.getNotificationUri(), MovieEntry.CONTENT_URI);
        }
        movies.close();

//        try {
//            mContext.getContentResolver().query(
//                    MovieEntry.CONTENT_URI,
//                    new String[]{"Invalid column"},
//                    null,
//                    null,
//                    null
//            );
//            fail();
//        } catch (IllegalArgumentException e) {
//            assertEquals("Unknown columns in projection.", e.getMessage());
//        }

        assertNull(
                mContext.getContentResolver().query(
                        INVALID_URI,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    public void testInsert() {
        ContentValues testValues = TestUtilities.createTestMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, moviesObserver);

        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);

        // Did our content observer get called?
        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);

        long movieRowId = ContentUris.parseId(movieUri);
        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        assertEquals(MovieEntry.buildMovieUri(movieRowId), movieUri);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        Cursor movies = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                movies, testValues);

        // Test replace police
        ContentValues conflictedValues = TestUtilities.createConflictedMovieValues();

        moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, conflictedValues);
        assertTrue(movieUri != null);

        movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);
        assertEquals(MovieEntry.buildMovieUri(movieRowId), movieUri);

        // Did our content observer get called?
        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        movieByIdObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);

        movies = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertTrue(movies != null);
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                movies, conflictedValues);
        movies.close();
    }

    public void testUpdateMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieId = ContentUris.parseId(movieUri);
        assertTrue(movieId != -1);
        assertEquals(movieUri, MovieEntry.buildMovieUri(movieId));

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MovieEntry._ID, movieId);
        updatedValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 6.5);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movies = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue(movies != null);

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        movies.registerContentObserver(moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues,
                MovieEntry._ID + "= ?", new String[]{Long.toString(movieId)});
        assertEquals(1, count);

        // Test to make sure our observer is called.
        moviesObserver.waitForNotificationOrFail();

        movies.unregisterContentObserver(moviesObserver);
        movies.close();

        movieByIdObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + movieId,
                null,   // Values for the "where" clause
                null    // sort order
        );
        assertTrue(cursor != null);
        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testDeleteAllMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long id = ContentUris.parseId(movieUri);
        assertTrue(id != -1);
        assertEquals(MovieEntry.buildMovieUri(id), movieUri);

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        deleteAllRecordsFromProvider();

        moviesObserver.waitForNotificationOrFail();
        movieByIdObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);
    }

    public void testBulkInsert() {
        deleteAllRecords();
        ContentValues[] bulkInsertContentValues = createBulkInsertValues();

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                MovieEntry.CONTENT_URI, true, moviesObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, bulkInsertContentValues);

        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor != null);
        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 25;

    static ContentValues[] createBulkInsertValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues values = new ContentValues();
            values.put(MovieEntry._ID, i);
            values.put(MovieEntry.COLUMN_MOVIE_ID, 10321);
            values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Test movie" + i);
            values.put(MovieEntry.COLUMN_OVERVIEW, "Test");
            values.put(MovieEntry.COLUMN_RELEASE_DATE, "13.04.2016");
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, 1.2 + i);
            values.put(MovieEntry.COLUMN_POSTER_PATH, "http://example.com/" + i);
            values.put(MovieEntry.COLUMN_BACKDROP_PATH, "http://example.com/" + i);
            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    public void deleteAllRecordsFromProvider() {
        clearTableByUri(MovieEntry.CONTENT_URI);
    }

    public void clearTableByUri(Uri uri) {
        mContext.getContentResolver().delete(
                uri,
                null,
                null
        );
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    private ContentValues insertTestValues() {
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTestMovieValues();
        long id = db.insert(MovieEntry.TABLE_NAME, null, testValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        db.close();
        return testValues;
    }
}
