package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 157821;

    public void testBaseContentUri() {
        assertNotNull("Error: Null Uri returned.", MovieContract.BASE_CONTENT_URI);
        assertEquals("Error: Base Content Uri doesn't match expected result",
                MovieContract.BASE_CONTENT_URI.toString(),
                "content://com.example.android.popularmovies.app");
    }

    public void testMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MovieEntry.CONTENT_URI);
        assertEquals("Error: Movies Uri doesn't match expected result",
                MovieEntry.CONTENT_URI.toString(),
                "content://com.example.android.popularmovies.app/movies");
    }

    public void testBuildMovieUri() {

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned.", movieUri);

        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("Error: Movie Uri doesn't match expected result",
                movieUri.toString(),
                "content://com.example.android.popularmovies.app/movies/157821");
    }
}
