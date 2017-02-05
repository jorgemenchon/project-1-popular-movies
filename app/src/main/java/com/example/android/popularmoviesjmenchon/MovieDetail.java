package com.example.android.popularmoviesjmenchon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.util.GeneralUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;
    private ImageView posterDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        findComponents();
        // Get an intent which runs this activity
        Intent intent = getIntent();
        if (intent.hasExtra(GeneralUtils.INTENTS_MOVIE)) {
            Movie movie = intent.getParcelableExtra(GeneralUtils.INTENTS_MOVIE);
            initializeMovieDetails(movie);

        }
    }

    private void findComponents() {
        this.mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        this.mOverview = (TextView) findViewById(R.id.tv_overview);
        this.mRating = (TextView) findViewById(R.id.tv_rating);
        this.mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        this.posterDetail = (ImageView) findViewById(R.id.iv_poster_detail);
    }

    private void initializeMovieDetails(Movie movie) {
        String originalTitle = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String average = movie.getVoteAverage();
        String averageComplete = average + GeneralUtils.AVERAGE_TOTAL;
        String releaseDate = movie.getReleaseDate();

        this.mOriginalTitle.setText(originalTitle);
        this.mOverview.setText(overview);
        this.mRating.setText(averageComplete);
        this.mReleaseDate.setText(releaseDate);

        Picasso.with(this)
                .load(NetworkUtils.getMoviePosterUrl(GeneralUtils.POSTER_WIDTH, movie.getPosterPath()))
                .into(this.posterDetail);
    }
}
