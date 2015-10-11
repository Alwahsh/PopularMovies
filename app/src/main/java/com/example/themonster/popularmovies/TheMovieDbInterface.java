package com.example.themonster.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.auth.AUTH;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by themonster on 9/29/15.
 */
public class TheMovieDbInterface {


    public static final String LOG_TAG = "FetcherLog";
    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    public static final String API_KEY_PARAM = "api_key";
    //TODO: PUT YOUR API KEY HERE
    public static final String API_KEY = "PUTYOURKEYHERE";
    public static final String BASE_IMG_URL_BIG = "http://image.tmdb.org/t/p/w500";
    public static final String BASE_IMG_URL_SMALL = "http://image.tmdb.org/t/p/w342";

    // API Result keys
    public static final String RESULTS_MOVIE_KEY = "results";
    public static final String TITLE_MOVIE_KEY = "title";
    public static final String POSTER_PATH_MOVIE_KEY = "poster_path";
    public static final String ID_MOVIE_KEY = "id";
    public static final String RELEASE_DATE_MOVIE_KEY = "release_date";
    public static final String VOTE_AVERAGE_MOVIE_KEY = "vote_average";
    public static final String OVERVIEW_MOVIE_KEY = "overview";


    //EXTERNAL: This function is copied from Sunshine app and modified to the need of this app.
    public static ArrayList<Movie> FetchMoviesList(String orderCriteria) {
        Log.d(LOG_TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesListStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            final String moviesUrl = API_BASE_URL + "/discover/movie?";
            final String SORT_BY_PARAM = "sort_by";

            Uri builtUri = Uri.parse(moviesUrl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(SORT_BY_PARAM, orderCriteria+".desc")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesListStr = buffer.toString();
            return getMovieTinyDetailsFromJson(moviesListStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    //EXTERNAL: This function is copied from Sunshine app and modified to the need of this app.
    public static ArrayList<Movie> FetchFavoriteMoviesList(Set<String> ids) {
        Log.d(LOG_TAG, "Starting sync");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesListStr = null;

        ArrayList<Movie> res = new ArrayList<Movie>();

        for (String movieId : ids) {
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String moviesUrl = API_BASE_URL + "/movie/" + movieId + "?";
                final String SORT_BY_PARAM = "sort_by";

                Uri builtUri = Uri.parse(moviesUrl).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    continue;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    continue;
                }
                moviesListStr = buffer.toString();
                res.add(getFullMovieDetailsFromJson(moviesListStr));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
        return res;
    }

    //EXTERNAL: This function is copied from Sunshine app and modified to the need of this app.
    public static ArrayList<Object> FetchMovieTrailers(String movieId) {

        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieTrailersStr = null;

        try {
            final String movieUrl = API_BASE_URL + "/movie/" + movieId + "/videos?";

            Uri builtUri = Uri.parse(movieUrl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());


            // Create the request to TheMovieDb API, and open the connection.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieTrailersStr = buffer.toString();
            return getMovieTrailersFromJson(movieTrailersStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    //EXTERNAL: This function is copied from Sunshine app and modified to the need of this app.
    public static ArrayList<Object> FetchMovieReviews(String movieId) {

        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieTrailersStr = null;

        try {
            final String movieUrl = API_BASE_URL + "/movie/" + movieId + "/reviews?";

            Uri builtUri = Uri.parse(movieUrl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());


            // Create the request to TheMovieDb API, and open the connection.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieTrailersStr = buffer.toString();
            return getMovieReviewsFromJson(movieTrailersStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private static ArrayList<Object> getMovieReviewsFromJson(String movieReviewsStr)  throws JSONException{
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject movieReviewsJson = new JSONObject(movieReviewsStr);
        JSONArray movieReviews = movieReviewsJson.getJSONArray(RESULTS_MOVIE_KEY);
        ArrayList<Object> res = new ArrayList<Object>();

        for (int i = movieReviews.length()-1; i >= 0 ; i--) {
            JSONObject cur = movieReviews.getJSONObject(i);
                res.add(new Review(cur.getString(AUTHOR), cur.getString(CONTENT)));
        }

        return res;
    }

    private static ArrayList<Object> getMovieTrailersFromJson(String movieTrailersStr) throws JSONException{
        final String TYPE = "type";
        final String TRAILER = "Trailer";
        final String SITE = "site";
        final String YOUTUBE = "YouTube";
        final String NAME = "name";
        final String KEY = "key";

        JSONObject movieTrailersJson = new JSONObject(movieTrailersStr);
        JSONArray movieTrailers = movieTrailersJson.getJSONArray(RESULTS_MOVIE_KEY);
        ArrayList<Object> res = new ArrayList<Object>();
        for (int i = movieTrailers.length()-1; i >= 0 ; i--) {
            JSONObject cur = movieTrailers.getJSONObject(i);
            if (cur.getString(TYPE).equals(TRAILER) && cur.getString(SITE).equals(YOUTUBE))
                res.add(new Trailer(cur.getString(NAME), cur.getString(KEY)));
        }

        return res;
    }

    private static Movie getFullMovieDetailsFromJson(String movieDetailsStr) throws JSONException {
        Movie res = new Movie();
        JSONObject movieDetailsJson = new JSONObject(movieDetailsStr);
        res.title = movieDetailsJson.getString(TITLE_MOVIE_KEY);
        res.id = movieDetailsJson.getString(ID_MOVIE_KEY);
        res.poster = movieDetailsJson.getString(POSTER_PATH_MOVIE_KEY);
        res.date = movieDetailsJson.getString(RELEASE_DATE_MOVIE_KEY);
        res.vote_average = movieDetailsJson.getString(VOTE_AVERAGE_MOVIE_KEY);
        res.plot_synopsis = movieDetailsJson.getString(OVERVIEW_MOVIE_KEY);
        return res;
    }

    private static ArrayList<Movie> getMovieTinyDetailsFromJson(String moviesListStr) throws JSONException {
        JSONObject moviesListJson = new JSONObject(moviesListStr);
        JSONArray moviesList = moviesListJson.getJSONArray(RESULTS_MOVIE_KEY);

        ArrayList<Movie> res = new ArrayList<Movie>(moviesList.length());
        for (int i = 0; i < moviesList.length(); i++) {

            res.add(i, getFullMovieDetailsFromJson(moviesList.getString(i)));
        }

        return res;
    }

}
