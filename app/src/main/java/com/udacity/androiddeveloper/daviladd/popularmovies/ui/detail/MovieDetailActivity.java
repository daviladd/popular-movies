package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMovieDetailBinding;

import java.text.DecimalFormat;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding mActivityMovieDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMovieDetail = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        // To add the "up" navigation button:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Movie movie = getIntent().getParcelableExtra("MOVIE");
        bindMovieToUI(movie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindMovieToUI(Movie movie) {

        String thumbnailPath = "http://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Picasso.get().load(thumbnailPath).into(mActivityMovieDetail.movieDetailsHeader.movieDetailsPosterThumbnail);

        mActivityMovieDetail.movieDetailsTitle.setText(movie.getTitle());
        mActivityMovieDetail.movieDetailsHeader.movieDetailsReleaseDate
                .setText(Integer.toString(movie.getReleaseYear()));
        mActivityMovieDetail.movieDetailsHeader.movieDetailsUserRatingValue
                .setText(new DecimalFormat(".#").format(movie.getVoteAverage()));
        mActivityMovieDetail.movieDetailsBody.movieDetailsSynopsisValue.setText(movie.getOverview());

    }
}
