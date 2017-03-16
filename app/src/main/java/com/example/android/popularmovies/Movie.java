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
    private double mPopularity;
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
        mId = cursor.getLong(MovieEntry.COL_MOVIE_ID);
        mOriginalTitle = cursor.getString(MovieEntry.COL_ORIGINAL_TITLE);
        mMoviePoster = cursor.getString(MovieEntry.COL_POSTER_PATH);
        mOverview = cursor.getString(MovieEntry.COL_OVERVIEW);
        mVoteAverage = cursor.getDouble(MovieEntry.COL_VOTE_AVERAGE);
        mReleaseDate = cursor.getString(MovieEntry.COL_RELEASE_DATE);
        mPopularity = cursor.getDouble(MovieEntry.COL_POPULARITY);
        mBackdropPoster = cursor.getString(MovieEntry.COL_BACKDROP_PATH);
    }

    public Movie(long id, String originalTitle, String moviePoster, String overview,
                 double voteAverage, String releaseDate, String backdropPoster) {
        mId = id;
        mOriginalTitle = originalTitle;
        mMoviePoster = moviePoster;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mBackdropPoster = backdropPoster;
    }

    public Movie(Parcel parcel) {
        Movie movie = new Movie();
        movie.mId = parcel.readLong();
        movie.mOriginalTitle = parcel.readString();
        movie.mMoviePoster = parcel.readString();
        movie.mOverview = parcel.readString();
        movie.mVoteAverage = parcel.readDouble();
        movie.mReleaseDate = parcel.readString();
        movie.mBackdropPoster = parcel.readString();

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
