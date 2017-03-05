package com.noahkim.android.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.noahkim.android.popularmovies.data.MovieContract.MovieEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    public static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get data for ArrayAdapter and use it to populate GridView
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        // Get a reference to the GridView and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMovieAdapter);


        // Set onItemClickListener on an individual poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieEntry.CONTENT_URI);
                    startActivity(intent);
                }
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

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));
        movieTask.execute(order);
    }
}

