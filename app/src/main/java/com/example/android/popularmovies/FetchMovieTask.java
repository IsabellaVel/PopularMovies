package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

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
import java.util.List;
import java.util.Vector;

/**
 * Created by noahkim on 3/3/17.
 */

public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

    private String sortOrder;
    private final Context mContext;

    public static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private void getMovieDataFromJson(String movieJSON) throws JSONException, ParseException {

        final String TMDB_RESULTS = "results";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_MOVIE_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_BACKDROP_PATH = "backdrop_path";

        // Parse JSON response string
        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray = baseJsonResponse.getJSONArray(TMDB_RESULTS);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            // For each movie in the movieArray, create a Movie object
            for (int i = 0; i < movieArray.length(); i++) {
                String originalTitle;
                long movieId;
                String posterPath;
                String overview;
                double voteAverage;
                String releaseDate;
                double popularity;
                String backdropPath;

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                originalTitle = currentMovie.getString(TMDB_ORIGINAL_TITLE);
                movieId = currentMovie.getLong(TMDB_MOVIE_ID);
                posterPath = currentMovie.getString(TMDB_POSTER_PATH);
                overview = currentMovie.getString(TMDB_OVERVIEW);
                voteAverage = currentMovie.getDouble(TMDB_VOTE_AVERAGE);
                releaseDate = currentMovie.getString(TMDB_RELEASE_DATE);
                popularity = currentMovie.getDouble(TMDB_POPULARITY);
                backdropPath = currentMovie.getString(TMDB_BACKDROP_PATH);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
                movieValues.put(MovieEntry.COLUMN_SORT_CRITERIA, sortOrder);
                cVVector.add(movieValues);
            }

            // add to database
            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
            e.printStackTrace();
        }
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        sortOrder = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {
            final String APPID_PARAM = "api_key";

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(sortOrder)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                    .build();

            URL url = new URL(uri.toString());

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
            getMovieDataFromJson(movieJsonStr);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
        return null;
    }
}
