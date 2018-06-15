package com.alexshr.baking;

public interface AppConfig {

    String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    int RETRY_COUNT = 3;
    long RETRY_INITIAL_DELAY = 100;

    int MAX_CONNECTION_TIMEOUT = 5000;
    int MAX_READ_TIMEOUT = 5000;
    int MAX_WRITE_TIMEOUT = 5000;

    String ETAG_HEADER = "ETag";
    String IF_NONE_MATCH_HEADER = "If-None-Match";

    String DATABASE_NAME = "recipes";
}
