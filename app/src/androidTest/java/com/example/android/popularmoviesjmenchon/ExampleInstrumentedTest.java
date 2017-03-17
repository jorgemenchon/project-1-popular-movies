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

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    private static final Uri TEST_TASKS = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
    // Content URI for a single task with id = 1
    private static final Uri TEST_TASK_WITH_ID = TEST_TASKS.buildUpon().appendPath("1").build();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.android.popularmoviesjmenchon", appContext.getPackageName());
    }


    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    // @Test
    public void testUriMatcher() {

        /* Create a URI matcher that the TaskContentProvider uses */
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected TASKS int */
        String tasksUriDoesNotMatch = "Error: The TASKS URI was matched incorrectly.";
        int actualTasksMatchCode = testMatcher.match(TEST_TASKS);
        int expectedTasksMatchCode = MovieContentProvider.MOVIES;
        assertEquals(tasksUriDoesNotMatch,
                actualTasksMatchCode,
                expectedTasksMatchCode);

       /* Test that the code returned from our matcher matches the expected TASK_WITH_ID */
        String taskWithIdDoesNotMatch =
                "Error: The TASK_WITH_ID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(TEST_TASK_WITH_ID);
        int expectedTaskWithIdCode = MovieContentProvider.MOVIES_WITH_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }

    /**
     * //     * Tests inserting a single row of data via a ContentResolver
     * //
     */
    // @Test
    public void testInsert() {

        /* Create values to insert */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ID, "12345678");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");


        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);


        Uri uri = contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, testTaskValues);


        Uri expectedUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, 12345678);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }

    //  @Test
    public void testQuery() {

        /* Get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

       /* Create values to insert */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ID, "123456789");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");

        /* Insert ContentValues into database and get a row ID back */
        long taskRowId = database.insert(
               /* Table to insert values into */
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testTaskValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, taskRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /* Perform the ContentProvider query */
        Cursor taskCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
               /* Sort order to return in Cursor */
                null);


        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, taskCursor != null);

       /* We are done with the cursor, close it now. */
        taskCursor.close();
    }


   // @Test
    public void testDelete() {
        /* Access writable database */
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* Create a new row of task data */
        ContentValues testTaskValues = new ContentValues();
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ID, "12345678");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "HARRY POTTER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "BEST MOVIE EVER");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/jdbfhberjjefhe");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/1991");
        testTaskValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, "5/5");

        /* Insert ContentValues into database and get a row ID back */
        long taskRowId = database.insert(
                /* Table to insert values into */
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testTaskValues);

        /* Always close the database when you're through with it */
        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, taskRowId != -1);


        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);



        /* The delete method deletes the previously inserted row with id = 1 */
        Uri uriToDelete = MoviesContract.MovieEntry.CONTENT_URI_MOVIES.buildUpon().appendPath("12345678").build();
        int tasksDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, tasksDeleted != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }
//@Test
    public void testDeleteAll() {
        /* Access writable database */
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MoviesContract.MovieEntry.CONTENT_URI_MOVIES,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);



        /* The delete method deletes the previously inserted row with id = 1 */
        Uri uriToDelete = MoviesContract.MovieEntry.CONTENT_URI_MOVIES;
        int tasksDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, tasksDeleted != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }
}
