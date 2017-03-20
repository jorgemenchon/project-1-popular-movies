package com.example.android.popularmoviesjmenchon.adapter.trailers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.model.Trailer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    TextView nameTrailer;
    ImageView iconPlay;
    Trailer trailer;
    ListItemTrailerClickListener mOnClickListener;

    public TrailerAdapterViewHolder(View view, ListItemTrailerClickListener mOnClickListener) {
        super(view);
        this.mOnClickListener = mOnClickListener;
        nameTrailer = (TextView) view.findViewById(R.id.tv_trailer_name);
        iconPlay = (ImageView) view.findViewById(R.id.iv_icon_play);
        nameTrailer.setOnClickListener(this);
        iconPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onListItemClick(trailer);
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public TextView getNameTrailer() {
        return nameTrailer;
    }
}
