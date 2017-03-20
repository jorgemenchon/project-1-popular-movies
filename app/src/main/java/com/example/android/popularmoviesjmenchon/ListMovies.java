package com.example.android.popularmoviesjmenchon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.adapter.CustomCursorAdapter;
import com.example.android.popularmoviesjmenchon.adapter.movies.ListItemClickListener;
import com.example.android.popularmoviesjmenchon.aynctasks.AsyncTaskCompleteListener;
import com.example.android.popularmoviesjmenchon.aynctasks.FetchMoviesInfo;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.adapter.movies.MovieAdapter;
import com.example.android.popularmoviesjmenchon.model.MoviesContract;
import com.example.android.popularmoviesjmenchon.util.GeneralUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.example.android.popularmoviesjmenchon.util.UtilFunctions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListMovies extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIES_SAVED = "moviesSaved";
    public static final int MOVIE_LOADER_ID = 0;

    private MovieAdapter movieAdapter;
    private CustomCursorAdapter favouriteMoviesAdapter;

    @BindView(R.id.rv_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_movies)
    ProgressBar loadingMovies;
    @BindView(R.id.tv_message_error)
    TextView messageError;


    private static final String TAG = ListMovies.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_movies);
        // Find all components
        ButterKnife.bind(this);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, GeneralUtils.NUM_COLUM, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, this);
        favouriteMoviesAdapter = new CustomCursorAdapter(this, this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIES_SAVED)) {
                showMoviesPosters();
                ArrayList<Movie> moviesData = savedInstanceState.getParcelableArrayList(MOVIES_SAVED);
                movieAdapter.setListMovies(moviesData);
                mRecyclerView.setAdapter(movieAdapter);
            } else {
                mRecyclerView.setAdapter(favouriteMoviesAdapter);
            }
        } else {
            loadFavouriteMovies();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            if (!UtilFunctions.isEmpty(this.movieAdapter.getListMovies())) {
                this.movieAdapter.getListMovies().clear();
            }
            loadMostPopularMoviesData();
            return true;
        } else if (id == R.id.action_top_rated) {
            if (!UtilFunctions.isEmpty(this.movieAdapter.getListMovies())) {
                this.movieAdapter.getListMovies().clear();
            }
            loadTopRatedMoviesData();
            return true;
        } else if (id == R.id.action_favourites) {
            if (!UtilFunctions.isEmpty(this.movieAdapter.getListMovies())) {
                this.movieAdapter.getListMovies().clear();
            }
            loadFavouriteMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesData(String location) {
        if (isOnline()) {
            mRecyclerView.setAdapter(movieAdapter);
            FetchMoviesInfo fetcher = new FetchMoviesInfo(ListMovies.this, new FetchMoviesTaskCompleteListener());
            fetcher.execute(location);
        } else {
            showMessageError();
        }
    }

    private void loadFavouriteMovies() {
        String title = getString(R.string.app_name) + " (" + getString(R.string.favourites_title) + ")";
        setTitle(title);
        mRecyclerView.setAdapter(favouriteMoviesAdapter);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private void loadTopRatedMoviesData() {
        String title = getString(R.string.app_name) + " (" + getString(R.string.top_rated_title) + ")";
        setTitle(title);
        loadMoviesData(NetworkUtils.getTopRatedMoviesUrl());
    }

    private void loadMostPopularMoviesData() {
        String title = getString(R.string.app_name) + " (" + getString(R.string.popular_title) + ")";
        setTitle(title);
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

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> moviesData = movieAdapter.getListMovies();
        if (!UtilFunctions.isEmpty(moviesData)) {
            outState.putParcelableArrayList(MOVIES_SAVED, moviesData);
        }
        //In other case it means that we are viewing favourites and we don´t need to send anything. Loader
    }

    /*----------------------------------- FETCH FROM API --------------------------*/

    public class FetchMoviesTaskCompleteListener implements AsyncTaskCompleteListener<ArrayList<Movie>> {
        @Override
        public void onPreExecute() {
            showLoaderMovies();
        }

        @Override
        public void onTaskComplete(ArrayList<Movie> moviesData) {
            if (moviesData != null && moviesData.size() > 0) {
                showMoviesPosters();
                movieAdapter.setListMovies(moviesData);
            } else {
                showMessageError();
            }
        }
    }

    /*------------------------------------ FETCH FROM DB ------------------------*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                            null,
                            null,
                            null,
                            MoviesContract.MovieEntry.COLUMN_ID);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favouriteMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favouriteMoviesAdapter.swapCursor(null);
    }


    /*--------- UTIL METHODS ---------------*/

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showLoaderMovies() {
        loadingMovies.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        messageError.setVisibility(View.INVISIBLE);
    }

    private void showMoviesPosters() {
        loadingMovies.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        messageError.setVisibility(View.INVISIBLE);
    }

    private void showMessageError() {
        loadingMovies.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        messageError.setVisibility(View.VISIBLE);
    }
}