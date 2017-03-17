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
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class ListMovies extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    private MovieAdapter movieAdapter;
    private CustomCursorAdapter favouriteMoviesAdapter;

    @BindView(R.id.rv_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_movies)
    ProgressBar loadingMovies;
    @BindView(R.id.tv_message_error)
    TextView messageError;
    private final String MOVIES_SAVED = "moviesSaved";
    private final int DURATION_EFECT_ADAPTER_MILI = 1000;
    public static final int MOVIE_LOADER_ID = 0;

    private static final String TAG = ListMovies.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_movies);
        // Find all components
        ButterKnife.bind(this);
        //LinearLayoutManager can support HORIZONTAL or VERTICAL orientations.
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, GeneralUtils.NUM_COLUM, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        //Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, this);
        favouriteMoviesAdapter = new CustomCursorAdapter(this, this);

        AlphaInAnimationAdapter movieAdapterWithAnimation = new AlphaInAnimationAdapter(movieAdapter);
        //Disabling the first scroll mode.
        movieAdapterWithAnimation.setFirstOnly(false);
        movieAdapterWithAnimation.setDuration(DURATION_EFECT_ADAPTER_MILI);
        //https://github.com/wasabeef/recyclerview-animators
        mRecyclerView.setAdapter(movieAdapterWithAnimation);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_SAVED)) {
            showMoviesPosters();
            ArrayList<Movie> moviesData = savedInstanceState.getParcelableArrayList(MOVIES_SAVED);
            movieAdapter.setListMovies(moviesData);
        } else {
            loadFavouriteMovies();
            // loadFavouriteMovies();
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
            mRecyclerView.setAdapter(favouriteMoviesAdapter);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> moviesData = movieAdapter.getListMovies();
        outState.putParcelableArrayList(MOVIES_SAVED, moviesData);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by id
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                            null,
                            null,
                            null,
                            MoviesContract.MovieEntry.COLUMN_ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        showMoviesPosters();
        favouriteMoviesAdapter.swapCursor(data);

    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favouriteMoviesAdapter.swapCursor(null);
    }


}