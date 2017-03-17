package com.example.android.popularmoviesjmenchon.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jorgemenchon on 25/02/2017.
 */

public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesjmenchon";
    public static final String SCHEME = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + CONTENT_AUTHORITY);
    public static final String PATH_FAVOURITE_MOVIES = "movies";
    public static final String PATH_MOVIE_WITH_ID = PATH_FAVOURITE_MOVIES + "/#";


    /* Inner class that defines the table contents of the weather table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI_MOVIES = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE_MOVIES)
                .build();
        public static final Uri CONTENT_URI_MOVIE_WITH_ID = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_WITH_ID)
                .build();

        /* Used internally as the name of our movie table. */
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_ID = "id";


        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards

        public static String getSqlSelectForTodayOnwards() {
        long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
        return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        } */

    }
}
