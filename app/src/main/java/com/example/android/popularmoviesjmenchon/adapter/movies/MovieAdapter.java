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
package com.example.android.popularmoviesjmenchon.adapter.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final ListItemClickListener mOnClickListener;
    private ArrayList<Movie> listMovies;
    private Context context;

    public MovieAdapter(ListItemClickListener mOnClickListener, Context context) {
        this.mOnClickListener = mOnClickListener;
        this.context = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = getItem(position);
        if (movie != null) {
            holder.setMovie(movie);
            ImageView view = holder.getPosterView();
            String urlMoviePosterPath = NetworkUtils.getMoviePosterUrl(null, movie.getPosterPath());
            if (!TextUtils.isEmpty(urlMoviePosterPath)) {
                Picasso.with(context)
                        .load(urlMoviePosterPath)
                        .into(view);
            }
        }
    }

    private Movie getItem(int position) {
        if (listMovies != null && listMovies.size() > position) {
            return listMovies.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (null == listMovies) return 0;
        return listMovies.size();
    }

    public void setListMovies(ArrayList<Movie> listMovies) {
        this.listMovies = listMovies;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getListMovies() {
        return listMovies;
    }


}
