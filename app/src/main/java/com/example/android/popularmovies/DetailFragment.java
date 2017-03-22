package com.example.android.popularmovies;

/**
 * Created by noahkim on 3/7/17.
 */

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    TextView mTitleView, mReleaseDateView, mUserRatingView, mOverviewView;
    ImageView mPosterView;
    FloatingActionButton fab;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Find views in fragment detail
        mTitleView = (TextView) rootView.findViewById(R.id.movie_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mUserRatingView = (TextView) rootView.findViewById(R.id.user_rating);
        mOverviewView = (TextView) rootView.findViewById(R.id.overview);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        // Populate views with data
//            mTitleView.setText(getString(MovieEntry.COL_ORIGINAL_TITLE));
//            Picasso.with(getContext()).load(movie.getMoviePosterURL()).into(mPosterView);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            mReleaseDateView.setText(dateFormat.format(movie.getReleaseDate()));
//            mUserRatingView.setText(String.valueOf(movie.getVoteAverage()));
//            mOverviewView.setText(movie.getOverview());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Create and return a CursorLoader that will take care of creating a Cursor
        // for the data being displayed
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MovieEntry.COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

//            String title = data.getString(MovieEntry.COL_ORIGINAL_TITLE);
//            mTitleView.setText(title);
//
//            String releaseDate = data.getString(MovieEntry.COL_RELEASE_DATE);
//            mReleaseDateView.setText(releaseDate);
//
//            String voteAverage = data.getString(MovieEntry.COL_VOTE_AVERAGE);
//            mUserRatingView.setText(voteAverage);
//
//            String overview = data.getString(MovieEntry.COL_OVERVIEW);
//            mOverviewView.setText(overview);
//
//            String moviePoster = data.getString(MovieEntry.COL_POSTER_PATH);
//            Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w185/"
//                    + moviePoster).into(mPosterView);

            Movie movie = new Movie(data);
            String title = movie.getOriginalTitle();
            mTitleView.setText(title);

            String releaseDate = movie.getReleaseDate();
            mReleaseDateView.setText(releaseDate);

            String voteAverage = movie.getVoteAverage();
            mUserRatingView.setText(voteAverage);

            String overview = movie.getOverview();
            mOverviewView.setText(overview);

            String moviePoster = movie.getMoviePosterURL();
            Picasso.with(getContext())
                    .load(moviePoster)
                    .into(mPosterView);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {

    }
}