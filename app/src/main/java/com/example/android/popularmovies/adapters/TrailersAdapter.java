package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.pojo.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by noahkim on 4/2/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private Context mContext;
    private List<Trailer> mTrailers;

    public TrailersAdapter(Context context, List<Trailer> trailers) {
        mContext = context;
        mTrailers = trailers;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_trailer_content, parent, false);
        return new TrailersAdapter.TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {
        final Trailer trailer = mTrailers.get(position);
        String trailerThumbnailPath = trailer.getThumbnail();
        Picasso.with(mContext)
                .load(trailerThumbnailPath)
                .placeholder(R.drawable.ic_local_movies_black_36dp)
                .error(R.drawable.ic_error_black_24dp)
                .into(holder.mThumbnailView);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public void setMovieTrailers(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumbnailView;

        public TrailersViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnailView = (ImageView) view.findViewById(R.id.trailer_thumbnail);

        }
    }
}
