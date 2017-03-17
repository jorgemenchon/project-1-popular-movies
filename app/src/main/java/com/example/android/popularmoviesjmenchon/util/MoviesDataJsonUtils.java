/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmoviesjmenchon.util;

import android.content.Context;

import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.model.Review;
import com.example.android.popularmoviesjmenchon.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle Movie JSON data.
 */
public final class MoviesDataJsonUtils {


    private static final String RESULTS = "results";

    public static List<Movie> getMoviesFromJson(String moviesJsonStr)
            throws JSONException {

        List<Movie> movies = new ArrayList<Movie>();
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String posterPath = movie.getString(GeneralUtils.POSTER_PATH);
            String originalTitle = movie.getString(GeneralUtils.ORIGINAL_TITLE);
            String overview = movie.getString(GeneralUtils.OVERVIEW);
            String voteAverage = movie.getString(GeneralUtils.VOTE_AVERAGE);
            String releaseDate = movie.getString(GeneralUtils.RELEASE_DATE);
            String id = movie.getString(GeneralUtils.ID);
            // Creating movie object
            Movie movieData = new Movie(posterPath, originalTitle, overview, voteAverage, releaseDate, id);
            movies.add(movieData);
        }
        return movies;
    }

    public static List<Trailer> getTrailersFromJson(String moviesJsonStr)
            throws JSONException {

        List<Trailer> trailers = new ArrayList<Trailer>();
        JSONObject videosJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = videosJson.getJSONArray(RESULTS);
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject trailer = moviesArray.getJSONObject(i);
            String key = trailer.getString(GeneralUtils.KEY);
            String name = trailer.getString(GeneralUtils.NAME);

            // Creating movie object
            Trailer trailerData = new Trailer(key, name);
            trailers.add(trailerData);
        }
        return trailers;
    }

    public static List<Review> getReviewsFromJson(String reviewsJsonStr)
            throws JSONException {

        List<Review> reviews = new ArrayList<Review>();
        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);
        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject review = reviewsArray.getJSONObject(i);
            String author = review.getString(GeneralUtils.AUTHOR);
            String id = review.getString(GeneralUtils.ID);
            String content = review.getString(GeneralUtils.CONTENT);
            String url = review.getString(GeneralUtils.URL);
            // Creating review object
            Review reviewData = new Review(id, author, content, url);
            reviews.add(reviewData);
        }
        return reviews;
    }
}