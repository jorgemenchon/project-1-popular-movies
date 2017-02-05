package com.example.android.popularmoviesjmenchon;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.adapter.ListItemClickListener;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.adapter.MovieAdapter;
import com.example.android.popularmoviesjmenchon.util.GeneralUtils;
import com.example.android.popularmoviesjmenchon.util.MoviesDataJsonUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListMovies extends AppCompatActivity implements ListItemClickListener {

    private MovieAdapter movieAdapter;
    private GridView posterView;
    private ProgressBar loadingMovies;
    private TextView messageError;

    private static final String TAG = ListMovies.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_movies);
        findComponents();
        loadMostPopularMoviesData();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            this.movieAdapter.clear();
            loadMostPopularMoviesData();
            return true;
        } else if (id == R.id.action_top_rated) {
            this.movieAdapter.clear();
            loadTopRatedMoviesData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findComponents() {
        posterView = (GridView) findViewById(R.id.lv_movies);
        loadingMovies = (ProgressBar) findViewById(R.id.pb_loading_movies);
        messageError = (TextView) findViewById(R.id.tv_message_error);
        movieAdapter = new MovieAdapter(ListMovies.this, new ArrayList<Movie>(), ListMovies.this);
    }

    private void loadMoviesData(String location) {
        if (isOnline()) {
            FetchMoviesInfo fetcher = new FetchMoviesInfo();
            fetcher.execute(location);
        } else {
            showMessageError();
        }
    }

    private void loadTopRatedMoviesData() {
        loadMoviesData(NetworkUtils.getTopRatedMoviesUrl());
    }

    private void loadMostPopularMoviesData() {
        loadMoviesData(NetworkUtils.getPopularMoviesUrl());
    }


    @Override
    public void onListItemClick(Movie movie) {
        Log.d(TAG, "Click on movie: " + movie.getOriginalTitle());
        Context context = ListMovies.this;
        Class destinationActivity = MovieDetail.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(GeneralUtils.INTENTS_MOVIE, movie);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showLoaderMovies() {
        loadingMovies.setVisibility(View.VISIBLE);
        posterView.setVisibility(View.INVISIBLE);
        messageError.setVisibility(View.INVISIBLE);
    }

    private void showMoviesPosters() {
        loadingMovies.setVisibility(View.INVISIBLE);
        posterView.setVisibility(View.VISIBLE);
        messageError.setVisibility(View.INVISIBLE);
    }

    private void showMessageError() {
        loadingMovies.setVisibility(View.INVISIBLE);
        posterView.setVisibility(View.INVISIBLE);
        messageError.setVisibility(View.VISIBLE);
    }


    // Asynchronous data
    public class FetchMoviesInfo extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoaderMovies();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String location = params[0];
            URL buildRequestUrl = NetworkUtils.buildUrl(location);
            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(buildRequestUrl);

                List<Movie> moviesData = MoviesDataJsonUtils
                        .getMoviesFromJson(ListMovies.this, jsonMoviesResponse);
                return moviesData;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error trying to access to the info.");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> moviesData) {
            if (moviesData != null && moviesData.size() > 0) {
                showMoviesPosters();
                movieAdapter.addAll(moviesData);
                posterView.setAdapter(movieAdapter);
            } else {
                showMessageError();
            }
        }
    }
}