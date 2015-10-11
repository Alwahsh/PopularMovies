package com.example.themonster.popularmovies;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by themonster on 10/6/15.
 */
public class Trailer{

    public String name;
    public String key;
    private static final String YOUTUBEPREFIX = "https://www.youtube.com/watch?v=";

    public Trailer(String n, String k) {
        name = n;
        key = k;
    }

    public String getYoutubeLink() {
        return YOUTUBEPREFIX+key;
    }

}

