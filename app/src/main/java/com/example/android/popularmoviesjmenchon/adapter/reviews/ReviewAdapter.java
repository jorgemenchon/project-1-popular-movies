package com.example.android.popularmoviesjmenchon.adapter.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.adapter.movies.MovieAdapter;
import com.example.android.popularmoviesjmenchon.model.Review;

import java.util.ArrayList;

/**
 * Created by jorgemenchon on 25/02/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapterViewHolder> {
    //ArrayAdapter<Movie> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Review> listReviews;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = getItem(position);
        if (review != null) {
            holder.setReview(review);
            TextView trailerName = holder.getReviewAutor();
            trailerName.setText(review.getAuthor());
            TextView reviewContent = holder.getReviewContent();
            reviewContent.setText(review.getContent());
        }
    }

    private Review getItem(int position) {
        if (listReviews != null && listReviews.size() > position) {
            return listReviews.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (null == listReviews) return 0;
        return listReviews.size();
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param listReviews The new listReviews data to be displayed.
     */
    public void setListReviews(ArrayList<Review> listReviews) {
        this.listReviews = listReviews;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getListReviews() {
        return listReviews;
    }


}