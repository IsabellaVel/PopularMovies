package com.example.android.popularmovies;


import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createTestMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID, 10378);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Big Buck Bunny");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "Follow a day of the life of Big Buck Bunny when he meets three bullying rodents: Frank, Rinky, and Gamera. The rodents amuse themselves by harassing helpless creatures by throwing fruits, nuts and rocks at them. After the deaths of two of Bunny's favorite butterflies, and an offensive attack on Bunny himself, Bunny sets aside his gentle nature and orchestrates a complex plan for revenge.");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2008-05-30");
        movieValues.put(MovieEntry.COLUMN_USER_RATING, 6.5);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "https://image.tmdb.org/t/p/original/uVEFQvFMMsg4e6yb03xOfVsDz4o.jpg");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "https://image.tmdb.org/t/p/original/1O3tFuQsVgmjwx47xGKBjkSUiU6.jpg");
        return movieValues;
    }

    static ContentValues createConflictedMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID, 10378);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Another Title");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "muse themselves by harassing helpless creatures by throwing fruits, nuts and rocks at them. After the deaths of two of Bunny's favorite butterflies, and an offensive attack on Bunny himself, Bunny sets aside his gentle nature and orchestrates a complex plan for revenge.");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2025-05-30");
        movieValues.put(MovieEntry.COLUMN_USER_RATING, 5.7);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "https://image.tmdb.org/t/p/original/abc.jpg");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "https://image.tmdb.org/t/p/original/abcdef.jpg");
        return movieValues;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
