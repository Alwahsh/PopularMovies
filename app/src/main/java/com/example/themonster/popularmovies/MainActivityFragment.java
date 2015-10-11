package com.example.themonster.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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


    public MainActivityFragment() {
    }

    public final String ORDER_POPULARITY = "popularity";
    public final String ORDER_VOTES = "vote_average";


    private GridView movieGrid;
    private MoviePostersAdapter mAdapter;
    private ArrayList<Movie> movies;
    private String order = ORDER_POPULARITY;

    public interface callBack {
        void onItemClick(Movie movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        movieGrid = (GridView) view.findViewById(R.id.main_movies_grid);
        mAdapter = new MoviePostersAdapter(getActivity(),R.layout.movie_poster);
        movieGrid.setAdapter(mAdapter);
        fetchAndDisplayMovies();
        movieGrid.setOnItemClickListener(mAdapterClickListener);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private void fetchAndDisplayMovies() {
        new MoviesListFetcherTask().execute(order);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void fetchAndDisplayFavoriteMoviesFromPrefs() {
        new FavoriteMoviesListFetcherTask().execute(getActivity().getSharedPreferences(
                getString(R.string.shared_pref_key), Context.MODE_PRIVATE
        ).getStringSet(getString(R.string.shared_pref_fav_movies), new HashSet<String>()));
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
            ((callBack)getActivity()).onItemClick(movies.get(position));
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
            //fetchAndDisplayFavoriteMoviesFromPrefs();
            fetchAndDisplayFavorites();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchAndDisplayFavorites() {
        Cursor cursor = getActivity().getContentResolver().query(MoviesTable.CONTENT_URI,null,null,null,null,null);
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

    private class FavoriteMoviesListFetcherTask extends AsyncTask<Set<String>, Integer, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(Set<String>... ids) {
            return TheMovieDbInterface.FetchFavoriteMoviesList(ids[0]);
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            movies = result;
            addMoviesToAdapter();
        }
    }

}
