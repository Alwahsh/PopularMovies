package com.example.themonster.popularmovies;

import java.io.Serializable;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by themonster on 9/30/15.
 */
@SimpleSQLTable(table = "movies", provider = "MoviesProvider")
public class Movie implements Serializable {

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

}
