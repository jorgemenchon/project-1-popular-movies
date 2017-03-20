package com.example.android.popularmoviesjmenchon.aynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesjmenchon.model.Movie;
import com.example.android.popularmoviesjmenchon.model.Trailer;
import com.example.android.popularmoviesjmenchon.util.GeneralUtils;
import com.example.android.popularmoviesjmenchon.util.MoviesDataJsonUtils;
import com.example.android.popularmoviesjmenchon.util.NetworkUtils;

import java.net.URL;
import java.util.List;


public class FetchTrailers extends AsyncTask<String, Void, List<Trailer>> {

    private static final String TAG = FetchTrailers.class.getSimpleName();

    private Context context;
    private AsyncTaskCompleteListener listener;

    public FetchTrailers(Context context, AsyncTaskCompleteListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.onPreExecute();
    }

    @Override
    protected List<Trailer> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String location = params[0];
        URL buildRequestUrl = NetworkUtils.buildUrl(location);
        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(buildRequestUrl);

            List<Trailer> trailersData = MoviesDataJsonUtils
                    .getTrailersFromJson(jsonMoviesResponse);

            if (trailersData.size()< GeneralUtils.MAX_TRAILERS){
                return trailersData;
            }
            List<Trailer> trailersSubList = trailersData.subList(0, GeneralUtils.MAX_TRAILERS);
            return trailersSubList;

        } catch (Exception e) {
            Log.e(TAG, "Error trying to access to the info.");
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Trailer> trailersData) {
        this.listener.onTaskComplete(trailersData);
    }
}
