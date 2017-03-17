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


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<MovieAdapterViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private final ListItemClickListener mOnClickListener;


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(Context mContext, ListItemClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view, mOnClickListener);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
        int posterPathIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        int originalTitleIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overviewIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteIndex = mCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        mCursor.moveToPosition(position); // get to the right location in the cursor
        // Determine the values of the wanted data
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


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}