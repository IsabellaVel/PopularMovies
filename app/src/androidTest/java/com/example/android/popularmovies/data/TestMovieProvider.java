package com.example.android.popularmovies.data;

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

import com.example.android.popularmovies.data.MovieContract.FavoriteMovies;
import com.example.android.popularmovies.data.MovieContract.MostPopularMovies;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.TopRatedMovies;

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
        // content://com.example.android.popularmovies.app/movies
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmovies.app/movies
        assertEquals("Error: the MOVIES CONTENT URI should return MovieEntry.CONTENT_DIR_TYPE",
                MovieEntry.CONTENT_DIR_TYPE, type);

        long TEST_MOVIE_ID = 157821;
        // content://com.example.android.popularmovies.app/movies/157821
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(TEST_MOVIE_ID));
        // vnd.android.cursor.item/com.example.android.popularmovies.app/movies/157821
        assertEquals("Error: the MOVIE BY ID CONTENT URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.popularmovies.app/movies/most_popular
        type = mContext.getContentResolver().getType(MostPopularMovies.CONTENT_URI);
        assertEquals("Error: the MOST POPULAR MOVIES CONTENT URI should return MostPopularMovies.CONTENT_DIR_TYPE",
                MostPopularMovies.CONTENT_DIR_TYPE, type);

        // content://com.example.android.popularmovies.app/movies/top_rated
        type = mContext.getContentResolver().getType(TopRatedMovies.CONTENT_URI);
        assertEquals("Error: the HIGHEST RATED MOVIES CONTENT URI should return HighestRatedMovies.CONTENT_DIR_TYPE",
                TopRatedMovies.CONTENT_DIR_TYPE, type);

        // content://com.example.android.popularmovies.app/movies/favorites
        type = mContext.getContentResolver().getType(FavoriteMovies.CONTENT_URI);
        assertEquals("Error: the FAVORITES CONTENT URI should return MostRatedMovies.CONTENT_DIR_TYPE",
                FavoriteMovies.CONTENT_DIR_TYPE, type);

        assertTrue(mContext.getContentResolver().getType(INVALID_URI) == null);
    }

    public void testMoviesQuery() {
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

    public void testMovieByIdQuery() {
        ContentValues testValues = insertTestValues();
        long testMovieId = testValues.getAsLong(MovieEntry._ID);
        Uri testMovieUri = MovieEntry.buildMovieUri(testMovieId);

        Cursor movie = mContext.getContentResolver().query(
                testMovieUri,
                null,
                null,
                null,
                null
        );
        if (movie == null) {
            fail("Get empty cursor by querying movie by id.");
        }
        TestUtilities.validateCursor("Error by querying movie by id.", movie, testValues);
        assertEquals("Movie by ID query returned more than one entry. ", movie.getCount(), 1);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie by ID Query did not properly set NotificationUri",
                    movie.getNotificationUri(), testMovieUri);
        }
        movie.close();
    }

    public void testMostPopularMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MovieEntry._ID);
        insertSortTableTestValues(MostPopularMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                MostPopularMovies.CONTENT_URI,
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
                    MostPopularMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
    }

    public void testTopRatedMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MovieEntry._ID);
        insertSortTableTestValues(TopRatedMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                TopRatedMovies.CONTENT_URI,
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
                    TopRatedMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
    }

    public void testFavoriteMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MovieEntry._ID);
        insertSortTableTestValues(FavoriteMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
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
                    FavoriteMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
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

    public void testInsertMostPopularMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MostPopularMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MostPopularMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry.", movies, entryValues);

        movies.close();
    }

    public void testInsertTopRatedMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TopRatedMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(TopRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                TopRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry", movies, entryValues);

        movies.close();
    }


    public void testInsertFavoriteMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FavoriteMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(FavoriteMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry", movies, entryValues);

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
        updatedValues.put(MovieEntry.COLUMN_USER_RATING, 6.5);

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

    public void testDeleteMovieById() {
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

        mContext.getContentResolver().delete(
                MovieEntry.buildMovieUri(id),
                null,
                null
        );

        moviesObserver.waitForNotificationOrFail();
        movieByIdObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);
    }

    public void testDeleteMostPopularMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MostPopularMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                MostPopularMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MostPopularMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testDeleteTopRatedMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(TopRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TopRatedMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                TopRatedMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                TopRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testDeleteFavoriteMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(FavoriteMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FavoriteMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                FavoriteMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
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
            values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Test movie" + i);
            values.put(MovieEntry.COLUMN_OVERVIEW, "Test");
            values.put(MovieEntry.COLUMN_RELEASE_DATE, "13.04.2016");
            values.put(MovieEntry.COLUMN_USER_RATING, 1.2 + i);
            values.put(MovieEntry.COLUMN_POSTER_PATH, "http://example.com/" + i);
            values.put(MovieEntry.COLUMN_BACKDROP_PATH, "http://example.com/" + i);
            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    public void deleteAllRecordsFromProvider() {
        clearTableByUri(MovieEntry.CONTENT_URI);
        clearTableByUri(MostPopularMovies.CONTENT_URI);
        clearTableByUri(TopRatedMovies.CONTENT_URI);
        clearTableByUri(FavoriteMovies.CONTENT_URI);
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

    private void insertSortTableTestValues(String tableName, long movieId) {
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.COLUMN_MOVIE_ID_KEY, movieId);
        long id = db.insert(tableName, null, testValues);
        if (id == -1) {
            fail("Error by inserting contentValues into " + tableName);
        }
        db.close();
    }
}
