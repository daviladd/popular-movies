package com.udacity.androiddeveloper.daviladd.popularmovies.database;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteMoviesDatabaseExecutors {
    private static  final String TAG = FavoriteMoviesDatabaseExecutors.class.getSimpleName();

    // For the Singleton pattern instantiation:
    private static FavoriteMoviesDatabaseExecutors sInstance;
    private static final Object LOCK = new Object();
    private final Executor databaseExecutor;

    private FavoriteMoviesDatabaseExecutors(Executor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;
    }

    public static FavoriteMoviesDatabaseExecutors getsInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FavoriteMoviesDatabaseExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor databaseExecutor() {
        return databaseExecutor;
    }
}
