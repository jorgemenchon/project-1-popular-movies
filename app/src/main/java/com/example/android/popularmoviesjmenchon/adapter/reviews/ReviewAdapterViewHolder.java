package com.example.android.popularmoviesjmenchon.adapter.reviews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.adapter.trailers.ListItemTrailerClickListener;
import com.example.android.popularmoviesjmenchon.model.Review;
import com.example.android.popularmoviesjmenchon.model.Trailer;


public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

    // @BindView(R.id.iv_poster)
    TextView reviewAutor;
    TextView reviewContent;
    Review review;

    public ReviewAdapterViewHolder(View view) {
        super(view);
        reviewAutor = (TextView) view.findViewById(R.id.tv_review_author);
        reviewContent = (TextView) view.findViewById(R.id.tv_review_content);
    }

    public void setReview(Review review) {
        this.review = review;
    }


    public TextView getReviewAutor() {
        return reviewAutor;
    }

    public TextView getReviewContent() {
        return reviewContent;
    }
}
