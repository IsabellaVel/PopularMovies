package com.example.android.popularmovies.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.adapters.TrailersAdapter;
import com.example.android.popularmovies.pojo.Trailer;

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
 * Created by noahkim on 4/2/17.
 */

public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {
    private final Context mContext;
    private TrailersAdapter mTrailersAdapter;

    public static final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    public FetchTrailersTask(Context context, TrailersAdapter trailersAdapter) {
        mContext = context;
        mTrailersAdapter = trailersAdapter;
    }

    private static List<Trailer> getMovieTrailersFromJson(String movieJSON) throws JSONException, ParseException {

        final String TMDB_YOUTUBE = "youtube";
        final String TMDB_NAME = "name";
        final String TMDB_SIZE = "size";
        final String TMDB_SOURCE = "source";
        final String TMDB_TYPE = "type";

        // Create an empty ArrayList that we can start adding reviews to
        List<Trailer> trailers = new ArrayList<>();

        // Parse JSON response string
        try {

            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray = baseJsonResponse.getJSONArray(TMDB_YOUTUBE);

            // For each movie in the movieArray, create a Movie object
            for (int i = 0; i < movieArray.length(); i++) {
                String trailerName;
                String trailerSize;
                String trailerSource;
                String trailerType;

                // Get a single review at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                trailerName = currentMovie.getString(TMDB_NAME);
                trailerSize = currentMovie.getString(TMDB_SIZE);
                trailerSource = currentMovie.getString(TMDB_SOURCE);
                trailerType = currentMovie.getString(TMDB_TYPE);

                // Create a new Review object and add it to the list of reviews
                Trailer trailer = new Trailer(trailerName, trailerSize, trailerSource, trailerType);
                trailers.add(trailer);
            }
            Log.d(LOG_TAG, "FetchTrailersTask Complete");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
            e.printStackTrace();
        }
        return trailers;
    }

    @Override
    protected List<Trailer> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieId = params[0];

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        List<Trailer> trailers = null;

        try {
            final String APPID_PARAM = "api_key";

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieId)
                    .appendPath("trailers")
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
            trailers = getMovieTrailersFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null && mTrailersAdapter != null) {
            mTrailersAdapter.setMovieTrailers(trailers);
        }
        super.onPostExecute(trailers);
    }
}
