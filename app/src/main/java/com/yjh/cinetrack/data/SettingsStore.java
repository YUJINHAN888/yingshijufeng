package com.yjh.cinetrack.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.yjh.cinetrack.BuildConfig;

public final class SettingsStore {
    private static final String PREFS = "cinetrack_settings";
    private static final String KEY_API = "tmdb_api_key";

    private SettingsStore() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String apiKey(Context context) {
        String saved = prefs(context).getString(KEY_API, "");
        if (saved != null && !saved.trim().isEmpty()) return saved.trim();
        return BuildConfig.TMDB_API_KEY == null ? "" : BuildConfig.TMDB_API_KEY.trim();
    }

    public static boolean hasKey(Context context) {
        return !apiKey(context).isEmpty();
    }

    public static void saveKey(Context context, String key) {
        prefs(context).edit().putString(KEY_API, key == null ? "" : key.trim()).apply();
    }

    public static void clearKey(Context context) {
        prefs(context).edit().remove(KEY_API).apply();
    }
}
