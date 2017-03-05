package com.example.android.popularmovies;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by noahkim on 11/1/16.
 */

public class Movie implements Parcelable {
    private long mId;
    private String mOriginalTitle;
    private String mMoviePoster;
    private String mOverview;
    private double mVoteAverage;
    private String mReleaseDate;
    private String mBackdropPoster;

    private final String BASE_URL = "https://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE = "w185";
    private final String TMDB_TITLE = "original_title";
    private final String TMDB_POSTER = "poster_path";
    private final String TMDB_SYNOPSIS = "overview";
    private final String TMDB_USER_RATING = "vote_average";
    private final String TMDB_RELEASE_DATE = "release_date";

    private Movie() {
    }

    public Movie(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
        mOriginalTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE));
        mMoviePoster = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
        mOverview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
        mVoteAverage = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
        mReleaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
        mBackdropPoster = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH));
    }

    public Movie(Parcel parcel) {
        mId = parcel.readLong();
        mOriginalTitle = parcel.readString();
        mMoviePoster = parcel.readString();
        mOverview = parcel.readString();
        mVoteAverage = parcel.readDouble();
        mReleaseDate = parcel.readString();
        mBackdropPoster = parcel.readString();
    }

    public String getOriginalTitle() { return mOriginalTitle; }
    public String getMoviePosterURL() { return BASE_URL + IMAGE_SIZE + mMoviePoster; }
    public String getOverview() { return mOverview; }
    public double getVoteAverage() { return mVoteAverage; }
    public String getReleaseDate() { return mReleaseDate; }
    public String getBackdropPoster() { return mBackdropPoster; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mOverview);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mBackdropPoster);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new Movie[i];
        }
    };
}
