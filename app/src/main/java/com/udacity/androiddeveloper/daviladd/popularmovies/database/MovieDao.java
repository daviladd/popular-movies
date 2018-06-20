package com.udacity.androiddeveloper.daviladd.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie ORDER BY id")
    List<Movie> getAllFavoriteMovies();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movie WHERE id = :id")
    Movie getMovieById(int id);
}
