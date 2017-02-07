package com.example.android.popularmoviesjmenchon.util;

import android.net.Uri;

import com.example.android.popularmoviesjmenchon.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class which allows you to search information about movies on the internet
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String ENDPOINT_POPULAR = "/movie/popular";
    private static final String ENDPOINT_TOP_RATED = "/movie/top_rated";
    private static final String BASE_URL_IMAGES = "http://image.tmdb.org/t/p/";
    private static final String SIZE_IMAGE = "w";
    private static final String API_PARAM = "api_key";


    public static URL buildUrl(String location) {
        Uri builtUri = Uri.parse(location).buildUpon()
                .appendQueryParameter(API_PARAM, BuildConfig.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getMoviePosterUrl(Integer size, String idPoster) {
        String finalUrl = "";
        if (size == null || size <= 0) {
            finalUrl = BASE_URL_IMAGES + SIZE_IMAGE + 600 + "//" + idPoster;
        } else {
            finalUrl = BASE_URL_IMAGES + SIZE_IMAGE + size + "//" + idPoster;
        }
        return finalUrl;
    }

    public static String getPopularMoviesUrl() {
        return BASE_URL + ENDPOINT_POPULAR;
    }

    public static String getTopRatedMoviesUrl() {
        return BASE_URL + ENDPOINT_TOP_RATED;
    }

    /* Check if the url is correct private static boolean isCorrect(String url) {
        Pattern pattern = Pattern.compile(BASE_URL_IMAGES + SIZE_IMAGE + "\\d+" + "//" + ".+");
        Matcher matcher = pattern.matcher(url);
        boolean isCorrect = matcher.matches();
        return isCorrect;
    }*/


}
