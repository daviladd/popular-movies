package com.udacity.androiddeveloper.daviladd.popularmovies.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

public class PopularMoviesUtilities {
    // TODO: finish this class to get the better configuration for the grid depending on the
    //  device's screen size.

    public final static String TMDB_API_THUMBNAIL_PATH = "http://image.tmdb.org/t/p/w500";

    public static final int DEFAULT_ROWS = 2;
    public static final int MAX_MOVIE_THUMBNAIL_ITEM_WIDTH = 500;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenOrientation() {
        return Resources.getSystem().getConfiguration().orientation;
    }

    public static int getNumberOfRows() {
        int rows = 2;
        int screenOrientation = getScreenOrientation();

        switch (screenOrientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                break;
        }
        return rows;
    }

    public static void launchTrailerVideoInYoutubeApp(@NonNull Context context, String videoID) {
        // Create the intent to launch the activity to show this trailer:
        Intent launchTrailerVideoInYoutube
                = new Intent(Intent.ACTION_VIEW, Uri.parse(("vnd.youtube://" + videoID)));

        // Launch the Youtube app to watch the movie trailer:
        context.startActivity(launchTrailerVideoInYoutube);
    }

    public static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    public static void launchTrailerVideoInYoutubeBrowser(@NonNull Context context, String videoID) {
        // Create the intent to launch the activity to show this trailer:
        Intent launchTrailerVideoInYoutube
                = new Intent(Intent.ACTION_VIEW, Uri.parse((BASE_URL_YOUTUBE + videoID)));

        // Launch the Youtube app to watch the movie trailer:
        context.startActivity(launchTrailerVideoInYoutube);
    }
}
