package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by noahkim on 11/2/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Context mContext;
    private MovieAdapterOnClickHandler mClickHandler;
    private final View mEmptyView;
    private Cursor mCursor;
    private List<Movie> mMovieList;

    public MovieAdapter(Context context, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public View mView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.poster_thumbnail);
            mView = itemView;
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(long id, MovieAdapterViewHolder vh);
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_posters, parent, false);
        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final Movie movie = new Movie(mCursor);
        String moviePosterPath = movie.getMoviePosterURL();
        Picasso
                .with(mContext)
                .load(moviePosterPath)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class)
                        .setData(MovieEntry.buildMovieUri(movie.getMovieId()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//
//        return LayoutInflater.from(context).inflate(
//                R.layout.item_movie_posters, parent, false);
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//
//        Movie movie = new Movie(cursor);
//
//        String moviePosterPath = movie.getMoviePosterURL();
//        ImageView posterView = (ImageView) view.findViewById(R.id.poster_thumbnail);
//        Picasso
//                .with(context)
//                .load(moviePosterPath)
//                .into(posterView);
//    }

}

