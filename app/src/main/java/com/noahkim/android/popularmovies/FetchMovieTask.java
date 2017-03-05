package com.noahkim.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.noahkim.android.popularmovies.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

/**
 * Created by noahkim on 3/3/17.
 */

public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

    private MovieAdapter mMovieAdapter;
    private final Context mContext;

    public static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public FetchMovieTask(Context context, MovieAdapter adapter) {
        mContext = context;
        mMovieAdapter = adapter;
    }

    private List<Movie> getMovieDataFromJson(String movieJSON) throws JSONException, ParseException {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        final String TMDB_RESULTS = "results";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_MOVIE_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
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
                String backdropPath;

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                originalTitle = currentMovie.getString(TMDB_ORIGINAL_TITLE);
                movieId = currentMovie.getLong(TMDB_MOVIE_ID);
                posterPath = currentMovie.getString(TMDB_POSTER_PATH);
                overview = currentMovie.getString(TMDB_OVERVIEW);
                voteAverage = currentMovie.getDouble(TMDB_VOTE_AVERAGE);
                releaseDate = currentMovie.getString(TMDB_RELEASE_DATE);
                backdropPath = currentMovie.getString(TMDB_BACKDROP_PATH);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);

                cVVector.add(movieValues);
            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            // display what is stored in bulkInsert
            Cursor cur = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null,
                    null, null, null);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + cVVector.size() + " Inserted");

            // TODO: return the content values in a readable object

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Query theMovieDB and return a list of {@link Movie} objects
     *
     * @param requestUrl
     * @return movies
     */
    public List<Movie> fetchMovieData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list
        List<Movie> movies = null;
        try {
            movies = getMovieDataFromJson(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the list of {@link Movie}
        return movies;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0)
            return null;

        String sortOrder = params[0];

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortOrder)
                .appendQueryParameter("api_key", BuildConfig.OPEN_MOVIE_API_KEY);

        String movie_url = builder.build().toString();

        List<Movie> movies = fetchMovieData(movie_url);
        return movies;
    }

//    @Override
//    protected void onPostExecute(List<Movie> movies) {
//        if (movies != null && mMovieAdapter != null) {
//            mMovieAdapter.clear();
//            for (String movieStr : movies) {
//                mMovieAdapter.add(movieStr);
//
//            }
//        }
//    }
}
