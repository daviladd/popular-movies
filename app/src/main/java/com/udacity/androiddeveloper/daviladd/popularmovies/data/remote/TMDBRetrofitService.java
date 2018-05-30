package com.udacity.androiddeveloper.daviladd.popularmovies.data.remote;

import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.MovieList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBRetrofitService {
    String POPULARITY = "movie/popular";
    String RATING = "movie/top_rated";
    String API_KEY = "api_key";

    @GET(POPULARITY)
    Call<MovieList> getMoviesByPopularity(@Query(API_KEY) String apiKey);

    @GET(RATING)
    Call<MovieList> getMoviesByUserRating(@Query(API_KEY) String apiKey);
}
