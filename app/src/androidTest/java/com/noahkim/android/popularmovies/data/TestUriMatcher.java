package com.noahkim.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.noahkim.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by noahkim on 2/24/17.
 */

public class TestUriMatcher {
    private static final long TEST_MOVIE_ID = 515320;

    // content://comp.noahkim.android.popularmovies.app
    private static final Uri TEST_BASE_URI = MovieContract.BASE_CONTENT_URI;
    // content://comp.noahkim.android.popularmovies.app/movies
    private static final Uri TEST_MOVIES_URI = MovieEntry.CONTENT_URI;

    @Test
    public void testUriMatcher() {
        UriMatcher uriMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The BASE CONTENT URI was matched incorrectly.",
                UriMatcher.NO_MATCH, uriMatcher.match(TEST_BASE_URI));
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                MovieProvider.MOVIES, uriMatcher.match(TEST_MOVIES_URI));
    }
}
