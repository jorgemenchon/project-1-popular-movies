package com.example.android.popularmoviesjmenchon.aynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesjmenchon.ListMovies;
import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.util.MoviesDataJsonUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;

import java.net.URL;
import java.util.List;


public class FetchMoviesInfo extends AsyncTask<String, Void, List<Movie>> {

    private static final String TAG = FetchMoviesInfo.class.getSimpleName();

    private Context context;
    private AsyncTaskCompleteListener listener;

    public FetchMoviesInfo(Context context, AsyncTaskCompleteListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String location = params[0];
        URL buildRequestUrl = NetworkUtils.buildUrl(location);
        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(buildRequestUrl);

            List<Movie> moviesData = MoviesDataJsonUtils
                    .getMoviesFromJson(this.context, jsonMoviesResponse);
            return moviesData;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error trying to access to the info.");
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> moviesData) {
        this.listener.onTaskComplete(moviesData);
    }
}
