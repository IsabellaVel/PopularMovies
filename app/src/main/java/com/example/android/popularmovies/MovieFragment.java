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
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieAdapter mMovieAdapter;
    private GridView gridView;
    private RecyclerView mRecyclerView;
    private String mSortOrder;
    private static final int MOVIE_LOADER = 0;

    public interface Callback {
        public void onItemSelected(Uri idUri, MovieAdapter.MovieAdapterViewHolder vh);
    }

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.action_popular_movies));

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

        // specify an adapter
        mRecyclerView.setAdapter(mMovieAdapter);

//        // Set onItemClickListener on an individual poster
//        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(MovieEntry.buildMovieUri(cursor.getLong(MovieEntry.COL_MOVIE_ID)));
//                    startActivity(intent);
//                }
//            }
//        });
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
        return new CursorLoader(getContext(),
                MovieEntry.CONTENT_URI,
                MovieEntry.COLUMNS,
                selection,
                selectionArgs,
                null);
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

