package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.data.MovieContract.FavoriteMovies;
import com.example.android.popularmovies.data.MovieContract.MostPopularMovies;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.TopRatedMovies;


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

    public void testGetIdFromUri() {
        Uri movieUri = MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertEquals("Error: Movie ID doesn't match expected result",
                MovieEntry.getIdFromUri(movieUri), TEST_MOVIE_ID);
    }

    public void testMostPopularMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MostPopularMovies.CONTENT_URI);
        assertEquals("Error: Most popular movies Uri doesn't match expected result",
                MostPopularMovies.CONTENT_URI.toString(),
                "content://com.example.android.popularmovies.app/movies/most_popular");
    }
    public void testTopRatedMoviesUri() {
        assertNotNull("Error: Null Uri returned.", TopRatedMovies.CONTENT_URI);
        assertEquals("Error: Most rated movies Uri doesn't match expected result",
                TopRatedMovies.CONTENT_URI.toString(),
                "content://com.example.android.popularmovies.app/movies/top_rated");
    }

    public void testFavoriteMoviesUri() {
        assertNotNull("Error: Null Uri returned.", FavoriteMovies.CONTENT_URI);
        assertEquals("Error: Most rated movies Uri doesn't match expected result",
                FavoriteMovies.CONTENT_URI.toString(),
                "content://com.example.android.popularmovies.app/movies/favorites");
    }
}
