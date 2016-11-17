package com.example.android.popularmovies;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail activity called via intent. Inspect the intent for movie data
            Movie movie = getActivity().getIntent().getParcelableExtra(MovieFragment.MOVIE_DATA);

            // Find views in fragment detail
            TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            TextView userRating = (TextView) rootView.findViewById(R.id.user_rating);
            TextView plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);

            // Populate views with data
            movieTitle.setText(movie.getMovieTitle());
            Picasso.with(getContext()).load(movie.getMoviePosterURL()).into(moviePoster);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            releaseDate.setText(dateFormat.format(movie.getReleaseDate()));
            userRating.setText(String.valueOf(movie.getUserRating()));
            plotSynopsis.setText(movie.getPlotSynopsis());

            return rootView;
        }
    }
}
