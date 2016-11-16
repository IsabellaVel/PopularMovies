package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by noahkim on 11/1/16.
 */

public class Movie implements Parcelable {
    private String movieTitle;
    private String moviePoster;
    private String plotSynopsis;
    private double userRating;
    private Date releaseDate;

    private final String BASE_URL = "https://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE = "w185";
    private final String TMDB_TITLE = "original_title";
    private final String TMDB_POSTER = "poster_path";
    private final String TMDB_SYNOPSIS = "overview";
    private final String TMDB_USER_RATING = "vote_average";
    private final String TMDB_RELEASE_DATE = "release_date";



    public Movie(JSONObject data) throws JSONException, ParseException {

        movieTitle = data.getString(TMDB_TITLE);
        moviePoster = data.getString(TMDB_POSTER);
        plotSynopsis = data.getString(TMDB_SYNOPSIS);
        userRating = data.getDouble(TMDB_USER_RATING);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        releaseDate = dateFormat.parse(data.getString(TMDB_RELEASE_DATE));
    }

    private Movie(Parcel parcel) {
        movieTitle = parcel.readString();
        moviePoster = parcel.readString();
        plotSynopsis = parcel.readString();
        userRating = parcel.readDouble();
        releaseDate = new Date(parcel.readLong());
    }

    public String getMovieTitle() { return movieTitle; }
    public String getMoviePosterURL() { return BASE_URL + IMAGE_SIZE + moviePoster; }
    public String getPlotSynopsis() { return plotSynopsis; }
    public double getUserRating() { return userRating; }
    public Date getReleaseDate() { return releaseDate; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTitle);
        parcel.writeString(moviePoster);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(userRating);
        parcel.writeLong(releaseDate.getTime());
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
