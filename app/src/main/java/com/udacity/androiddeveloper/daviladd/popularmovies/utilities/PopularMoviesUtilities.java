package com.udacity.androiddeveloper.daviladd.popularmovies.utilities;

import android.content.res.Configuration;
import android.content.res.Resources;

public class PopularMoviesUtilities {

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
}
