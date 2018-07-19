package com.udacity.androiddeveloper.daviladd.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Review;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    /**
     * Tag for the debugger
     */
    public static final String LOG_TAG = MovieReviewsAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_REVIEWS_GRID = 0;
    private static List<Review> mReviewList;
    private Context mContext;

    public MovieReviewsAdapter(@NonNull Context context, List<Review> reviews) {
        mContext = context;
        mReviewList = reviews;
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) return 0;
        return mReviewList.size();
    }

    @Override
    public MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_REVIEWS_GRID:
                layoutID = R.layout.movie_review_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new MovieReviewsAdapter.MovieReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsViewHolder holder, int position) {
        // Set review header text:
        holder.reviewHeader.setText(mReviewList.get(position).getAuthor());
        // Set review content text:
        holder.reviewContent.setText(mReviewList.get(position).getContent());

    }

    public void updateReviews(List<Review> reviews) {
        Log.d(LOG_TAG, "Updating the Review lists");
        mReviewList = reviews;
        notifyDataSetChanged();
    }

    public static class MovieReviewsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final TextView reviewHeader;
        final TextView reviewContent;

        MovieReviewsViewHolder(View view) {
            super(view);

            reviewHeader = view.findViewById(R.id.review_header);
            reviewContent = view.findViewById(R.id.review_content);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();

            // Retrieve the movie from this view's position in the adapter:
            Review review = mReviewList.get(position);

            // TODO: implement expand/collapse of the review for example
        }
    }
}
