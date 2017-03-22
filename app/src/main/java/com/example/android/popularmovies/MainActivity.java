package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEFRAGMENT_TAG = "MFTAG";

    private String mSortOrder;
    MovieFragment movieGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        movieGridFragment = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);

        // Handle action bar item clicks here
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_popular:
                Utility.setSortOrder(this, this.getString(R.string.pref_order_popular));
                break;
            case R.id.menu_item_top_rated:
                Utility.setSortOrder(this, this.getString(R.string.pref_order_top_rated));
                break;
            case R.id.menu_item_favorites:
//                Fragment ff = new FavoritesFragment();
//                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
//                fragTransaction.replace(R.id.container, ff);
//                fragTransaction.addToBackStack(null);
//                fragTransaction.commit();
                break;
        }
        movieGridFragment.updateMovies();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getDefaultSortOrder(this);

        // update the sort order using the fragment manager
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            if (null != movieGridFragment) {
                movieGridFragment.updateMovies();
            }
            mSortOrder = sortOrder;
        }
    }

        public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onItemSelected(Uri contentUri, MovieAdapter.MovieAdapterViewHolder vh) {
        Bundle args = new Bundle();
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(contentUri);
        ActivityCompat.startActivity(this, intent, args);
    }
}
