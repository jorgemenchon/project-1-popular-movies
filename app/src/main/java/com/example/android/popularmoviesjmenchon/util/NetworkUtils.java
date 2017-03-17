package com.example.android.popularmoviesjmenchon.util;

import android.net.Uri;

import com.example.android.popularmoviesjmenchon.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class which allows you to search information about movies on the internet
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String ENDPOINT_POPULAR = "/popular";
    private static final String ENDPOINT_VIDEOS = "/videos";
    private static final String ENDPOINT_REVIEWS = "/reviews";
    private static final String ENDPOINT_TOP_RATED = "/top_rated";
    private static final String BASE_URL_IMAGES = "http://image.tmdb.org/t/p/";
    private static final String SIZE_IMAGE = "w";
    private static final String API_PARAM = "api_key";

    private static final OkHttpClient client = new OkHttpClient();


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

    /*
    Method using library Okhttp. More info:http://square.github.io/okhttp/
    We could use Retrofit or volley: http://vickychijwani.me/retrofit-vs-volley/
    http://www.vogella.com/tutorials/Retrofit/article.html
    http://www.androidhive.info/2014/05/android-working-with-volley-library-1/
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return getResponse(request);
    }

    public static String getResponseFromHttpUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return getResponse(request);
    }

    private static String getResponse(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        return response.body().string();
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

    // TODO change to get**Location
    public static String getPopularMoviesUrl() {
        return BASE_URL + ENDPOINT_POPULAR;
    }

    public static String getTopRatedMoviesUrl() {
        return BASE_URL + ENDPOINT_TOP_RATED;
    }

    public static String getTrailersUrl(String id) {
        return BASE_URL + "/" + id + ENDPOINT_VIDEOS;
    }
    public static String getReviewsUrl(String id) {
        return BASE_URL + "/" + id + ENDPOINT_REVIEWS;
    }
    /* Check if the url is correct private static boolean isCorrect(String url) {
        Pattern pattern = Pattern.compile(BASE_URL_IMAGES + SIZE_IMAGE + "\\d+" + "//" + ".+");
        Matcher matcher = pattern.matcher(url);
        boolean isCorrect = matcher.matches();
        return isCorrect;
    }*/


}
