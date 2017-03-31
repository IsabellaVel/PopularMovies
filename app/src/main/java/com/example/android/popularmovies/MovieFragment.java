package com.example.android.popularmovies;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract.FavoritesEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView;
    private RecyclerView mRecyclerView;
    private String mSortOrder;
    public static final int MOVIE_LOADER = 0;
    public static final int FAVORITES_LOADER = 1;
    private MovieAdapter mMovieAdapter;

    public interface Callback {
        public void onItemSelected(Uri idUri, MovieAdapter.MovieItemViewHolder vh);
    }

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.action_popular_movies));

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView and attach this adapter to it
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movies);

        // Set the layout manager
        GridLayoutManager gridLayoutMgr = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutMgr);

        View emptyView = rootView.findViewById(R.id.recyclerview_movies_empty);

        // Get data for CursorAdapter and use it to populate RecyclerView
        mMovieAdapter = new MovieAdapter(getActivity(), emptyView);

        // Specify an adapter
        mRecyclerView.setAdapter(mMovieAdapter);

        return rootView;
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

        switch (id) {
            case R.id.menu_item_popular:
                Utility.setSortOrder(getContext(), getActivity().getString(R.string.pref_order_popular));
                break;
            case R.id.menu_item_top_rated:
                Utility.setSortOrder(getContext(), getActivity().getString(R.string.pref_order_top_rated));
                break;
            case R.id.menu_item_favorites:
                getActivity().getSupportLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
                return true;
        }
        updateMovies();
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    public void updateMovies() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        movieTask.execute(mSortOrder);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSortOrder = Utility.getDefaultSortOrder(getContext());
        final String selection = MovieEntry.COLUMN_SORT_CRITERIA + " = ? ";
        final String[] selectionArgs = new String[]{mSortOrder};

        if (id == FAVORITES_LOADER) {
            return new CursorLoader(
                    getContext(),
                    FavoritesEntry.CONTENT_URI,
                    FavoritesEntry.COLUMNS,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(getContext(),
                    MovieEntry.CONTENT_URI,
                    MovieEntry.COLUMNS,
                    selection,
                    selectionArgs,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
        mRecyclerView.scrollToPosition(0);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
}

