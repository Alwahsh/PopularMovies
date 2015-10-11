package com.example.themonster.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by themonster on 9/30/15.
 */
@SimpleSQLTable(table = "movies", provider = "MoviesProvider")
public class Movie implements Parcelable {

    private static final long serialVersionUID = 1L;

    @SimpleSQLColumn(value = "col_id", primary = true)
    public String id;

    @SimpleSQLColumn("col_poster")
    public String poster;

    @SimpleSQLColumn("col_date")
    public String date;

    @SimpleSQLColumn("col_vote_average")
    public String vote_average;

    @SimpleSQLColumn("col_plot_synopsis")
    public String plot_synopsis;

    @SimpleSQLColumn("col_title")
    public String title;

    public Movie() {}

    public Movie(String a, String b) {
        id = a;
        poster = b;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        poster = in.readString();
        date = in.readString();
        vote_average = in.readString();
        plot_synopsis = in.readString();
        title = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(poster);
        dest.writeString(date);
        dest.writeString(vote_average);
        dest.writeString(plot_synopsis);
        dest.writeString(title);
    }
}
