package com.yjh.cinetrack.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.yjh.cinetrack.data.Movie;
import com.yjh.cinetrack.data.SettingsStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Dependency-free TMDb v3 client using HttpURLConnection. */
public final class TMDbClient {
    private static final String BASE = "https://api.themoviedb.org/3";
    private static final ExecutorService IO = Executors.newFixedThreadPool(4);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static volatile TMDbClient instance;
    private final Context context;

    private TMDbClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public static TMDbClient get(Context context) {
        if (instance == null) {
            synchronized (TMDbClient.class) {
                if (instance == null) instance = new TMDbClient(context);
            }
        }
        return instance;
    }

    public boolean hasKey() {
        return !SettingsStore.apiKey(context).isEmpty();
    }

    public void trending(ApiCallback<List<Movie>> callback) {
        requestList("/trending/movie/week", new LinkedHashMap<>(), "movie", callback);
    }

    public void upcoming(ApiCallback<List<Movie>> callback) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("region", "CN");
        requestList("/movie/upcoming", params, "movie", callback);
    }

    public void validateKey(String key, ApiCallback<Boolean> callback) {
        if (TextUtils.isEmpty(key)) {
            MAIN.post(() -> callback.onError("请先填写 API Key"));
            return;
        }
        IO.execute(() -> {
            try {
                getJson("/configuration", new LinkedHashMap<>(), key.trim());
                MAIN.post(() -> callback.onSuccess(true));
            } catch (Exception error) {
                String message = messageFor(error);
                MAIN.post(() -> callback.onError(message));
            }
        });
    }

    private void requestList(String path, LinkedHashMap<String, String> params,
                             String fallbackType, ApiCallback<List<Movie>> callback) {
        if (!hasKey()) {
            MAIN.post(() -> callback.onError("尚未配置 TMDb API Key"));
            return;
        }
        IO.execute(() -> {
            try {
                JSONObject root = getJson(path, params, null);
                JSONArray results = root.optJSONArray("results");
                List<Movie> movies = new ArrayList<>();
                if (results != null) {
                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = Movie.fromJson(results.optJSONObject(i), fallbackType);
                        if (movie != null && movie.id > 0) movies.add(movie);
                    }
                }
                MAIN.post(() -> callback.onSuccess(movies));
            } catch (Exception error) {
                String message = messageFor(error);
                MAIN.post(() -> callback.onError(message));
            }
        });
    }

    private JSONObject getJson(String path, Map<String, String> params, String keyOverride) throws Exception {
        String key = keyOverride != null ? keyOverride : SettingsStore.apiKey(context);
        if (key == null || key.isEmpty()) throw new ApiException("尚未配置 TMDb API Key");

        StringBuilder query = new StringBuilder();
        append(query, "api_key", key);
        append(query, "language", "zh-CN");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            append(query, entry.getKey(), entry.getValue());
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE + path + "?" + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15_000);
            connection.setReadTimeout(20_000);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "CineTrack/0.2 (Android)");

            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300
                    ? connection.getInputStream() : connection.getErrorStream();
            String body = readAll(stream);
            if (status == 401) throw new ApiException("TMDb API Key 无效或已失效");
            if (status == 404) throw new ApiException("TMDb 找不到对应内容");
            if (status == 429) throw new ApiException("请求太频繁，请稍后再试");
            if (status < 200 || status >= 300) throw new ApiException("TMDb 请求失败（" + status + "）");
            if (TextUtils.isEmpty(body)) throw new ApiException("TMDb 返回了空数据");
            return new JSONObject(body);
        } catch (SocketTimeoutException error) {
            throw new ApiException("连接 TMDb 超时，请检查网络");
        } catch (UnknownHostException error) {
            throw new ApiException("无法连接 TMDb，请检查网络");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String readAll(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }
        return body.toString();
    }

    private void append(StringBuilder builder, String key, String value) {
        if (builder.length() > 0) builder.append('&');
        try {
            builder.append(URLEncoder.encode(key, "UTF-8"))
                    .append('=')
                    .append(URLEncoder.encode(value == null ? "" : value, "UTF-8"));
        } catch (Exception ignored) {
            builder.append(key).append('=').append(value);
        }
    }

    private String messageFor(Exception error) {
        if (error instanceof ApiException && !TextUtils.isEmpty(error.getMessage())) {
            return error.getMessage();
        }
        if (error instanceof SocketTimeoutException) return "连接 TMDb 超时，请检查网络";
        if (error instanceof UnknownHostException) return "无法连接 TMDb，请检查网络";
        return "网络开小差了，请稍后重试";
    }

    private static class ApiException extends Exception {
        ApiException(String message) {
            super(message);
        }
    }
}
