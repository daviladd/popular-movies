package com.udacity.androiddeveloper.daviladd.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;

import java.util.List;

public class PopularMoviesAdapter
        extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {
    // Tag for logger:
    private static final String TAG = PopularMoviesAdapter.class.getSimpleName();


    private static final int VIEW_TYPE_POSTER_THUMBNAIL_GRID = 0;

    private Context mContext;

    private List<Movie> mMovies;

    public PopularMoviesAdapter(@NonNull Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }


    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_POSTER_THUMBNAIL_GRID:
                layoutID = R.layout.movie_thumbnail_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        holder.movieOriginalTitle.setText(mMovies.get(position).getOriginalTitle());
        holder.moviePosterThumbnail.setImageResource(android.R.drawable.ic_menu_report_image);
    }

    public static class PopularMoviesViewHolder extends RecyclerView.ViewHolder
            //        implements View.OnClickListener
    {

        final ImageView moviePosterThumbnail;
        final TextView movieOriginalTitle;

        PopularMoviesViewHolder(View view) {
            super(view);

            moviePosterThumbnail = view.findViewById(R.id.movie_poster_thumbnail_image);
            movieOriginalTitle = view.findViewById(R.id.movie_poster_thumbnail_title);

            //view.setOnClickListener(this);
        }

        // TODO: override onClick to handle the clicks on the View!
    }
}
