package com.example.themonster.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    public static final String SS_ORDER = "SS_ORDER";
    private static final String SS_MVGRDSP = "SS_MVGRDSP";
    private static final String SS_MVS = "SS_MVS";

    public MainActivityFragment() {
    }

    public final String ORDER_POPULARITY = "popularity";
    public final String ORDER_VOTES = "vote_average";
    private static final String ORDER_FAVORITES = "favorites";


    private GridView movieGrid;
    private MoviePostersAdapter mAdapter;
    private ArrayList<Movie> movies;
    private String order = ORDER_POPULARITY;
    private Activity mActivity = null;

    public interface callBack {
        void onItemClick(Movie movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        movieGrid = (GridView) view.findViewById(R.id.main_movies_grid);
        if (mActivity != null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(SS_ORDER))
                order = savedInstanceState.getString(SS_ORDER);
            mAdapter = new MoviePostersAdapter(mActivity, R.layout.movie_poster);
            movieGrid.setAdapter(mAdapter);
            if (savedInstanceState != null && savedInstanceState.containsKey(SS_MVS)) {
                movies = (ArrayList<Movie>) savedInstanceState.getSerializable(SS_MVS);
                addMoviesToAdapter();
                movieGrid.setSelection(savedInstanceState.getInt(SS_MVGRDSP));
            } else
                fetchAndDisplayMovies();
            movieGrid.setOnItemClickListener(mAdapterClickListener);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SS_ORDER, order);
        outState.putSerializable(SS_MVS, movies);
        outState.putInt(SS_MVGRDSP, movieGrid.getFirstVisiblePosition());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    //Based on a stackoverflow snippet(Taken from reviewer.)
    private boolean isNetworkAvailable() {
        if(mActivity == null)
            return false;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void fetchAndDisplayMovies() {
        if (isNetworkAvailable() && order != ORDER_FAVORITES)
            new MoviesListFetcherTask().execute(order);
        else
            fetchAndDisplayFavorites();
    }

    private void addMoviesToAdapter() {
        mAdapter.clear();
        if (movies == null)
            fetchAndDisplayFavorites();
        else
            for (int i = 0; i < movies.size(); i++)
                mAdapter.add(TheMovieDbInterface.BASE_IMG_URL_BIG +movies.get(i).poster);
    }

    private AdapterView.OnItemClickListener mAdapterClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((callBack)mActivity).onItemClick(movies.get(position));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_most_popular) {
            order = ORDER_POPULARITY;
            fetchAndDisplayMovies();
            return true;
        } else if (id == R.id.action_highest_rated) {
            order = ORDER_VOTES;
            fetchAndDisplayMovies();
            return true;
        } else if (id == R.id.action_favorite) {
            order = ORDER_FAVORITES;
            //fetchAndDisplayFavoriteMoviesFromPrefs();
            fetchAndDisplayFavorites();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchAndDisplayFavorites() {
        Cursor cursor = null;
        if (mActivity != null)
            cursor = mActivity.getContentResolver().query(MoviesTable.CONTENT_URI, null, null, null, null, null);
        if (cursor == null)
            movies = new ArrayList<Movie>();
        else
            movies = new ArrayList<Movie>(MoviesTable.getRows(cursor,false));
        addMoviesToAdapter();
    }

    private class MoviesListFetcherTask extends AsyncTask<String, Integer, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... orderCriteria) {
            return TheMovieDbInterface.FetchMoviesList(orderCriteria[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            movies = result;
            addMoviesToAdapter();
        }
    }

}
