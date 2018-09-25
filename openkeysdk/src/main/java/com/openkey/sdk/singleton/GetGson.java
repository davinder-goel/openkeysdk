package com.openkey.sdk.singleton;

import com.google.gson.Gson;

/**
 * @author OpenKey Inc.
 *         <p>
 *         Single Instance For GSON
 */
public class GetGson {
    private static Gson mInstance;

    public static Gson getInstance() {
        if (mInstance == null) {
            mInstance = new Gson();
        }
        return mInstance;
    }
}