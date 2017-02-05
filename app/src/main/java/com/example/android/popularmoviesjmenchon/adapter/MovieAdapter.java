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
package com.example.android.popularmoviesjmenchon.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

// DONE: Creating and Using a Custom ArrayAdapter
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final ListItemClickListener mOnClickListener;


    public MovieAdapter(Activity context, List<Movie> movies, ListItemClickListener mOnClickListener) {
        super(context, 0, movies);
        this.mOnClickListener = mOnClickListener;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_item, parent, false);
        }
        ImageView view = (ImageView) convertView.findViewById(R.id.iv_poster);
        view.setOnClickListener(new ListItemListener(movie, this.mOnClickListener));
        String urlMoviePosterPath = NetworkUtils.getMoviePosterUrl(null, movie.getPosterPath());
        if (urlMoviePosterPath != null && !urlMoviePosterPath.equals("")) {
            Picasso.with(getContext())
                    .load(urlMoviePosterPath)
                    .into(view);
        }
        return convertView;
    }

}
