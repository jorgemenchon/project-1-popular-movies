/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.popularmoviesjmenchon.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;

// TODO (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
public class MovieContentProvider extends ContentProvider {


    private MovieDbHelper mMovieDbHelper;
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMather = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVOURITE_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE_WITH_ID, MOVIES_WITH_ID);
        return uriMatcher;
    }

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return false;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMather.match(uri);
        Uri returnUri = null;

        switch (match) {
            case MOVIES:
                long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI_MOVIES, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMather.match(uri)) {

            case MOVIES_WITH_ID: {

                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};

                cursor = mMovieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES: {
                cursor = mMovieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMather.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case MOVIES_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, "id=?", new String[]{id});
                break;
            case MOVIES:
                // Delete all
                tasksDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return number of items deleted
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
