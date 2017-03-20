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

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.adapter.movies.ListItemClickListener;
import com.example.android.popularmoviesjmenchon.adapter.movies.MovieAdapterViewHolder;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.model.MoviesContract;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;
import com.squareup.picasso.Picasso;


public class CustomCursorAdapter extends RecyclerView.Adapter<MovieAdapterViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private final ListItemClickListener mOnClickListener;


    public CustomCursorAdapter(Context mContext, ListItemClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view, mOnClickListener);
    }


    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
        int posterPathIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        int originalTitleIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overviewIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String posterPath = mCursor.getString(posterPathIndex);
        String originalTitle = mCursor.getString(originalTitleIndex);
        String overview = mCursor.getString(overviewIndex);
        String releaseDate = mCursor.getString(releaseDateIndex);
        String voteAverage = mCursor.getString(voteIndex);

        Movie movie = new Movie(posterPath, originalTitle, overview, voteAverage, releaseDate, String.valueOf(id));
        holder.setMovie(movie);
        ImageView view = holder.getPosterView();
        String urlMoviePosterPath = NetworkUtils.getMoviePosterUrl(null, movie.getPosterPath());
        if (!TextUtils.isEmpty(urlMoviePosterPath)) {
            Picasso.with(mContext)
                    .load(urlMoviePosterPath)
                    .into(view);
        }
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public Cursor getCursor() {
        return mCursor;
    }

}