package com.example.android.popularmoviesjmenchon;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.android.popularmoviesjmenchon.util.UtilFunctions;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity implements ListItemTrailerClickListener {

    private static final String TAG = MovieDetail.class.getSimpleName();
    public static final String URI_YOUTUBE_OPTIONS = "ytpl://";

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
    @BindView(R.id.tv_no_trailers)
    TextView noTrailersMessage;
    @BindView(R.id.b_favourite)
    Button buttonFavourite;
    @BindView(R.id.b_removefavourite)
    Button buttonRemoveFavourite;
    @BindView(R.id.pb_loading_reviews)
    ProgressBar loadingTrailers;
    @BindView(R.id.pb_loading_trailers)
    ProgressBar loadingReviews;

    Movie movie;
    TrailerAdapter trailersAdapter;
    ReviewAdapter reviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        // Find all components
        ButterKnife.bind(this);
        // Get an intent which runs this activity
        Intent intent = getIntent();
        if (intent.hasExtra(GeneralUtils.INTENTS_MOVIE)) {
            movie = intent.getParcelableExtra(GeneralUtils.INTENTS_MOVIE);
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
        trailers.setHasFixedSize(true);
        trailers.setNestedScrollingEnabled(false);
        trailersAdapter = new TrailerAdapter(this, this);
        trailers.setAdapter(trailersAdapter);


        GridLayoutManager layoutManagerReviews
                = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        reviews.setLayoutManager(layoutManagerReviews);
        reviews.setHasFixedSize(true);
        reviews.setNestedScrollingEnabled(false);
        reviewsAdapter = new ReviewAdapter(this);
        reviews.setAdapter(reviewsAdapter);

    }

    private void loadFavouriteButtons(String idMovie) {
        if (isMovieAmongFavourites(idMovie)) {
            buttonFavourite.setVisibility(View.INVISIBLE);
            buttonRemoveFavourite.setVisibility(View.VISIBLE);
        } else {
            buttonFavourite.setVisibility(View.VISIBLE);
            buttonRemoveFavourite.setVisibility(View.INVISIBLE);
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
        String idMovie = movie.getId();
        String path = movie.getPosterPath();
        getTrailersFromMovieId(idMovie);
        getReviewsFromMovieId(idMovie);

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


    private void openYoutube(String keyTrailer) {
        Intent optionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI_YOUTUBE_OPTIONS + keyTrailer));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(GeneralUtils.YOUTUBE_URL + keyTrailer));
        try {
            startActivity(optionIntent);
        } catch (ActivityNotFoundException e) {
            Log.v(TAG, "Cannot open an application. Tying to open web browser");
            startActivity(webIntent);
        }
    }


    public void onClickAddFavouriteMovie(View view) {
        ContentValues contentValues = UtilFunctions.getContentValuesFromMovie(movie);
        Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, contentValues);
        if (uri != null) {
            Toast.makeText(getBaseContext(), getString(R.string.add_without_error), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.add_with_error), Toast.LENGTH_SHORT).show();
        }
        loadFavouriteButtons(movie.getId());
    }

    public void onClickRemoveFromFavouriteMovie(View view) {
        String stringId = movie.getId();
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
        uri = uri.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        if (uri != null) {
            Toast.makeText(getBaseContext(), getString(R.string.remove_without_error), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.remove_with_error), Toast.LENGTH_SHORT).show();
        }
        loadFavouriteButtons(movie.getId());
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*------------------- TRAILERS -----------------*/
    @Override
    public void onListItemClick(Trailer trailer) {
        openYoutube(trailer.getKey());
    }

    private void getTrailersFromMovieId(String id) {
        if (isOnline()) {
            String location = NetworkUtils.getTrailersUrl(id);
            FetchTrailers fetcher = new FetchTrailers(this, new FetchTrailersTaskCompleteListener());
            fetcher.execute(location);
        } else {
            showMessageErrorTrailers();
        }
    }

    public class FetchTrailersTaskCompleteListener implements AsyncTaskCompleteListener<List<Trailer>> {
        @Override
        public void onPreExecute() {
            showLoaderTrailers();
        }

        @Override
        public void onTaskComplete(List<Trailer> trailersData) {
            if (trailersData != null && trailersData.size() > 0) {
                showMoviesTrailers();
                trailersAdapter.setListTrailers(trailersData);
            } else {
                showMessageErrorTrailers();
            }
        }
    }

    private void showLoaderTrailers() {
        loadingTrailers.setVisibility(View.VISIBLE);
        trailers.setVisibility(View.INVISIBLE);
        noTrailersMessage.setVisibility(View.INVISIBLE);
    }

    private void showMoviesTrailers() {
        loadingTrailers.setVisibility(View.INVISIBLE);
        trailers.setVisibility(View.VISIBLE);
        noTrailersMessage.setVisibility(View.INVISIBLE);
    }

    private void showMessageErrorTrailers() {
        loadingTrailers.setVisibility(View.INVISIBLE);
        trailers.setVisibility(View.INVISIBLE);
        noTrailersMessage.setVisibility(View.VISIBLE);
    }


    /*----------------REVIEWS ----------------*/

    private void getReviewsFromMovieId(String id) {
        if (isOnline()) {
            String location = NetworkUtils.getReviewsUrl(id);
            FetchReviews fetcher = new FetchReviews(this, new FetchReviewsTaskCompleteListener());
            fetcher.execute(location);
        } else {
            showMessageErrorReviews();
        }
    }

    public class FetchReviewsTaskCompleteListener implements AsyncTaskCompleteListener<ArrayList<Review>> {
        @Override
        public void onPreExecute() {
            showLoaderReviews();
        }

        @Override
        public void onTaskComplete(ArrayList<Review> reviewsData) {
            if (reviewsData != null && reviewsData.size() > 0) {
                showMoviesReviews();
                reviewsAdapter.setListReviews(reviewsData);
            } else {
                showMessageErrorReviews();
            }
        }
    }

    private void showLoaderReviews() {
        loadingReviews.setVisibility(View.VISIBLE);
        reviews.setVisibility(View.INVISIBLE);
        noReviewsMessage.setVisibility(View.INVISIBLE);
    }

    private void showMoviesReviews() {
        loadingReviews.setVisibility(View.INVISIBLE);
        reviews.setVisibility(View.VISIBLE);
        noReviewsMessage.setVisibility(View.INVISIBLE);
    }

    private void showMessageErrorReviews() {
        loadingReviews.setVisibility(View.INVISIBLE);
        reviews.setVisibility(View.INVISIBLE);
        noReviewsMessage.setVisibility(View.VISIBLE);
    }

}
