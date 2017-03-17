package com.example.android.popularmoviesjmenchon.aynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesjmenchon.model.Review;
import com.example.android.popularmoviesjmenchon.model.Trailer;
import com.example.android.popularmoviesjmenchon.util.MoviesDataJsonUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;

import java.net.URL;
import java.util.List;


public class FetchReviews extends AsyncTask<String, Void, List<Review>> {

    private static final String TAG = FetchReviews.class.getSimpleName();

    private Context context;
    private AsyncTaskCompleteListener listener;

    public FetchReviews(Context context, AsyncTaskCompleteListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.onPreExecute();
    }

    @Override
    protected List<Review> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String location = params[0];
        URL buildRequestUrl = NetworkUtils.buildUrl(location);
        try {
            String jsonReviewsResponse = NetworkUtils
                    .getResponseFromHttpUrl(buildRequestUrl);

            List<Review> reviewsData = MoviesDataJsonUtils
                    .getReviewsFromJson(jsonReviewsResponse);
            return reviewsData;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error trying to access to the info.");
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Review> trailersData) {
        this.listener.onTaskComplete(trailersData);
    }
}
