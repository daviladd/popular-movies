package com.udacity.androiddeveloper.daviladd.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Movie>> mFavoriteMovies;

    private MutableLiveData<Integer> mViewMode;

    public MutableLiveData<Integer> getViewMode() {
        return mViewMode;
    }

    public void setViewMode(int viewMode) {
        mViewMode.postValue(viewMode);
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        FavoriteMoviesDatabase favoriteMoviesDatabase
                = FavoriteMoviesDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the favorite movies from the database");
        mFavoriteMovies = favoriteMoviesDatabase.movieDao().getAllFavoriteMovies();

        mViewMode = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }
}
