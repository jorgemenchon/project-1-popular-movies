package com.example.android.popularmoviesjmenchon;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmoviesjmenchon.model.MovieContentProvider;
import com.example.android.popularmoviesjmenchon.model.MovieDbHelper;
import com.example.android.popularmoviesjmenchon.model.MoviesContract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private static final Uri TEST_MOVIES = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
    private static final Uri TEST_MOVIES_WITH_ID = TEST_MOVIES.buildUpon().appendPath("1").build();

    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.android.popularmoviesjmenchon", appContext.getPackageName());
    }

    // @Test
    public void testUriMatcher() {

        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        String moviesUriDoesNotMatch = "Error: The MOVIES URI was matched incorrectly.";
        int actualMoviesMatchCode = testMatcher.match(TEST_MOVIES);
        int expectedMoviesMatchCode = MovieContentProvider.MOVIES;
        assertEquals(moviesUriDoesNotMatch,
                actualMoviesMatchCode,
                expectedMoviesMatchCode);

        String movieWithIdDoesNotMatch =
                "Error: The MOVIE_WITH_ID URI was matched incorrectly.";
        int actualMovieWithIdCode = testMatcher.match(TEST_MOVIES_WITH_ID);
        int expectedMovieWithIdCode = MovieContentProvider.MOVIES_WITH_ID;
        assertEquals(movieWithIdDoesNotMatch,
                actualMovieWithIdCode,
                expectedMovieWithIdCode);
    }


    // @Test
    public void testInsert() {

        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ID, "12345678");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");


        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.registerContentObserver(
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                true,
                movieObserver);

        Uri uri = contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, testMovieValues);
        Uri expectedUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, 12345678);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        movieObserver.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(movieObserver);
    }

    //  @Test
    public void testQuery() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ID, "123456789");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");

        long movieRowId = database.insert(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                testMovieValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, movieRowId != -1);

        database.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                null,
                null,
                null,
                null);

        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, movieCursor != null);

        movieCursor.close();
    }

    // @Test
    public void testDelete() {
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* Create a new row of movie data */
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ID, "12345678");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testMovieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");

        long movieRowId = database.insert(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                testMovieValues);

        database.close();
        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.registerContentObserver(
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                true,
                movieObserver);

        Uri uriToDelete = MoviesContract.MovieEntry.CONTENT_URI_MOVIES.buildUpon().appendPath("12345678").build();
        int moviesDeleted = contentResolver.delete(uriToDelete, null, null);
        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, moviesDeleted != 0);
        movieObserver.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(movieObserver);
    }

    //@Test
    public void testDeleteAll() {
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.registerContentObserver(
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                true,
                movieObserver);

        Uri uriToDelete = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
        int moviesDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, moviesDeleted != 0);

        movieObserver.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(movieObserver);
    }
}
