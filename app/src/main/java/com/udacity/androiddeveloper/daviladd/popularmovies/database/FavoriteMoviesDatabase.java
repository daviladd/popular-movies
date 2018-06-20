package com.udacity.androiddeveloper.daviladd.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavoriteMoviesDatabase extends RoomDatabase {

    private static final String TAG = FavoriteMoviesDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favoritemovies";
    private static FavoriteMoviesDatabase sInstance;

    /**
     * Returns an instance of the favorite movies database using the Singleton pattern
     * to ensure that only one database object of the database class is instantiated.
     *
     * @param context
     * @return instance of the database
     */
    public static FavoriteMoviesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        FavoriteMoviesDatabase.class,
                        FavoriteMoviesDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Returning the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
