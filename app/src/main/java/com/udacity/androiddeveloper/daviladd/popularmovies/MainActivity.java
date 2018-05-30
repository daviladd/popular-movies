package com.udacity.androiddeveloper.daviladd.popularmovies;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.androiddeveloper.daviladd.popularmovies.adapters.PopularMoviesAdapter;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.MovieList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitClient;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private PopularMoviesAdapter mPopularMoviesAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the RecyclerView and the ProgressBar:
        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Create a layout manager to handle the item views on the RecyclerView:
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        //  Associate the layout manager with the RecyclerView:
        mRecyclerView.setLayoutManager(layoutManager);
        // TODO: check if this can be left this way:
        mRecyclerView.setHasFixedSize(true);

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, createFakeMovies(20));
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        // TODO: show the progress bar while the data is being fetched and loaded
        loadindIndicatorShow();

        getMoviesByPopularity();

    }

    private void getMoviesByPopularity(){
        Log.d(TAG, "Trying to retrieve movies by Popularity");
        Retrofit retrofit = TMDBRetrofitClient.getClient();

        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);
        String apiKey = getString(R.string.API_KEY_TMDB);

        Call<MovieList> call = apiServiceTmdb
                .getMoviesByPopularity(apiKey);
        call.enqueue(new Callback<MovieList>() {
                    @Override
                    public void onResponse(retrofit2.Call<MovieList> call, Response<MovieList> movieListResponse) {
                        loadindIndicatorHide();
                        if (movieListResponse.isSuccessful()) {
                            Log.d(TAG, "List of movies sorted by popularity has been successfully retrieved");
                            mPopularMoviesAdapter.updateAnswers(movieListResponse.body().getResults());
                        } else {
                            Log.d(TAG, "List of movies sorted by popularity could not be retrieved");
                            int statusCode = movieListResponse.code();
                            // TODO: handle error on request depending on the status code
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MovieList> call, Throwable t) {
                        loadindIndicatorHide();
                        Log.d(TAG, "Error while loading from TMDB API while trying to retrieve movies by popularity");
                    }
                });
    }

    private void getMoviesByUserRating(){
        Log.d(TAG, "Trying to retrieve movies by Rating");
        Retrofit retrofit = TMDBRetrofitClient.getClient();

        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);
        String apiKey = getString(R.string.API_KEY_TMDB);

        Call<MovieList> call = apiServiceTmdb
                .getMoviesByUserRating(apiKey);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(retrofit2.Call<MovieList> call, Response<MovieList> movieListResponse) {
                loadindIndicatorHide();
                if (movieListResponse.isSuccessful()) {
                    Log.d(TAG, "List of movies sorted by user rating has been successfully retrieved");
                    mPopularMoviesAdapter.updateAnswers(movieListResponse.body().getResults());
                } else {
                    Log.d(TAG, "List of movies sorted by user rating could not be retrieved");
                    int statusCode = movieListResponse.code();
                    // TODO: handle error on request depending on the status code
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MovieList> call, Throwable t) {
                loadindIndicatorHide();
                Log.d(TAG, "Error while loading from TMDB API while trying to retrieve movies by user rating");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sort_method, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_sort_method_popularity:
                // TODO: get the new data sorted by popularity
                Toast.makeText(this,
                        getText(R.string.debug_menu_sort_method_popularity),
                        Toast.LENGTH_LONG).show();
                getMoviesByPopularity();
                return true;
            case R.id.menu_sort_method_rating:
                // TODO: get the new data sorted by user rating
                Toast.makeText(this,
                        getText(R.string.debug_menu_sort_method_rating),
                        Toast.LENGTH_LONG).show();
                getMoviesByUserRating();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadindIndicatorShow() {
        // Hide the movie items view:
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Show the loading indicator:
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the loading indicator in order to show the
     */
    private void loadindIndicatorHide() {
        // Hide the loading indicator:
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Show the movie items view:
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    List<Movie> createFakeMovies(int quantity) {
        List<Movie> movies = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            Movie movie = new Movie();
            movie.setTitle("Movie " + i);
            movies.add(movie);
        }

        return movies;
    }
}
