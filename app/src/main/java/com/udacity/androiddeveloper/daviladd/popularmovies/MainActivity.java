package com.udacity.androiddeveloper.daviladd.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.androiddeveloper.daviladd.popularmovies.adapters.PopularMoviesAdapter;
import com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        //  Associate the layout manager with the RecyclerView:
        mRecyclerView.setLayoutManager(layoutManager);
        // TODO: check if this can be left this way:
        mRecyclerView.setHasFixedSize(true);

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, createFakeMovies(20));
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        // TODO: show the progress bar while the data is being fetched and loaded
        //loadindIndicatorShow();

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
                return true;
            case R.id.menu_sort_method_rating:
                // TODO: get the new data sorted by user rating
                Toast.makeText(this,
                        getText(R.string.debug_menu_sort_method_rating),
                        Toast.LENGTH_LONG).show();
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
