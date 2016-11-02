package com.example.android.popularmovies;

/**
 * Created by noahkim on 11/1/16.
 */

public class Movies {
    String movieTitle;
    String moviePoster;
    String plotSynopsis;
    int userRating;
    String releaseDate;

    public Movies(String mMovieTitle, String mMoviePoster, String mPlotSynopsis,
                  int mUserRating, String mReleaseDate) {

        movieTitle = mMovieTitle;
        moviePoster = mMoviePoster;
        plotSynopsis = mPlotSynopsis;
        userRating = mUserRating;
        releaseDate = mReleaseDate;
    }

    public String getMovieTitle() { return movieTitle; }
    public String getMoviePoster() { return moviePoster; }
    public String getPlotSynopsis() { return plotSynopsis; }
    public int getUserRating() { return userRating; }
    public String getReleaseDate() { return releaseDate; }

}
