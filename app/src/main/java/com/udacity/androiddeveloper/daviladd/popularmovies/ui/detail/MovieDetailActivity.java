package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMovieDetailBinding;

import java.text.DecimalFormat;

public class MovieDetailActivity extends LifecycleActivity {

    private static MovieDetailActivityViewModel mMovieDetailActivityViewModel;

    private ActivityMovieDetailBinding mActivityMovieDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMovieDetail = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

//        mMovieDetailActivityViewModel = ViewModelProviders.of(this).get(MovieDetailActivityViewModel.class);
//        mMovieDetailActivityViewModel.getMovie().observe(this, movie->{
//            if (movie != null ) bindMovieToUI(movie);
//        });

        Movie movie = getIntent().getParcelableExtra("MOVIE");
        bindMovieToUI(movie);
    }

    private void bindMovieToUI(Movie movie) {

        String thumbnailPath = "http://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Picasso.get().load(thumbnailPath).into(mActivityMovieDetail.movieDetailsHeader.movieDetailsPosterThumbnail);

        mActivityMovieDetail.movieDetailsHeader.movieDetailsTitle.setText(movie.getTitle());
        mActivityMovieDetail.movieDetailsHeader.movieDetailsReleaseDate
                .setText("(" + Integer.toString(movie.getReleaseYear()) + ")");
        mActivityMovieDetail.movieDetailsHeader.movieDetailsUserRatingLabel
                .setText(new DecimalFormat(".#").format(movie.getVoteAverage()) + "/10");
        mActivityMovieDetail.movieDetailsBody.movieDetailsSynopsisValue.setText(movie.getOverview());

    }
}
