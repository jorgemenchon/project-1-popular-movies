package com.example.android.popularmoviesjmenchon.adapter.movies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.model.Movie;


public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    ImageView posterView;
    Movie movie;
    ListItemClickListener mOnClickListener;

    public MovieAdapterViewHolder(View view, ListItemClickListener mOnClickListener) {
        super(view);
        this.mOnClickListener = mOnClickListener;
        posterView = (ImageView) view.findViewById(R.id.iv_poster);
        posterView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onListItemClick(movie);
    }

    public ImageView getPosterView() {
        return posterView;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
