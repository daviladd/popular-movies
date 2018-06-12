package com.udacity.androiddeveloper.daviladd.popularmovies.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;

public class MovieDetailActivityViewModel extends ViewModel {

    private MutableLiveData<Movie> mMovie;

    public MovieDetailActivityViewModel() {
        mMovie = new MutableLiveData<>();
    }

    public MutableLiveData<Movie> getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie.postValue(movie);
    }
}
