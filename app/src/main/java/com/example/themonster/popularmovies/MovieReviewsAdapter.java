package com.example.themonster.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by themonster on 10/6/15.
 */
public class MovieReviewsAdapter extends ArrayAdapter<Review> {

    private LayoutInflater mInflater;

    public MovieReviewsAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        if (convertView == null)
            view = (LinearLayout) mInflater.inflate(R.layout.review, parent, false);
        else
            view = (LinearLayout) convertView;
        ((TextView)view.findViewById(R.id.reviewAuthor)).setText(getItem(position).author);
        ((TextView)view.findViewById(R.id.reviewContent)).setText(getItem(position).content);
        return view;
    }
}
