package com.vriza.donasikita.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.vriza.donasikita.models.User;

public class SessionManager {

    private static final String PREF_NAME = "DonasiKitaSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_API_TOKEN = "apiToken";
    private static final String KEY_USER_DETAILS = "userDetails";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public void createLoginSession(String apiToken, User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_API_TOKEN, apiToken);

        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DETAILS, userJson);

        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public String getApiToken() {
        return pref.getString(KEY_API_TOKEN, null);
    }


    public User getUserDetails() {
        String userJson = pref.getString(KEY_USER_DETAILS, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }


    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
