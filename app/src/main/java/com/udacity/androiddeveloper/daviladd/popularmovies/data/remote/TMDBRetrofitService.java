package com.udacity.androiddeveloper.daviladd.popularmovies.data.remote;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.MovieList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TMDBRetrofitService {
    String POPULARITY = "movie/popular";
    String RATING = "movie/top_rated";

    @GET(POPULARITY)
    Call<MovieList> getMoviesByPopularity();

    @GET(RATING)
    Call<MovieList> getMoviesByRating();
}
