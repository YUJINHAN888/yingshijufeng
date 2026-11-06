package com.yjh.cinetrack.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Step 2 intentionally uses a lightweight, no-cache image loader.
 * The two-level poster cache is introduced in step 8.
 */
public final class NetworkImageLoader {
    private static final ExecutorService IO = Executors.newFixedThreadPool(4);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private NetworkImageLoader() {}

    public static void load(ImageView view, String url, int placeholder) {
        if (view == null) return;
        view.setTag(url);
        if (placeholder != 0) view.setImageResource(placeholder);
        if (url == null || url.isEmpty()) return;

        IO.execute(() -> {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(15_000);
                connection.setReadTimeout(20_000);
                connection.setRequestProperty("User-Agent", "CineTrack/0.2 (Android)");
                if (connection.getResponseCode() / 100 == 2) {
                    try (InputStream input = connection.getInputStream()) {
                        bitmap = BitmapFactory.decodeStream(input);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (connection != null) connection.disconnect();
            }
            final Bitmap result = bitmap;
            MAIN.post(() -> {
                if (result != null && url.equals(view.getTag())) view.setImageBitmap(result);
            });
        });
    }
}
