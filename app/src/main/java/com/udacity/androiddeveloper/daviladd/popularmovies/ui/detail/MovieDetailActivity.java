package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabase;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMovieDetailBinding;
import com.udacity.androiddeveloper.daviladd.popularmovies.utilities.PopularMoviesUtilities;

import java.text.DecimalFormat;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public final static String PARCELABLE_EXTRA_MOVIE = "MOVIE";

    private ActivityMovieDetailBinding mActivityMovieDetail;
    private Movie mMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMovieDetail = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        // To add the "up" navigation button:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve the movie instance which details are to be shown in this activity:
        mMovie = getIntent().getParcelableExtra(PARCELABLE_EXTRA_MOVIE);
        if (mMovie != null) {
            bindMovieToUI(mMovie);
        } else {
            // TODO: what to do in this case? Return back immediately to previous activity?
            Log.e(TAG, getString(R.string.movie_details_not_available));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.movie_details_not_available),
                    Toast.LENGTH_LONG);
        }

        ToggleButton favoriteButton = findViewById(R.id.favorite_star);
        favoriteButton.setText(null);
        favoriteButton.setTextOn(null);
        favoriteButton.setTextOff(null);
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton favoriteButton, boolean isChecked) {
                FavoriteMoviesDatabase favoriteMoviesDatabase
                        = FavoriteMoviesDatabase.getInstance(getApplicationContext());
                if (isChecked) {
                    Log.d(TAG, "User selected to add this movie to the Favorite Movies DB");
                    // TODO: add this movie to the database
                    // TODO: before inserting a movie, we need to check if a movie with this one's ID
                    //  is already in the database, and if so, call update instead!
                    favoriteMoviesDatabase.movieDao().insertMovie(mMovie);
                } else {
                    Log.d(TAG, "The user does not want this movie to be in the Favorite Movies DB");
                    // TODO: remove this movie from the database
                    favoriteMoviesDatabase.movieDao().deleteMovie(mMovie);
                }
            }
        });
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

    /**
     * Uses binding library to bind the details of a given Movie to the widgets in the MovieDetailActivity.
     *
     * @param movie Movie which details are to be shown
     */
    private void bindMovieToUI(Movie movie) {

        String thumbnailPath
                = PopularMoviesUtilities.TMDB_API_THUMBNAIL_PATH
                + movie.getPosterPath();
        Picasso.get().load(thumbnailPath).into(mActivityMovieDetail.movieDetailsHeader.movieDetailsPosterThumbnail);

        mActivityMovieDetail.movieDetailsTitle.setText(movie.getTitle());
        mActivityMovieDetail.movieDetailsHeader.movieDetailsReleaseDate
                .setText(Integer.toString(movie.getReleaseYear()));
        mActivityMovieDetail.movieDetailsHeader.movieDetailsUserRatingValue
                .setText(new DecimalFormat(".#").format(movie.getVoteAverage()));
        mActivityMovieDetail.movieDetailsBody.movieDetailsSynopsisValue.setText(movie.getOverview());
        // TODO: find a method to know if a movie is on the user's favorite movie list or not, to
        //  display the star toggle button either glowing or off.

    }
}
