package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.pojo.Reviews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noah on 4/1/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private Context mContext;
    private List<Reviews> mReviews;

    public ReviewsAdapter(Context context, List<Reviews> reviews){
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review_content, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsViewHolder holder, int position) {
        final Reviews review = mReviews.get(position);

        holder.mAuthorTextView.setText(review.getAuthor());
        holder.mContentTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void setMovieReviews(List<Reviews> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorTextView;
        public final TextView mContentTextView;

        public ReviewsViewHolder(View view) {
            super(view);
            mView = view;
            mAuthorTextView = (TextView) view.findViewById(R.id.review_author);
            mContentTextView = (TextView) view.findViewById(R.id.review_text);
        }
    }
}
