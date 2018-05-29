package com.udacity.androiddeveloper.daviladd.popularmovies.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDBRetrofitClient {
    private static Retrofit retrofit = null;

    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    static Retrofit getClient() {

        if (null == retrofit) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


}
