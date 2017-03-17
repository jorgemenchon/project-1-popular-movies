package com.example.android.popularmoviesjmenchon;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesjmenchon.adapter.reviews.ReviewAdapter;
import com.example.android.popularmoviesjmenchon.adapter.trailers.ListItemTrailerClickListener;
import com.example.android.popularmoviesjmenchon.adapter.trailers.TrailerAdapter;
import com.example.android.popularmoviesjmenchon.aynctasks.AsyncTaskCompleteListener;
import com.example.android.popularmoviesjmenchon.aynctasks.FetchReviews;
import com.example.android.popularmoviesjmenchon.aynctasks.FetchTrailers;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.model.MoviesContract;
import com.example.android.popularmoviesjmenchon.model.Review;
import com.example.android.popularmoviesjmenchon.model.Trailer;
import com.example.android.popularmoviesjmenchon.util.GeneralUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class MovieDetail extends AppCompatActivity implements ListItemTrailerClickListener {

    private String id;
    private String path;

    @BindView(R.id.tv_original_title)
    TextView mOriginalTitle;
    @BindView(R.id.tv_overview)
    TextView mOverview;
    @BindView(R.id.tv_rating)
    TextView mRating;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.iv_poster_detail)
    ImageView posterDetail;
    @BindView(R.id.rv_trailers)
    RecyclerView trailers;
    @BindView(R.id.rv_reviews)
    RecyclerView reviews;
    @BindView(R.id.tv_no_reviews)
    TextView noReviewsMessage;
    @BindView(R.id.b_favourite)
    Button buttonFavourite;
    @BindView(R.id.b_unfavourite)
    Button buttonUnfavourite;


    TrailerAdapter trailersAdapter;
    ReviewAdapter reviewsAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        // Find all components
        ButterKnife.bind(this);
        // Get an intent which runs this activity
        Intent intent = getIntent();
        if (intent.hasExtra(GeneralUtils.INTENTS_MOVIE)) {
            Movie movie = intent.getParcelableExtra(GeneralUtils.INTENTS_MOVIE);
            initializeMovieDetails(movie);
            loadFavouriteButtons(movie.getId());
        }
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);

        trailers.setLayoutManager(layoutManager);
        //Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        trailers.setHasFixedSize(true);

        trailersAdapter = new TrailerAdapter(this, this);

        AlphaInAnimationAdapter movieAdapterWithAnimation = new AlphaInAnimationAdapter(trailersAdapter);
        //Disabling the first scroll mode.
        movieAdapterWithAnimation.setFirstOnly(false);
        //movieAdapterWithAnimation.setDuration(DURATION_EFECT_ADAPTER_MILI);
        //https://github.com/wasabeef/recyclerview-animators
        trailers.setAdapter(movieAdapterWithAnimation);


        GridLayoutManager layoutManagerReviews
                = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        reviews.setLayoutManager(layoutManagerReviews);
        //Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        reviews.setHasFixedSize(true);
        // This disable the scroll
        reviews.setNestedScrollingEnabled(false);

        reviewsAdapter = new ReviewAdapter(this);
        reviews.setAdapter(reviewsAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void loadFavouriteButtons(String idMovie) {
        if (isMovieAmongFavourites(idMovie)) {
            buttonFavourite.setVisibility(View.INVISIBLE);
            buttonUnfavourite.setVisibility(View.VISIBLE);
        } else {
            buttonFavourite.setVisibility(View.VISIBLE);
            buttonUnfavourite.setVisibility(View.INVISIBLE);
        }
    }


    private boolean isMovieAmongFavourites(String idMovie) {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
        uri = uri.buildUpon().appendPath(idMovie).build();

        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    private void initializeMovieDetails(Movie movie) {
        String originalTitle = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String average = movie.getVoteAverage();
        String averageComplete = average + GeneralUtils.AVERAGE_TOTAL;
        String releaseDate = movie.getReleaseDate();
        //String id = movie.getId();
        id = movie.getId();
        path = movie.getPosterPath();
        getTrailersFromMovieId(id);
        getReviewsFromMovieId(id);

        this.mOriginalTitle.setText(originalTitle);
        this.mOverview.setText(overview);
        this.mRating.setText(averageComplete);
        this.mReleaseDate.setText(releaseDate);

        Picasso.with(this)
                .load(NetworkUtils.getMoviePosterUrl(GeneralUtils.POSTER_WIDTH, path))
                .into(this.posterDetail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    private void getTrailersFromMovieId(String id) {
        String location = NetworkUtils.getTrailersUrl(id);
        URL buildRequestUrl = NetworkUtils.buildUrl(location);

        FetchTrailers fetcher = new FetchTrailers(this, new FetchTrailersTaskCompleteListener());
        fetcher.execute(location);
    }


    private void getReviewsFromMovieId(String id) {
        String location = NetworkUtils.getReviewsUrl(id);
        URL buildRequestUrl = NetworkUtils.buildUrl(location);

        FetchReviews fetcher = new FetchReviews(this, new FetchReviewsTaskCompleteListener());
        fetcher.execute(location);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MovieDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    // Fetch trailers
    public class FetchTrailersTaskCompleteListener implements AsyncTaskCompleteListener<List<Trailer>> {
        @Override
        public void onPreExecute() {
            //TODO do something
        }

        @Override
        public void onTaskComplete(List<Trailer> trailersData) {
            if (trailersData != null && trailersData.size() > 0) {
                //  showMoviesPosters();
                trailersAdapter.setListTrailers(trailersData);
            } else {
                // showMessageError();
            }
        }
    }

    //Fetch reviews

    public class FetchReviewsTaskCompleteListener implements AsyncTaskCompleteListener<ArrayList<Review>> {
        @Override
        public void onPreExecute() {
            //TODO do something
        }

        @Override
        public void onTaskComplete(ArrayList<Review> reviewsData) {
            if (reviewsData != null && reviewsData.size() > 0) {
                //  showMoviesPosters();
                reviewsAdapter.setListReviews(reviewsData);
            } else {
                // showMessageError();
                noReviewsMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openYoutube(String keyTrailer) {
        //    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("ytpl://" + keyTrailer));
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + keyTrailer));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + keyTrailer));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            startActivity(webIntent);
        }
    }


    @Override
    public void onListItemClick(Trailer trailer) {
        openYoutube(trailer.getKey());
    }

    public void onClickAddFavouriteMovie(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, id);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mOriginalTitle.getText().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mOverview.getText().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, path);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate.getText().toString());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, mRating.getText().toString());

        Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, contentValues);
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
        }
        loadFavouriteButtons(id);
       // finish();

    }

    public void onClickUnfavouriteMovie(View view) {
        // Build appropriate uri with String row id appended
        String stringId = id;
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
        uri = uri.buildUpon().appendPath(stringId).build();

        // COMPLETED (2) Delete a single row of data using a ContentResolver
        getContentResolver().delete(uri, null, null);
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
        }
        loadFavouriteButtons(id);
        // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
        // getSupportLoaderManager().restartLoader(ListMovies.MOVIE_LOADER_IDnull, ListMovies.this);
        // o, nRestart();
    }
}
