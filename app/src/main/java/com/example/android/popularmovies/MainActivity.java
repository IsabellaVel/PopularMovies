package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEFRAGMENT_TAG = "MFTAG";

    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieGridFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getDefaultSortOrder(this);

        // update the sort order using the fragment manager
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MovieGridFragment mf = (MovieGridFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if (null != mf) {
                mf.updateMovies();
            }
            mSortOrder = sortOrder;
        }
    }

//        public void setActionBarTitle(String title){
//        getSupportActionBar().setTitle(title);
//    }
}
