package com.yjh.cinetrack.util;

import android.content.Context;
import android.widget.Toast;

public final class Ui {
    private Ui() {}

    public static void toast(Context context, String message) {
        if (context != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static int color(Context context, int colorRes) {
        return androidx.core.content.ContextCompat.getColor(context, colorRes);
    }
}
