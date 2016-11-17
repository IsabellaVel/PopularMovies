package com.example.android.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    public static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieAdapter movieAdapter;
    private String mApiKey;

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get data for ArrayAdapter and use it to populate GridView
        movieAdapter = new MovieAdapter(getContext(), R.layout.grid_item);

        // Get a reference to the GridView and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);


        // Set onItemClickListener on an individual poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(MovieFragment.MOVIE_DATA, movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
        setHasOptionsMenu(true);
    }

    private void updateMovies(){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));
        movieTask.execute(order);
    }

    /**
    private  void updateMovieData(String order){
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putString(getString(R.string.pref_order_key), order).apply();
        FetchMovieTask movieTask = new FetchMovieTask(
                getString(R.string.movie_db_api_key), movieAdapter);
        movieTask.execute(order);
    }
     */

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        /**
        public FetchMovieTask(String apiKey, MovieAdapter adapter) {
            mApiKey = apiKey;
            movieAdapter = adapter;
        } */

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length < 1)
                return null;

            String sortOrder = params[0];

            Uri.Builder builder = new Uri.Builder();

            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(sortOrder)
                    .appendQueryParameter("api_key", getString(R.string.movie_db_api_key));

            String movie_url = builder.build().toString();

            List<Movie> movies = QueryUtils.fetchMovieData(movie_url);
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.clear();
                movieAdapter.addAll(movies);
            }
        }
    }
}
