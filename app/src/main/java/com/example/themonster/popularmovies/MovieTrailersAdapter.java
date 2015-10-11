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
public class MovieTrailersAdapter extends ArrayAdapter<String> {


    private LayoutInflater mInflater;

    public MovieTrailersAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        if (convertView == null)
            view = (LinearLayout) mInflater.inflate(R.layout.trailer,parent, false);
        else
            view = (LinearLayout) convertView;
        ((TextView)view.findViewById(R.id.trailer_title)).setText(getItem(position));
        return view;
    }
}
