package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.adapters.MovieReviewsAdapter;
import com.udacity.androiddeveloper.daviladd.popularmovies.adapters.MovieTrailersAdapter;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.ReviewList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.TrailerList;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabase;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabaseExecutors;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMovieDetailBinding;
import com.udacity.androiddeveloper.daviladd.popularmovies.utilities.PopularMoviesUtilities;

import java.text.DecimalFormat;

public class MovieDetailActivity extends AppCompatActivity {
    public final static String PARCELABLE_EXTRA_MOVIE = "MOVIE";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private MovieDetailViewModel mMovieDetailViewModel;
    private ActivityMovieDetailBinding mActivityMovieDetail;

    private MovieTrailersAdapter mMovieTrailersAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    private boolean mIsFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate has been called");

        // To add the "up" navigation button:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the UI binding:
        mActivityMovieDetail
                = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        // Set the ViewModel:
        mMovieDetailViewModel
                = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        // and observe the LiveData:
        // First the movie to be shown:
        Log.d(TAG, "MOVIE: observing the ViewModel movie LiveData");
        mMovieDetailViewModel.getMovie().observe(this, movie -> {
            if (movie != null) {
                Log.d(TAG, "MOVIE: a movie has been received -> updating the UI");
                updateMovieDetails(movie);
            }
        });
        // Then the trailers for this movie:
        Log.d(TAG, "TRAILERS: observing the ViewModel TrailerList LiveData");
        mMovieDetailViewModel.getTrailers().observe(this, trailerList -> {
            if (trailerList != null) {
                Log.d(TAG, "TRAILERS: a trailer list has been received -> updating the UI");
                updateTrailers(trailerList);
            }
        });
        // Then, the user reviews of this movie:
        Log.d(TAG, "REVIEWS: observing the ViewModel ReviewList LiveData");
        mMovieDetailViewModel.getReviews().observe(this, reviewsList -> {
            if (reviewsList != null) {
                Log.d(TAG, "REVIEWS: a review list has been received -> updating the UI");
                updateReviews(reviewsList);
            }
        });
        // Now the favorite star to show that this movie is in the Favorite Movies DB:
        Log.d(TAG, "FAVORITES: observing the ViewModel favorite LiveData");
        mMovieDetailViewModel.isMovieInFavorites().observe(this, isInFavorites -> {
            if (isInFavorites != null) {
                Log.d(TAG, "FAVORITES: a review list has been received -> updating the UI");
                updateFavoriteStar(isInFavorites);
            }
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
        }

        Log.d(TAG, "Opening detailed view for " + movie.getTitle());
        // Initialize the UI elements:
        initializeUI(movie);

        isMovieInFavorites(movie.getId());

        // Notify the ViewModel about the movie to be shown:
        mMovieDetailViewModel.setMovie(movie);
    }

    private void initializeUI(Movie movie) {
        // Set the favorite start to be displayed as designed:
        initializeUIFavoriteStar();
        initializeUITrailers(movie);
        initializeUIReviews(movie);
    }

    private void initializeUIFavoriteStar() {
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setText(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setTextOn(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setTextOff(null);
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setOnClickListener(new FavoriteButtonOnClickListener());
    }

    private void initializeUIReviews(Movie movie) {
        Log.d(TAG, "Setting up the Reviews view");
        // Reviews section:
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mActivityMovieDetail.movieDetailsReviews.recyclerviewReviews.setLayoutManager(layoutManager);
        mActivityMovieDetail.movieDetailsReviews.recyclerviewReviews.setHasFixedSize(true);
        // Create the adapter:
        mMovieReviewsAdapter = new MovieReviewsAdapter(this, null);
        mActivityMovieDetail.movieDetailsReviews.recyclerviewReviews.setAdapter(mMovieReviewsAdapter);
    }

    private void initializeUITrailers(Movie movie) {
        Log.d(TAG, "Setting up the Trailers view");
        // Trailers section:
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mActivityMovieDetail.movieDetailsTrailers.recyclerviewTrailers.setLayoutManager(layoutManager);
        mActivityMovieDetail.movieDetailsTrailers.recyclerviewTrailers.setHasFixedSize(true);
        // Create the adapter:
        mMovieTrailersAdapter = new MovieTrailersAdapter(this, null);
        mActivityMovieDetail.movieDetailsTrailers.recyclerviewTrailers.setAdapter(mMovieTrailersAdapter);
    }

    private void updateTrailers(TrailerList trailerList){
        mMovieTrailersAdapter.updateTrailers(trailerList.getResults());
    }

    private void updateReviews(ReviewList reviewList){
        mMovieReviewsAdapter.updateReviews(reviewList.getResults());
    }

    private void updateFavoriteStar(boolean isFavorite) {
        mActivityMovieDetail.movieDetailsHeader.favoriteStar.setChecked(isFavorite);
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
    private void updateMovieDetails(Movie movie) {
        // Set the movie title:
        mActivityMovieDetail.movieDetailsTitle.setText(movie.getTitle());
        // Set the movie thumbnail:
        String thumbnailPath
                = PopularMoviesUtilities.TMDB_API_THUMBNAIL_PATH
                + movie.getPosterPath();
        Picasso.get().load(thumbnailPath).into(mActivityMovieDetail.movieDetailsHeader.movieDetailsPosterThumbnail);
        // Set the release year:
        mActivityMovieDetail.movieDetailsHeader.movieDetailsReleaseDate
                .setText(Integer.toString(movie.getReleaseYear()));
        // Set the user rating:
        mActivityMovieDetail.movieDetailsHeader.movieDetailsUserRatingValue
                .setText(new DecimalFormat(".#").format(movie.getVoteAverage()));
        // Set the overview:
        mActivityMovieDetail.movieDetailsBody.movieDetailsSynopsisValue.setText(movie.getOverview());
    }

    private void isMovieInFavorites(int movieID) {
        Log.d(TAG, "FAVORITES: checking if the movie is in the Favorite Movies DB");
        FavoriteMoviesDatabase favoriteMoviesDatabase
                = FavoriteMoviesDatabase.getInstance(getApplication().getApplicationContext());
        LiveData<Integer> dbMovieId = favoriteMoviesDatabase.movieDao().isMovieInFavorites(movieID);
        dbMovieId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer movieId) {
                if (movieId != null) {
                    Log.d(TAG, "FAVORITES: movie is in the favorites list");
                    mMovieDetailViewModel.setIsMovieInFavorites(true);
                } else {
                    Log.d(TAG, "FAVORITES: movie is in not the favorites list");
                    mMovieDetailViewModel.setIsMovieInFavorites(false);
                }
            }
        });
    }

    private class FavoriteButtonOnClickListener implements CompoundButton.OnClickListener {
        @Override
        public void onClick(View view) {
            mMovieDetailViewModel.updateMovieInFavorites(
                    mActivityMovieDetail.movieDetailsHeader.favoriteStar.isChecked());
        }
    }

    private class FavoriteButtonOnCheckedChangedListener
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
