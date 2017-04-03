package com.example.android.popularmovies;

/**
 * Created by noahkim on 3/7/17.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.adapters.ReviewsAdapter;
import com.example.android.popularmovies.adapters.TrailersAdapter;
import com.example.android.popularmovies.api.FetchReviewsTask;
import com.example.android.popularmovies.api.FetchTrailersTask;
import com.example.android.popularmovies.data.FavoritesHelper;
import com.example.android.popularmovies.pojo.Movie;
import com.example.android.popularmovies.pojo.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    TextView mTitleView, mReleaseDateView, mUserRatingView, mOverviewView, mSnackbarView;
    ImageView mPosterView;
    FloatingActionButton fab;
    private FavoritesHelper mFavoritesHelper;
    private Movie movie;
    private long currentMovieId;
    private RecyclerView mRecyclerView;
    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private List<Review> reviews;

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

        movie = getActivity().getIntent().getExtras().getParcelable(MovieAdapter.MOVIE_DETAILS);
        String title, releaseDate, voteAverage, overview, moviePoster;

        title = movie.getOriginalTitle();
        mTitleView.setText(title);

        releaseDate = movie.getReleaseDate();
        mReleaseDateView.setText(releaseDate);

        voteAverage = movie.getVoteAverage();
        mUserRatingView.setText(voteAverage);

        overview = movie.getOverview();
        mOverviewView.setText(overview);

        moviePoster = movie.getMoviePosterURL();
        Picasso.with(getContext())
                .load(moviePoster)
                .into(mPosterView);

        mFavoritesHelper = new FavoritesHelper(getContext());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFavoritesHelper.isFavorite(currentMovieId)) {
                    mFavoritesHelper.addToFavorites(movie);
                    Log.d(LOG_TAG, "Movie added to favorites");
                    Snackbar.make(getView(), getString(R.string.snackbar_added_to_favorites), Snackbar.LENGTH_SHORT).show();
                } else {
                    mFavoritesHelper.deleteFromFavorites(currentMovieId);
                    Log.d(LOG_TAG, "Movie deleted from favorites");
                    Snackbar.make(getView(), getString(R.string.snackbar_removed_from_favorites), Snackbar.LENGTH_SHORT).show();
                }
                updateFab();
            }
        });

        reviews = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_reviews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewsAdapter = new ReviewsAdapter(getActivity(), reviews);
        mRecyclerView.setAdapter(mReviewsAdapter);

        return rootView;
    }

    @Override

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onResume() {
        updateFab();
        super.onResume();
    }

    private void updateFab() {
        currentMovieId = movie.getMovieId();
        if (mFavoritesHelper.isFavorite(currentMovieId)) {
            fab.setImageResource(R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public void onStart() {
        currentMovieId = movie.getMovieId();
        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(), mReviewsAdapter);
        fetchReviewsTask.execute(String.valueOf(currentMovieId));
        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(getActivity(), mTrailersAdapter);
        fetchTrailersTask.execute(String.valueOf(currentMovieId));
        super.onStart();
    }
}