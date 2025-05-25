package com.example.questionnairebuilder.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.primitives.Primitives;
import com.google.gson.Gson;

public class SharedPreferencesManager {

    private static SharedPreferencesManager instance = null;
    private static final String DB_FILE = "DB_FILE";
    private SharedPreferences sharedPref;

    private SharedPreferencesManager(Context context) {
        this.sharedPref = context.getSharedPreferences(DB_FILE, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        synchronized (SharedPreferencesManager.class) {
            if (instance == null) {
                instance = new SharedPreferencesManager(context);
            }
        }
    }

    public static SharedPreferencesManager getInstance() {
        return instance;
    }

    public void putBoolean(String KEY, boolean value) {
        sharedPref.edit().putBoolean(KEY, value).apply();
    }

    public boolean getBoolean(String KEY, boolean defaultValue) {
        return sharedPref.getBoolean(KEY, defaultValue);
    }
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPref.getInt(key, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPref.getString(key, defaultValue);
    }

    public void putObject(String KEY, Object value) {
        sharedPref.edit().putString(KEY, new Gson().toJson(value)).apply();
    }

    public <T> T getObject(String KEY, Class<T> mModelClass) {
        Object object = null;
        try {
            object = new Gson().fromJson(sharedPref.getString(KEY, ""), mModelClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Primitives.wrap(mModelClass).cast(object);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key).apply();
    }

}
