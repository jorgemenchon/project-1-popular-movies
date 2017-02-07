package com.example.android.popularmoviesjmenchon.aynctasks;

public interface AsyncTaskCompleteListener<T>
{
    void onTaskComplete(T result);
    void onPreExecute();

}