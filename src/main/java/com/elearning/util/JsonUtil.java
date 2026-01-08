package com.elearning.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {
    private static final Gson GSON = new GsonBuilder().create();

    private JsonUtil() {
    }

    public static Gson gson() {
        return GSON;
    }
}
