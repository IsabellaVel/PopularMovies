package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemViewHolder> {

    public static final String MOVIE_DETAILS = "movie_details";
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Context mContext;
    private final View mEmptyView;
    private Cursor mCursor;

    public MovieAdapter(Context context, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
    }

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_posters, parent, false);
        MovieItemViewHolder viewHolder = new MovieItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final Movie movie = new Movie(mCursor);
        String moviePosterPath = movie.getMoviePosterURL();
        Picasso
                .with(mContext)
                .load(moviePosterPath)
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(MOVIE_DETAILS, movie);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.poster_thumbnail);
        }
    }
}

