package com.example.themonster.popularmovies;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

@SimpleSQLConfig(
        name = "MoviesProvider",
        authority = "com.example.themonster.popularmovies.authority",
        database = "popular_movies.db",
        version = 1)
public class MoviesProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}