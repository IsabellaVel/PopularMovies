package com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by noahkim on 11/1/16.
 */

public class Movie implements Parcelable {
    private long mId;
    private String mOriginalTitle;
    private String mMoviePoster;
    private String mOverview;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mPopularity;
    private String mBackdropPoster;
    private String mSortCriteria;
    private final String BASE_URL = "https://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE = "w185";

    private Movie() {
    }

    public Movie(Cursor cursor) {
        mId = cursor.getLong(MovieEntry.COL_MOVIE_ID);
        mOriginalTitle = cursor.getString(MovieEntry.COL_ORIGINAL_TITLE);
        mMoviePoster = cursor.getString(MovieEntry.COL_POSTER_PATH);
        mOverview = cursor.getString(MovieEntry.COL_OVERVIEW);
        mVoteAverage = cursor.getString(MovieEntry.COL_VOTE_AVERAGE);
        mReleaseDate = cursor.getString(MovieEntry.COL_RELEASE_DATE);
        mPopularity = cursor.getString(MovieEntry.COL_POPULARITY);
        mBackdropPoster = cursor.getString(MovieEntry.COL_BACKDROP_PATH);
        mSortCriteria = cursor.getString(MovieEntry.COL_SORT_CRITERIA);
    }

//    public Movie(long id, String originalTitle, String moviePoster, String overview,
//                 String voteAverage, String releaseDate, String popularity,
//                 String backdropPoster, String sortCriteria) {
//        mId = id;
//        mOriginalTitle = originalTitle;
//        mMoviePoster = moviePoster;
//        mOverview = overview;
//        mVoteAverage = voteAverage;
//        mReleaseDate = releaseDate;
//        mPopularity = popularity;
//        mBackdropPoster = backdropPoster;
//        mSortCriteria = sortCriteria;
//    }

    public ContentValues toFaveContentValues() {
        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_MOVIE_ID, mId);
        values.put(FavoritesEntry.COLUMN_ORIGINAL_TITLE, mOriginalTitle);
        values.put(FavoritesEntry.COLUMN_OVERVIEW, mOverview);
        values.put(FavoritesEntry.COLUMN_RELEASE_DATE, mReleaseDate);
        values.put(FavoritesEntry.COLUMN_POSTER_PATH, mMoviePoster);
        values.put(FavoritesEntry.COLUMN_POPULARITY, mPopularity);
        values.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
        values.put(FavoritesEntry.COLUMN_BACKDROP_PATH, mBackdropPoster);
        values.put(FavoritesEntry.COLUMN_SORT_CRITERIA, mSortCriteria);
        return values;
    }

    public Movie(Parcel parcel) {
        Movie movie = new Movie();
        movie.mId = parcel.readLong();
        movie.mOriginalTitle = parcel.readString();
        movie.mMoviePoster = parcel.readString();
        movie.mOverview = parcel.readString();
        movie.mVoteAverage = parcel.readString();
        movie.mReleaseDate = parcel.readString();
        movie.mPopularity = parcel.readString();
        movie.mBackdropPoster = parcel.readString();
        movie.mSortCriteria = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mOverview);
        parcel.writeString(mVoteAverage);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mPopularity);
        parcel.writeString(mBackdropPoster);
        parcel.writeString(mSortCriteria);

    }

    public long getMovieId() {return mId;}
    public String getOriginalTitle() { return mOriginalTitle; }
    public String getMoviePosterURL() { return BASE_URL + IMAGE_SIZE + mMoviePoster; }
    public String getOverview() { return mOverview; }
    public String getVoteAverage() { return mVoteAverage; }
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
