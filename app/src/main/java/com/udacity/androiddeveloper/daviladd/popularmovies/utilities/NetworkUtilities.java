package com.udacity.androiddeveloper.daviladd.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();

    public static boolean isDeviceConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isDeviceConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();

        if (isDeviceConnected) {
            Log.d(TAG, "Device is connected to Internet");
        } else {
            Log.e(TAG, "Device is not connected to Internet");
        }

        return isDeviceConnected;
    }

}
