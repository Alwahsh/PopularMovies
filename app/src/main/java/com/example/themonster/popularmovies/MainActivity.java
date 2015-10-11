package com.example.themonster.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.callBack {

    private static final String MOVIEDETAILACTIVITY_TAG = "MVDT";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            if ( savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailActivityFragment(), MOVIEDETAILACTIVITY_TAG)
                        .commit();
            }
        }
        else
            mTwoPane = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putSerializable(MovieDetailActivityFragment.MOVIE_ID, movie);
            MovieDetailActivityFragment frag = new MovieDetailActivityFragment();
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, frag, MOVIEDETAILACTIVITY_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivityFragment.MOVIE_ID, movie);
            startActivity(intent);
        }
    }
}
