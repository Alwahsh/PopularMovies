package com.example.themonster.popularmovies;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

/**
 * Created by themonster on 9/29/15.
 */
public class MoviePostersAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;

    public MoviePostersAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view;
        if (convertView == null)
            view = (ImageView) mInflater.inflate(R.layout.movie_poster,parent, false);
        else
            view = (ImageView) convertView;
        Picasso.with(mContext).load(getItem(position)).placeholder(mContext.getResources().getDrawable(R.drawable.ic_default_poster)).error(mContext.getResources().getDrawable(R.drawable.ic_default_poster)).into(view);
        return view;
    }

}
