package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.ReviewList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.TrailerList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitClient;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitService;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabase;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabaseExecutors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieDetailViewModel extends AndroidViewModel {
    private final String LOG_TAG = MovieDetailViewModel.class.getSimpleName();
    /* MutableLiveData vs LiveData:

    In LiveData - Android Developer Documentation, you can see that for LiveData,
        setValue() & postValue() methods are not public.
    Whereas, in MutableLiveData - Android Developer Documentation, you can see that,
        MutableLiveData extends LiveData internally and also the two magic methods
        of LiveData is publicly available in this and they are setValue() & postValue().

    setValue() : set the value and dispatch the value to all the active
        observers, must be called from main thread.
    postValue() : post a task to main thread to override value set by
        setValue(), must be called from background thread (it can also be called from
        the main/ui thread, even though it is no point in using that call).

    So, LiveData is immutable. MutableLiveData is LiveData which is mutable & thread-safe.
    */
    // The movie to be displayed in the UI:
    private MutableLiveData<Movie> mMovie;
    private MutableLiveData<TrailerList> mTrailers;
    private MutableLiveData<ReviewList> mReviews;
    private MutableLiveData<Boolean> mIsMovieInFavorites;

    private boolean mTrailersQuerySent;
    private boolean mReviewsQuerySent;


    public MovieDetailViewModel(Application application) {
        super(application);
        Log.d(LOG_TAG, "Creating the MovieDetailViewModel");
        mMovie = new MutableLiveData<>();
        mTrailers = new MutableLiveData<>();
        mReviews = new MutableLiveData<>();
        mIsMovieInFavorites = new MutableLiveData<>();
        mTrailersQuerySent = false;
        mReviewsQuerySent = false;
    }

    public MutableLiveData<Movie> getMovie() {
        Log.d(LOG_TAG, "MOVIE: getMovie");
        return mMovie;
    }

    public void setMovie(Movie movie) {

        if (!movie.equals(mMovie.getValue())) {
            int movieId = movie.getId();
            Log.d(LOG_TAG, "MOVIE: the movie is DIFFERENT than current one -> everything needs to be updated");
            mMovie.postValue(movie);
            // Retrieve movie's trailers:
            getMovieTrailers(getApplication().getApplicationContext().getString(R.string.API_KEY_TMDB), movieId);
            // Retrieve movie's reviews:
            getMovieReviews(getApplication().getApplicationContext().getString(R.string.API_KEY_TMDB), movieId);
//            // Check if the movie is in the Favorite Movies DB:
//            isMovieInFavorites(movieId);
        } else {
            Log.d(LOG_TAG, "MOVIE: the movie is the SAME as current one -> nothing needs to be updated");
        }
    }

    public MutableLiveData<Boolean> isMovieInFavorites() {
        Log.d(LOG_TAG, "FAVORITES: isMovieInFavorites");
        return mIsMovieInFavorites;
    }

    public void setIsMovieInFavorites(boolean isMovieInFavorites) {
        Log.d(LOG_TAG, "FAVORITES: setIsMovieInFavorites");
        mIsMovieInFavorites.postValue(isMovieInFavorites);
    }

    public void updateMovieInFavorites(boolean setFavorite) {
        FavoriteMoviesDatabase favoriteMoviesDatabase
                = FavoriteMoviesDatabase.getInstance(getApplication().getApplicationContext());
        if (setFavorite) {
            // Add the movie to the favorite movies DB:
            if (mIsMovieInFavorites.getValue()) {
                // It is already in the DB. Right now this situation is impossible to happen
                //  but who knows in the future...
                // Update the entry:
                Log.d(LOG_TAG, "FAVORITES: updating movie entry in the Favorite Movies DB");
                favoriteMoviesDatabase.movieDao()
                        .updateMovie(mMovie.getValue());
            } else {
                // Add the movie to favorites:
                FavoriteMoviesDatabaseExecutors.getsInstance().databaseExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "FAVORITES: adding movie to the Favorite Movies DB");
                        favoriteMoviesDatabase.movieDao()
                                .insertMovie(mMovie.getValue());
                    }
                });
            }
        } else {
            // Remove the movie from favorites:
            Log.d(LOG_TAG, "FAVORITES: removing movie from the Favorite Movies DB");
            FavoriteMoviesDatabaseExecutors.getsInstance().databaseExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    favoriteMoviesDatabase.movieDao()
                            .deleteMovie(mMovie.getValue());
                }
            });
        }
    }

    public MutableLiveData<TrailerList> getTrailers() {
        Log.d(LOG_TAG, "TRAILERS: getTrailers");
        return mTrailers;
    }

    private void setTrailers(TrailerList trailers) {
        Log.d(LOG_TAG, "TRAILERS: setTrailers");
        mTrailersQuerySent = true;
        mTrailers.postValue(trailers);
    }

    /**
     * Calls the suitable Retrofit method to get a TrailerList for the given movie.
     *
     * @param movieId Database ID of the movie
     */
    public void getMovieTrailers(String apiKey, int movieId) {
        Log.d(LOG_TAG, "TRAILERS: trying to retrieve trailers from movies with ID " + movieId);

        if (mTrailersQuerySent) {
            Log.d(LOG_TAG, "TRAILERS: the trailers were already retrieved before");
            return;
        }

        Retrofit retrofit = TMDBRetrofitClient.getClient();
        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);

        Call<TrailerList> call = apiServiceTmdb.getTrailersByMovie(movieId, apiKey);
        call.enqueue(new MovieDetailViewModel.TrailerListCallback());
    }

    public MutableLiveData<ReviewList> getReviews() {
        Log.d(LOG_TAG, "REVIEWS: getReviews");
        return mReviews;
    }

    private void setReviews(ReviewList reviewList) {
        Log.d(LOG_TAG, "REVIEWS: setReviews");
        mReviewsQuerySent = true;
        mReviews.postValue(reviewList);
    }

    /**
     * Calls the suitable Retrofit method to get a ReviewList for the given movie.
     *
     * @param movieId Database ID of the movie
     */
    public void getMovieReviews(String apiKey, int movieId) {

        if (mReviewsQuerySent) {
            Log.d(LOG_TAG, "REVIEWS: the reviews were already retrieved before (NO QUERY TO TMDB)");
            return;
        }

        Log.d(LOG_TAG, "REVIEWS: QUERYING TMDB to retrieve Reviews from movies with ID " + movieId);

        Retrofit retrofit = TMDBRetrofitClient.getClient();
        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);

        Call<ReviewList> call = apiServiceTmdb.getReviewsByMovie(movieId, apiKey);
        call.enqueue(new MovieDetailViewModel.ReviewListCallback());
    }

    private class TrailerListCallback implements Callback<TrailerList> {
        @Override
        public void onResponse(retrofit2.Call<TrailerList> call, Response<TrailerList> trailerListResponse) {
            if (trailerListResponse.isSuccessful()) {
                Log.d(LOG_TAG, "TRAILERS: TMDB returned a TrailerList");
                setTrailers(trailerListResponse.body());
            } else {
                int statusCode = trailerListResponse.code();
                Log.d(LOG_TAG, "TRAILERS: the TrailerList could not be retrieved: TMDB server returned errorcode " + statusCode);
                // TODO: handle error on request depending on the status code
                Log.e(LOG_TAG, trailerListResponse.message());
                setTrailers(null);
            }
        }

        @Override
        public void onFailure(retrofit2.Call<TrailerList> call, Throwable t) {
            Log.d(LOG_TAG, "TRAILERS: the TrailerList could not be retrieved: no answer from TMDB Server");
        }
    }

    private class ReviewListCallback implements Callback<ReviewList> {
        @Override
        public void onResponse(retrofit2.Call<ReviewList> call, Response<ReviewList> reviewListResponse) {
            if (reviewListResponse.isSuccessful()) {
                Log.d(LOG_TAG, "REVIEWS: TMDB returned a ReviewList with " + reviewListResponse.body().getTotalResults() + " reviews");
                setReviews(reviewListResponse.body());
            } else {
                int statusCode = reviewListResponse.code();
                Log.d(LOG_TAG, "REVIEWS: the ReviewList could not be retrieved: TMDB server returned errorcode " + statusCode);
                // TODO: handle error on request depending on the status code
                Log.e(LOG_TAG, reviewListResponse.message());
                setReviews(null);
            }
        }

        @Override
        public void onFailure(retrofit2.Call<ReviewList> call, Throwable t) {
            Log.d(LOG_TAG, "REVIEWS: the ReviewList could not be retrieved: no answer from TMDB Server");
        }
    }
}
