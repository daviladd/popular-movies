package com.udacity.androiddeveloper.daviladd.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.udacity.androiddeveloper.daviladd.popularmovies.database.FavoriteMoviesDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.udacity.androiddeveloper.daviladd.popularmovies.utilities.NetworkUtilities.isDeviceConnectedToInternet;


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final String MOVIE_LIST_KEY = "movie_list";

    private final int DEFAULT_COLUMNS_NUMBER = 2;

    private final int SORT_METHOD_POPULARITY = 0;
    private final int SORT_METHOD_USER_RATING = 1;
    private final int SORT_METHOD_USER_FAVORITES = 2;
    private final int SORT_METHOD_DEFAULT = SORT_METHOD_POPULARITY;

    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private ProgressBar mLoadingIndicator;

    private MovieList mMovieList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovieList != null) {
            outState.putParcelable(MOVIE_LIST_KEY, mMovieList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the RecyclerView and the ProgressBar:
        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // TODO: the number of columns and the size of the movies' posters, should be adaptable
        //  to the device's screen characteristics
        // Create a layout manager to handle the item views on the RecyclerView:
        GridLayoutManager layoutManager = new GridLayoutManager(this, DEFAULT_COLUMNS_NUMBER);

        //  Associate the layout manager with the RecyclerView:
        mRecyclerView.setLayoutManager(layoutManager);
        // TODO: check if this can be left this way:
        mRecyclerView.setHasFixedSize(true);

        loadingIndicatorShow();

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, null);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
                Log.d(TAG, "A movie list was found in the savedInstanceState");
                mMovieList = savedInstanceState.getParcelable(MOVIE_LIST_KEY);
                loadingIndicatorHide();
                mPopularMoviesAdapter.updateAnswers(mMovieList.getResults());
            }
        } else {
            // TODO: store the sorting method as a setting, so when the app is started, the user
            //      gets the same sorting method as when app was closed/destroyed.
            callMovieAPI(SORT_METHOD_DEFAULT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sort_method, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Handle item selection
        switch (itemId) {
            case R.id.menu_sort_method_popularity:
                callMovieAPI(SORT_METHOD_POPULARITY);
                return true;
            case R.id.menu_sort_method_rating:
                callMovieAPI(SORT_METHOD_USER_RATING);
                return true;
            case R.id.menu_sort_method_favorite_movies:
                callMovieAPI(SORT_METHOD_USER_FAVORITES);
                return true;
            default:
                Log.d(TAG, "Unknown menu option selected");
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calls the REST API to get data from the server
     *
     * @param callType Type of the call to be done
     */
    private void callMovieAPI(int callType) {
        loadingIndicatorShow();
        // First check if the device is connected to the internet
        if (!isDeviceConnectedToInternet(this)) {
            // TODO: substitute the Toast with a Snackbar maybe?
            Toast.makeText(this,
                    getString(R.string.device_not_connected),
                    Toast.LENGTH_LONG).show();
        } else {
            switch (callType) {
                case SORT_METHOD_POPULARITY:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_popularity));
                    getSortedMovieList(SORT_METHOD_POPULARITY);
                    break;
                case SORT_METHOD_USER_RATING:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_rating));
                    getSortedMovieList(SORT_METHOD_USER_RATING);
                    break;
                case SORT_METHOD_USER_FAVORITES:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_user_favorites));
                    // TODO: Retrieve the list from the FavoriteMoviesDatabase
                    FavoriteMoviesDatabase favoriteMoviesDatabase
                            = FavoriteMoviesDatabase.getInstance(getApplicationContext());
                    List<Movie> movieList = favoriteMoviesDatabase.movieDao().getAllFavoriteMovies();
                    if (movieList != null) {
                        Log.d(TAG, "The following movies are on the user's favorite list:");
                        for (Movie movie : movieList) {
                            Log.d(TAG, movie.getTitle());
                        }
                        loadingIndicatorHide();
                        mPopularMoviesAdapter.updateAnswers(movieList);
                    } else {
                        Log.d(TAG, "The user's favorite list is empty");
                    }
                    break;
                default:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_unknown));
                    break;
            }
        }

        return;
    }

    /**
     * Calls the suitable Retrofit method to get a sorted MovieList depending on the
     * sorting method selected by the user.
     *
     * @param sortingMethod Sorting method selected by the user
     */
    private void getSortedMovieList(int sortingMethod) {
        Log.d(TAG, "Trying to retrieve movies by Rating");

        String apiKey = getString(R.string.API_KEY_TMDB);
        Retrofit retrofit = TMDBRetrofitClient.getClient();
        TMDBRetrofitService apiServiceTmdb = retrofit.create(TMDBRetrofitService.class);

        Call<MovieList> call;
        switch (sortingMethod) {
            default:
            case SORT_METHOD_POPULARITY:
                call = apiServiceTmdb.getMoviesByPopularity(apiKey);
                break;
            case SORT_METHOD_USER_RATING:
                call = apiServiceTmdb.getMoviesByUserRating(apiKey);
                break;
        }
        call.enqueue(new MovieListCallback());
    }

    /**
     * Hides the loading indicator
     */
    private void loadingIndicatorHide() {
        Log.d(TAG, getString(R.string.loading_indicator_hide));
        // Hide the loading indicator:
        mLoadingIndicator.setVisibility(View.GONE);
        // Show the movie items view:
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the loading indicator
     */
    private void loadingIndicatorShow() {
        Log.d(TAG, getString(R.string.loading_indicator_show));
        // Hide the movie items view:
        mRecyclerView.setVisibility(View.GONE);
        // Show the loading indicator:
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private class MovieListCallback implements Callback<MovieList> {
        @Override
        public void onResponse(retrofit2.Call<MovieList> call, Response<MovieList> movieListResponse) {
            loadingIndicatorHide();
            if (movieListResponse.isSuccessful()) {
                Log.d(TAG, getString(R.string.mlc_onresponse_successful));
                mMovieList = movieListResponse.body();
                // Notify the adapter that we have new data and the activity needs to be updated:
                mPopularMoviesAdapter.updateAnswers(mMovieList.getResults());
            } else {
                Log.d(TAG, getString(R.string.mlc_onresponse_failure));
                int statusCode = movieListResponse.code();
                // TODO: handle error on request depending on the status code
                Log.e(TAG, movieListResponse.message());
                Toast.makeText(getApplicationContext(),
                        getString(R.string.mlc_onresponse_failure),
                        Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onFailure(retrofit2.Call<MovieList> call, Throwable t) {
            loadingIndicatorHide();
            Log.d(TAG, getString(R.string.mlc_onfailure));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.mlc_onfailure),
                    Toast.LENGTH_LONG);
        }
    }
}
