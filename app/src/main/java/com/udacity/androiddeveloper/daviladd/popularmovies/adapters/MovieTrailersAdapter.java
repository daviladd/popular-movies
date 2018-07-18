package com.udacity.androiddeveloper.daviladd.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Trailer;
import com.udacity.androiddeveloper.daviladd.popularmovies.utilities.PopularMoviesUtilities;

import java.util.List;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersViewHolder> {

    /**
     * Tag for the debugger
     */
    public static final String LOG_TAG = MovieTrailersAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_TRAILERS_THUMBNAIL_GRID = 0;
    private static List<Trailer> mTrailerList;
    private Context mContext;

    public MovieTrailersAdapter(@NonNull Context context, List<Trailer> trailers) {
        mContext = context;
        mTrailerList = trailers;
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) return 0;
        return mTrailerList.size();
    }

    @Override
    public MovieTrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID;

        switch (viewType) {
            case VIEW_TYPE_TRAILERS_THUMBNAIL_GRID:
                layoutID = R.layout.movie_trailer_thumbnail_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutID, parent, false);
        view.setFocusable(true);

        return new MovieTrailersAdapter.MovieTrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailersViewHolder holder, int position) {
        // Show movie title:
        holder.trailerID.setText(mTrailerList.get(position).getName());
        // Show movie poster image:
        String thumbnailPath
                = "http://img.youtube.com/vi/" + mTrailerList.get(position).getKey()+ "/0.jpg";
        Picasso.get().load(thumbnailPath).into(holder.trailerPosterThumbnail);
    }

    public void updateTrailers(List<Trailer> trailers) {
        Log.d(LOG_TAG, "Updating the trailer lists");
        mTrailerList = trailers;
        notifyDataSetChanged();
    }

    public static class MovieTrailersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView trailerPosterThumbnail;
        final TextView trailerID;

        MovieTrailersViewHolder(View view) {
            super(view);

            trailerPosterThumbnail = view.findViewById(R.id.movie_trailer_thumbnail_image);
            trailerID = view.findViewById(R.id.movie_trailer_thumbnail_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();

            // Retrieve the movie from this view's position in the adapter:
            Trailer trailer = mTrailerList.get(position);

            try {
                PopularMoviesUtilities
                        .launchTrailerVideoInYoutubeApp(context, trailer.getKey());
            } catch (ActivityNotFoundException exception) {
                PopularMoviesUtilities
                        .launchTrailerVideoInYoutubeBrowser(context, trailer.getKey());
            }
        }
    }
}
