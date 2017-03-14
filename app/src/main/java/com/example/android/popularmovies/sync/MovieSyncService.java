package com.example.android.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by noahkim on 3/13/17.
 */

public class MovieSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MoviesSyncService", "onCreate - MoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }

}
