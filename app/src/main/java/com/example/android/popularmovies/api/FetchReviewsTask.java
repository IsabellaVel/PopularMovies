package com.example.android.popularmovies.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.adapters.ReviewsAdapter;
import com.example.android.popularmovies.pojo.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by noahkim on 3/3/17.
 */

public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

    private final Context mContext;
    private ReviewsAdapter mReviewsAdapter;

    public static final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    public FetchReviewsTask(Context context, ReviewsAdapter reviewsAdapter) {
        mContext = context;
        mReviewsAdapter = reviewsAdapter;
    }

    private static List<Review> getMovieReviewsFromJson(String movieJSON) throws JSONException, ParseException {

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";

        // Create an empty ArrayList that we can start adding reviews to
        List<Review> reviews = new ArrayList<>();

        // Parse JSON response string
        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray = baseJsonResponse.getJSONArray(TMDB_RESULTS);

            // For each movie in the movieArray, create a Movie object
            for (int i = 0; i < movieArray.length(); i++) {
                String reviewId;
                String reviewAuthor;
                String reviewContent;
                String reviewUrl;

                // Get a single review at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                reviewId = currentMovie.getString(TMDB_ID);
                reviewAuthor = currentMovie.getString(TMDB_AUTHOR);
                reviewContent = currentMovie.getString(TMDB_CONTENT);
                reviewUrl = currentMovie.getString(TMDB_URL);

                // Create a new Review object and add it to the list of reviews
                Review review = new Review(reviewId, reviewAuthor, reviewContent, reviewUrl);
                reviews.add(review);
            }
            Log.d(LOG_TAG, "FetchReviewsTask Complete");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the reviews JSON results", e);
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    protected List<Review> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieId = params[0];

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        List<Review> reviews = null;

        try {
            final String APPID_PARAM = "api_key";

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieId)
                    .appendPath("reviews")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                    .build();

            URL url = new URL(uri.toString());
            Log.d(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            reviews = getMovieReviewsFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null && mReviewsAdapter != null) {
            mReviewsAdapter.setMovieReviews(reviews);
        }
        super.onPostExecute(reviews);
    }

}
