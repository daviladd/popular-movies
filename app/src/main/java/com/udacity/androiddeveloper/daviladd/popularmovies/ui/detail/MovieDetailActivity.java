package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabaseExecutors;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMovieDetailBinding;
import com.udacity.androiddeveloper.daviladd.popularmovies.utilities.PopularMoviesUtilities;

import java.text.DecimalFormat;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public final static String PARCELABLE_EXTRA_MOVIE = "MOVIE";

    private MovieDetailViewModel mMovieDetailViewModel;
    private ActivityMovieDetailBinding mActivityMovieDetail;

    private boolean mIsFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To add the "up" navigation button:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the UI binding:
        mActivityMovieDetail
                = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        // Set the ViewModel:
        mMovieDetailViewModel
                = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        // and observe the LiveData:
        mMovieDetailViewModel.getMovie().observe(this, movie -> {
            if (movie != null) bindMovieToUI(movie);
        });


        // Retrieve the movie instance which details are to be shown in this activity:
        Movie movie = getIntent().getParcelableExtra(PARCELABLE_EXTRA_MOVIE);
        if (movie == null) {
            // TODO: what to do in this case? Return back immediately to previous activity?
            Log.e(TAG, getString(R.string.movie_details_not_available));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.movie_details_not_available),
                    Toast.LENGTH_LONG);
            finish();
        } else {
            Log.d(TAG, "Opening detailed view for " + movie.getTitle());
            isMovieInFavorites(movie);
        }
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

        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setChecked(mIsFavorite);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setText(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setTextOn(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setTextOff(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setOnCheckedChangeListener(new favoriteButtonOnCheckedChangedListener());
    }

    private void isMovieInFavorites(Movie movie) {
        FavoriteMoviesDatabase favoriteMoviesDatabase
                = FavoriteMoviesDatabase.getInstance(getApplicationContext());
        LiveData<Integer> dbMovieId = favoriteMoviesDatabase.movieDao().isMovieInFavorites(movie.getId());
        dbMovieId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer movieId) {
                Log.d(TAG, "movieId = " + movieId);
                if (movieId != null) {
                    Log.d(TAG, "Movie is in the favorites list");
                    mIsFavorite = true;
                } else {
                    Log.d(TAG, "Movie is in not the favorites list");
                    mIsFavorite = false;
                }
                mMovieDetailViewModel.setMovie(movie);
            }
        });
    }

    private class favoriteButtonOnCheckedChangedListener
            implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton favoriteButton, boolean isChecked) {
            FavoriteMoviesDatabase favoriteMoviesDatabase
                    = FavoriteMoviesDatabase.getInstance(getApplicationContext());
            if (isChecked) {
                // Add the movie to the favorite movies DB:
                if (mIsFavorite) {
                    // It is already in the DB. Right now this situation is impossible to happen
                    //  but who knows in the future...
                    // Update the entry:
                    Log.d(TAG, "Updating movie entry in the Favorite Movies DB");
                    favoriteMoviesDatabase.movieDao()
                            .updateMovie(mMovieDetailViewModel.getMovie().getValue());
                } else {
                    // Add the movie to favorites:
                    FavoriteMoviesDatabaseExecutors.getsInstance().databaseExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Adding movie to the Favorite Movies DB");
                            favoriteMoviesDatabase.movieDao()
                                    .insertMovie(mMovieDetailViewModel.getMovie().getValue());
                        }
                    });
                }
            } else {
                // Remove the movie from favorites:
                Log.d(TAG, "Removing movie from the Favorite Movies DB");
                FavoriteMoviesDatabaseExecutors.getsInstance().databaseExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        favoriteMoviesDatabase.movieDao()
                                .deleteMovie(mMovieDetailViewModel.getMovie().getValue());
                    }
                });
            }
        }
    }
}
