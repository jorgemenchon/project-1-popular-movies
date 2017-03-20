package com.example.android.popularmoviesjmenchon.util;

import android.content.ContentValues;

import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.model.MoviesContract;

import java.util.List;

/**
 * Created by jorgemenchon on 17/03/2017.
 */

public class UtilFunctions {

    public static boolean isEmpty(List list) {
        return (list == null || list.size() == 0);
    }

    public static ContentValues getContentValuesFromMovie(Movie movie) {
        String id = movie.getId();
        String originalTitle = movie.getOriginalTitle();
        String overview = movie.getOverview();
        String posterPath = movie.getPosterPath();
        String releaseDate = movie.getReleaseDate();
        String voteAverage = movie.getVoteAverage();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, id);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, overview);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        return contentValues;
    }
}
