package com.stickynotes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private Context ctx;
    private SharedPreferences sharedPref;

    private static final String SHARED_PREF_FILENAME = "shared_preferences";
    private static final String USER_LOGGED_IN_TOKEN = "user_logged_in";

    public SharedPrefManager(Context ctx) {
        this.ctx = ctx;
        sharedPref = ctx.getSharedPreferences(
                SHARED_PREF_FILENAME, Context.MODE_PRIVATE
        );
    }

    public boolean isUserLoggedIn() {
        return sharedPref.getBoolean(USER_LOGGED_IN_TOKEN, false);
    }

}
