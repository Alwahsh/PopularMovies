package com.example.themonster.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public static final String MOVIE_ID = "movie_id";

    private Activity mActivity = null;

    public MovieDetailActivityFragment() {
    }

    public interface withShare {
        void setShareIntent(Intent shareIntent);
    }

    private View view;
    private Movie movie;
    private MovieTrailersAdapter trailersAdapter;
    private MovieReviewsAdapter reviewsAdapter;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private ListView trailersListView;
    private ListView reviewsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Bundle arg = getArguments();
        if (arg != null) {
            movie = arg.getParcelable(MOVIE_ID);
            if (mActivity != null) {
                trailersAdapter = new MovieTrailersAdapter(mActivity, R.layout.trailer);
                trailersListView = (ListView) view.findViewById(R.id.trailersList);
                trailersListView.setAdapter(trailersAdapter);
                trailersListView.setOnItemClickListener(trailersClickListener);
                reviewsAdapter = new MovieReviewsAdapter(mActivity, R.layout.review);
                reviewsListView = (ListView) view.findViewById(R.id.reviewsList);
                reviewsListView.setAdapter(reviewsAdapter);
                DisplayMovieDetails();
                fetchAndDisplayMovieTrailers();
                fetchAndDisplayMovieReviews();
            }
            Button button = (Button) view.findViewById(R.id.add_to_favorites);
            if (exists())
                button.setVisibility(View.GONE);
            else
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMovieToDB();
                        //addMovieToFav();
                    }
                });
        } else {
            view.findViewById(R.id.movie_details_extras).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    private boolean exists() {
        if (mActivity == null)
            return false;
        Cursor cursor = mActivity.getContentResolver().query(MoviesTable.CONTENT_URI,null,"col_id = " + movie.id,null,null);
        return cursor.getCount() > 0;
    }

    private void addMovieToDB() {
        if (mActivity == null)
            return;
        mActivity.getContentResolver().insert(MoviesTable.CONTENT_URI,MoviesTable.getContentValues(movie,true));
        view.findViewById(R.id.add_to_favorites).setVisibility(View.GONE);
    }


    private AdapterView.OnItemClickListener trailersClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openYoutubeVideo(trailers.get(position));
        }
    };

    public void openYoutubeVideo(Trailer trailer){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getYoutubeLink()));
        startActivity(intent);
    }

    private void fetchAndDisplayMovieTrailers() {
        new MovieTrailersFetcherTask().execute(movie.id);
    }

    private void fetchAndDisplayMovieReviews() {
        new MovieReviewsFetcherTask().execute(movie.id);
    }

    void DisplayMovieDetails() {
        Picasso.with(mActivity).load(TheMovieDbInterface.BASE_IMG_URL_SMALL + movie.poster).placeholder(mActivity.getResources().getDrawable(R.drawable.ic_default_poster)).error(mActivity.getResources().getDrawable(R.drawable.ic_default_poster)).into((ImageView) view.findViewById(R.id.movie_detail_poster));
        ((TextView)view.findViewById(R.id.movie_details_title)).setText(movie.title);
        ((TextView)view.findViewById(R.id.movie_detail_date)).setText(movie.date);
        ((TextView)view.findViewById(R.id.movie_detail_rating)).setText(movie.vote_average);
        ((TextView)view.findViewById(R.id.movie_detail_summary)).setText(movie.plot_synopsis);
    }

    // EXTERNAL: I got this method from the internet. It adjusts the height of the ListView.
    // Without using it, the ListView would only show one item because it's inside a ScrollView.
    // This workaround calculates the needed height and sets the ListView to that.
    // Source: nex-otaku-en.blogspot.com.eg/2010/12/android-put-listview-in-scrollview.html
    private void adjustListViewHeight(ListView listView, ArrayAdapter adp) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < adp.getCount(); i++) {
            View listItem = adp.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adp.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void addToShare(Intent shareIntent) {
        if (mActivity == null)
            return;
        try {
            ((withShare) mActivity).setShareIntent(shareIntent);
        } catch (ClassCastException e) {}
    }

    private class MovieTrailersFetcherTask extends AsyncTask<String, Integer, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(String... movie) {
            return TheMovieDbInterface.FetchMovieTrailers(movie[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            trailers = (ArrayList) result;
            trailersAdapter.clear();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            if (trailers == null || trailers.size() <= 0) {
                view.findViewById(R.id.movie_details_trailers_section).setVisibility(View.GONE);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "This movie has no trailers");
                addToShare(shareIntent);
                return;
            }
            addToShare(shareIntent);
            shareIntent.putExtra(Intent.EXTRA_TEXT, trailers.get(0).getYoutubeLink());
            for (int i = trailers.size()-1; i >= 0; i--) {
                trailersAdapter.add((trailers.get(i)).name);
            }
            adjustListViewHeight(trailersListView, trailersAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class MovieReviewsFetcherTask extends AsyncTask<String, Integer, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(String... movie) {
            return TheMovieDbInterface.FetchMovieReviews(movie[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Object> results) {
            reviews = (ArrayList) results;
            reviewsAdapter.clear();
            if (reviews == null || reviews.size() <= 0) {
                view.findViewById(R.id.movie_details_reviews_section).setVisibility(View.GONE);
                return;
            }
            for (int i = 0; i < reviews.size(); i++) {
                reviewsAdapter.add((reviews.get(i)));
            }
            adjustListViewHeight(reviewsListView, reviewsAdapter);
        }
    }

}
