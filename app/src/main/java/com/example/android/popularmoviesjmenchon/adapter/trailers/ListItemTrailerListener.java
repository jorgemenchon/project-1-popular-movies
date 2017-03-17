package com.example.android.popularmoviesjmenchon.adapter.trailers;

import android.view.View;

import com.example.android.popularmoviesjmenchon.model.Trailer;

/**
 * Created by jorgemenchon on 25/02/2017.
 */

public class ListItemTrailerListener implements View.OnClickListener {
    private Trailer trailer;
    private ListItemTrailerClickListener mOnClickListener;

    public ListItemTrailerListener(Trailer trailer, ListItemTrailerClickListener mOnClickListener) {
        this.trailer = trailer;
        this.mOnClickListener = mOnClickListener;
    }

    public void onClick(View v) {
        this.mOnClickListener.onListItemClick(trailer);
    }

}
