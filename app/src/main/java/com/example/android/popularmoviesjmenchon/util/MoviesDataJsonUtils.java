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

    public static List<Movie> getMoviesFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        List<Movie> movies = new ArrayList<Movie>();
        JSONObject forecastJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = forecastJson.getJSONArray(RESULTS);
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String posterPath = movie.getString(GeneralUtils.POSTER_PATH);
            String originalTitle = movie.getString(GeneralUtils.ORIGINAL_TITLE);
            String overview = movie.getString(GeneralUtils.OVERVIEW);
            String voteAverage = movie.getString(GeneralUtils.VOTE_AVERAGE);
            String releaseDate = movie.getString(GeneralUtils.RELEASE_DATE);
            // Creating movie object
            Movie movieData = new Movie(posterPath, originalTitle, overview, voteAverage, releaseDate);
            movies.add(movieData);
        }
        return movies;
    }

}