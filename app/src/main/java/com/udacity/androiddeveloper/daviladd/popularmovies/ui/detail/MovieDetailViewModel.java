package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;

public class MovieDetailViewModel extends ViewModel {
    // The movie to be displayed in the UI:
    private MutableLiveData<Movie> mMovie;
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

    public MovieDetailViewModel() {
        mMovie = new MutableLiveData<>();
    }

    public MutableLiveData<Movie> getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie.postValue(movie);
    }
}
