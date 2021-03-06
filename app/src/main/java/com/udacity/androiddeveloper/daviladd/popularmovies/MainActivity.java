package com.udacity.androiddeveloper.daviladd.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.androiddeveloper.daviladd.popularmovies.adapters.PopularMoviesAdapter;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.MovieList;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitClient;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.remote.TMDBRetrofitService;
import com.udacity.androiddeveloper.daviladd.popularmovies.databinding.ActivityMainBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.udacity.androiddeveloper.daviladd.popularmovies.utilities.NetworkUtilities.isDeviceConnectedToInternet;


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final String MOVIE_LIST_KEY = "movie_list";
    private final String SORT_METHOD_KEY = "sort_method";

    private final int DEFAULT_COLUMNS_NUMBER = 2;

    private final int SORT_METHOD_POPULARITY = 0;
    private final int SORT_METHOD_USER_RATING = 1;
    private final int SORT_METHOD_USER_FAVORITES = 2;
    private final int SORT_METHOD_DEFAULT = SORT_METHOD_POPULARITY;

    private PopularMoviesAdapter mPopularMoviesAdapter;

    private MovieList mMovieList;
    // TODO: the sorting method should be better saved as a preference!
    //private int mSortMethod;

    // For the data binding:
    private ActivityMainBinding mActivityMain;

    private MainViewModel mainViewModel;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovieList != null) {
            outState.putParcelable(MOVIE_LIST_KEY, mMovieList);
        }
        outState.putInt(SORT_METHOD_KEY, mainViewModel.getViewMode().getValue());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the UI binding:
        mActivityMain = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // Observe the viewMode:
        Log.d(TAG, "VIEWMODE: observing the MainViewModel viewMode LiveData");
        mainViewModel.getViewMode().observe(this, viewMode -> {
            if (viewMode != null) {
                Log.d(TAG, "VIEWMODE: the viewMode has been received -> updating the UI");
                callMovieAPI(viewMode);
            }
        });
        mainViewModel.getFavoriteMovies().observe(this, favoriteList -> {
            // TODO
            if (mainViewModel.getViewMode().getValue() == SORT_METHOD_USER_FAVORITES) {
                updateUI(SORT_METHOD_USER_FAVORITES, favoriteList);
            }
        });

        // TODO: the number of columns and the size of the movies' posters, should be adaptable
        //  to the device's screen characteristics
        // Create a layout manager to handle the item views on the RecyclerView:
        GridLayoutManager layoutManager = new GridLayoutManager(this, DEFAULT_COLUMNS_NUMBER);

        //  Associate the layout manager with the RecyclerView:
        mActivityMain.recyclerviewMovies.setLayoutManager(layoutManager);
        mActivityMain.recyclerviewMovies.setHasFixedSize(true);

        loadingIndicatorShow();

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, null);
        mActivityMain.recyclerviewMovies.setAdapter(mPopularMoviesAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
                Log.d(TAG, "A movie list was found in the savedInstanceState");
                mMovieList = savedInstanceState.getParcelable(MOVIE_LIST_KEY);
                loadingIndicatorHide();
                if (savedInstanceState.getInt(SORT_METHOD_KEY) == SORT_METHOD_USER_FAVORITES) {
                    updateUI(SORT_METHOD_USER_FAVORITES, mainViewModel.getFavoriteMovies().getValue());
                } else {
                    mPopularMoviesAdapter.updateAnswers(mMovieList.getResults());
                }
            }
        } else {
            // TODO: store the sorting method as a setting, so when the app is started, the user
            //      gets the same sorting method as when app was closed/destroyed.
            // callMovieAPI(SORT_METHOD_DEFAULT);
            mainViewModel.setViewMode(SORT_METHOD_DEFAULT);
            //callMovieAPI(mainViewModel.getViewMode().getValue());
        }
    }

    private void updateUI(int viewMode, List<Movie> movieList) {
        switch (viewMode) {
            case SORT_METHOD_POPULARITY:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_popularity));
                mActivityMain.tvViewMode.setText(R.string.view_mode_popularity);
                break;
            case SORT_METHOD_USER_RATING:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_rating));
                mActivityMain.tvViewMode.setText(R.string.view_mode_rating);
                break;
            case SORT_METHOD_USER_FAVORITES:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_user_favorites));
                mActivityMain.tvViewMode.setText(R.string.view_mode_favorite_movies);
                break;
            default:
                Log.d(TAG, getString(R.string.debug_menu_sort_method_unknown));
                break;
        }
        mPopularMoviesAdapter.updateAnswers(movieList);
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
                mainViewModel.setViewMode(SORT_METHOD_POPULARITY);
                callMovieAPI(SORT_METHOD_POPULARITY);
                return true;
            case R.id.menu_sort_method_rating:
                mainViewModel.setViewMode(SORT_METHOD_USER_RATING);
                callMovieAPI(SORT_METHOD_USER_RATING);
                return true;
            case R.id.menu_sort_method_favorite_movies:
                mainViewModel.setViewMode(SORT_METHOD_USER_FAVORITES);
                callMovieAPI(SORT_METHOD_USER_FAVORITES);
                return true;
            default:
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
                    mActivityMain.tvViewMode.setText(R.string.view_mode_popularity);
                    getSortedMovieList(SORT_METHOD_POPULARITY);
                    break;
                case SORT_METHOD_USER_RATING:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_rating));
                    mActivityMain.tvViewMode.setText(R.string.view_mode_rating);
                    getSortedMovieList(SORT_METHOD_USER_RATING);
                    break;
                case SORT_METHOD_USER_FAVORITES:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_user_favorites));
                    mActivityMain.tvViewMode.setText(R.string.view_mode_favorite_movies);
                    setupFavoriteMoviesViewModel();
                    break;
                default:
                    Log.d(TAG, getString(R.string.debug_menu_sort_method_unknown));
                    break;
            }
        }

        return;
    }

    private void setupFavoriteMoviesViewModel() {
        // Favorite Movies list:
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Updating the favorite movies list from the LiveData in ViewModel");
                loadingIndicatorHide();
                if (mainViewModel.getViewMode().getValue() != SORT_METHOD_USER_FAVORITES) {
                    mainViewModel.getFavoriteMovies().removeObserver(this);
                    Log.d(TAG, "Sorting method is not user favorites");
                    return;
                }
                if (movies != null) {
                    Log.d(TAG, "The following movies are now on the user's favorite list:");
                    for (Movie movie : movies) {
                        Log.d(TAG, movie.getTitle());
                    }
                    updateUI(SORT_METHOD_USER_FAVORITES, movies);
                } else {
                    Log.d(TAG, "The user's favorite list is empty");
                }
            }
        });
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
        mActivityMain.pbLoadingIndicator.setVisibility(View.GONE);
        // Show the movie items view:
        mActivityMain.recyclerviewMovies.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the loading indicator
     */
    private void loadingIndicatorShow() {
        Log.d(TAG, getString(R.string.loading_indicator_show));
        // Hide the movie items view:
        mActivityMain.recyclerviewMovies.setVisibility(View.GONE);
        // Show the loading indicator:
        mActivityMain.pbLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private class MovieListCallback implements Callback<MovieList> {
        @Override
        public void onResponse(retrofit2.Call<MovieList> call, Response<MovieList> movieListResponse) {
            loadingIndicatorHide();
            if (movieListResponse.isSuccessful()) {
                Log.d(TAG, getString(R.string.mlc_onresponse_successful));
                mMovieList = movieListResponse.body();
                // Notify the adapter that we have new data and the activity needs to be updated:
                updateUI(mainViewModel.getViewMode().getValue(), mMovieList.getResults());
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
