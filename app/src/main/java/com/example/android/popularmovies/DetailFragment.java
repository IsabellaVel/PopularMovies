package com.example.android.popularmovies;

/**
 * Created by noahkim on 3/7/17.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.example.android.popularmovies.pojo.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    @BindView(R.id.movie_title)
    TextView mTitleView;
    @BindView(R.id.release_date)
    TextView mReleaseDateView;
    @BindView(R.id.user_rating)
    TextView mUserRatingView;
    @BindView(R.id.overview)
    TextView mOverviewView;
    @BindView(R.id.recyclerview_reviews)
    RecyclerView mReviewsView;
    @BindView(R.id.recyclerview_trailers)
    RecyclerView mTrailersView;

    ImageView mPosterView, mBackdropView;
    CollapsingToolbarLayout mAppBarLayout;
    FloatingActionButton fab;
    private FavoritesHelper mFavoritesHelper;
    private Movie movie;
    private long currentMovieId;
    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private List<Review> reviews;
    private List<Trailer> trailers;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Find views in fragment detail
        mAppBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        mBackdropView = (ImageView) getActivity().findViewById(R.id.movie_backdrop);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        movie = getActivity().getIntent().getExtras().getParcelable(MovieAdapter.MOVIE_DETAILS);

        String title, releaseDate, voteAverage, overview, moviePoster, backdropPoster;

        // attach data to views
        if (mAppBarLayout != null && getActivity() instanceof DetailActivity) {
            mAppBarLayout.setTitle(movie.getOriginalTitle());
        }

        title = movie.getOriginalTitle();
        mTitleView.setText(title);

        releaseDate = movie.getReleaseDate();
        mReleaseDateView.setText("Released: " + releaseDate);

        voteAverage = movie.getVoteAverage();
        mUserRatingView.setText(voteAverage);

        overview = movie.getOverview();
        mOverviewView.setText(overview);

        backdropPoster = movie.getBackdropPoster();
        Picasso.with(getContext())
                .load(backdropPoster)
                .into(mBackdropView);

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

        // display reviews
        displayReviews();

        // display trailers
        displayTrailers();

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

    private void displayReviews() {
        reviews = new ArrayList<>();
        mReviewsView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mReviewsAdapter = new ReviewsAdapter(getActivity(), reviews);
        mReviewsView.setAdapter(mReviewsAdapter);
    }

    private void displayTrailers() {
        trailers = new ArrayList<>();
        mTrailersView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mTrailersAdapter = new TrailersAdapter(getActivity(), trailers);
        mTrailersView.setAdapter(mTrailersAdapter);
    }
}