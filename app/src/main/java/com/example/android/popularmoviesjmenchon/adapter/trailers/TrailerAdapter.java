package com.example.android.popularmoviesjmenchon.adapter.trailers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesjmenchon.R;
import com.example.android.popularmoviesjmenchon.adapter.movies.MovieAdapter;
import com.example.android.popularmoviesjmenchon.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgemenchon on 25/02/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapterViewHolder> {
    //ArrayAdapter<Movie> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final ListItemTrailerClickListener mOnClickListener;
    private List<Trailer> listTrailers;
    private Context context;

    public TrailerAdapter(ListItemTrailerClickListener mOnClickListener, Context context) {
        this.mOnClickListener = mOnClickListener;
        this.context = context;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        Trailer trailer = getItem(position);
        if (trailer != null) {
            holder.setTrailer(trailer);
            TextView trailerName = holder.getNameTrailer();
            trailerName.setText(trailer.getName());
        }
    }

    private Trailer getItem(int position) {
        if (listTrailers != null && listTrailers.size() > position) {
            return listTrailers.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (null == listTrailers) return 0;
        return listTrailers.size();
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param listTrailers The new listTrailers data to be displayed.
     */
    public void setListTrailers(List<Trailer> listTrailers) {
        this.listTrailers = listTrailers;
        notifyDataSetChanged();
    }

    public List<Trailer> getListTrailers() {
        return listTrailers;
    }


}