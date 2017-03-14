package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by noahkim on 3/8/17.
 */

public class Utility {

    public static void setSortOrder(Context context, String sort) {
        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = userPrefs.edit();
        e.putString(context.getString(R.string.pref_order_key), sort);
        e.apply();

    }

    public static String getDefaultSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_order_key),
                context.getString(R.string.pref_order_default));
    }
}
