package com.example.android.popularmoviesjmenchon.adapter;

import android.view.View;

import com.example.android.popularmoviesjmenchon.model.Movie;

/**
 * Listener which call onListItemClick from class which implements interface: ListItemClickListener. In this case: ListMovies
 */

public class ListItemListener implements View.OnClickListener {
    private Movie movie;
    private ListItemClickListener mOnClickListener;

    public ListItemListener(Movie movie, ListItemClickListener mOnClickListener) {
        this.movie = movie;
        this.mOnClickListener = mOnClickListener;
    }

    public void onClick(View v) {
        this.mOnClickListener.onListItemClick(movie);
    }

}
