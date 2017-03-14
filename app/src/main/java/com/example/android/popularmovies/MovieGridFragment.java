package com.example.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.sync.MovieSyncAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieGridAdapter movieGridAdapter;

    private static final int MOVIE_LOADER = 0;

    public MovieGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get data for CursorAdapter and use it to populate GridView
        movieGridAdapter = new MovieGridAdapter(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieGridAdapter);


        // Set onItemClickListener on an individual poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieEntry.buildMovieUri(cursor.getLong(MovieEntry.COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });
        updateMovies();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }
        if (id == R.id.menu_item_popular) {
            Utility.setSortOrder(getContext(), getActivity().getString(R.string.pref_order_popular));
        }
        if (id == R.id.menu_item_top_rated) {
            Utility.setSortOrder(getContext(), getActivity().getString(R.string.pref_order_top_rated));
        }
        updateMovies();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onSortOrderChanged() {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
//        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
//        String order = Utility.getDefaultSortOrder(getActivity());
//        movieTask.execute(order);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                MovieEntry.CONTENT_URI,
                MovieEntry.COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieGridAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieGridAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
}

