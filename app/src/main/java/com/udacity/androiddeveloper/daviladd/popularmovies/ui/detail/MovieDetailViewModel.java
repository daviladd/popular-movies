package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.udacity.androiddeveloper.daviladd.popularmovies.R;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.MovieList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.TrailerList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitClient;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieDetailViewModel extends ViewModel {
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

    public MovieDetailViewModel() {
        mMovie = new MutableLiveData<>();
        mTrailers = new MutableLiveData<>();
    }

    public MutableLiveData<Movie> getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie.postValue(movie);
    }

    public MutableLiveData<TrailerList> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(TrailerList trailers) {
        mTrailers.postValue(trailers);
    }

    /**
     * Calls the suitable Retrofit method to get a TrailerList for the given movie.
     *
     * @param movieId Database ID of the movie
     */
    public void getMovieTrailers(String apiKey, int movieId) {
        Log.d(LOG_TAG, "Trying to retrieve trailers from movies with ID " + movieId);

        Retrofit retrofit = TMDBRetrofitClient.getClient();
        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);

        Call<TrailerList> call = apiServiceTmdb.getTrailersByMovie(movieId, apiKey);
        call.enqueue(new MovieDetailViewModel.TrailerListCallback());
    }

    private class TrailerListCallback implements Callback<TrailerList> {
        @Override
        public void onResponse(retrofit2.Call<TrailerList> call, Response<TrailerList> trailerListResponse) {
            if (trailerListResponse.isSuccessful()) {
                Log.d(LOG_TAG, "Received a TrailerList");
                setTrailers(trailerListResponse.body());
            } else {
                int statusCode = trailerListResponse.code();
                Log.d(LOG_TAG, "The TrailerList could not be retrieved: server returned errorcode " + statusCode);
                // TODO: handle error on request depending on the status code
                Log.e(LOG_TAG, trailerListResponse.message());
                setTrailers(null);
            }
        }

        @Override
        public void onFailure(retrofit2.Call<TrailerList> call, Throwable t) {
            Log.d(LOG_TAG, "The TrailerList could not be retrieved: no answer from Server");
        }
    }
}
