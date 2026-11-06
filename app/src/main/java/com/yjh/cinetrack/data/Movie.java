package com.yjh.cinetrack.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class Movie {
    public int id;
    public String mediaType = "movie";
    public String title;
    public String originalTitle;
    public String overview;
    public String releaseDate;
    public double score;
    public int voteCount;
    public int posterRes;
    public int backdropRes;
    public String posterPath;
    public String backdropPath;
    public String genre;
    public int runtime;
    public boolean demo;

    public Movie(int id, String title, String originalTitle, String overview, String releaseDate,
                 double score, int posterRes, int backdropRes, String genre, int runtime) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.score = score;
        this.posterRes = posterRes;
        this.backdropRes = backdropRes;
        this.genre = genre;
        this.runtime = runtime;
        this.demo = true;
    }

    public static Movie fromJson(JSONObject json, String fallbackType) {
        if (json == null) return null;
        String type = opt(json, "media_type");
        if (type.isEmpty()) type = fallbackType;
        if (!"movie".equals(type) && !"tv".equals(type)) return null;

        String title = "tv".equals(type) ? opt(json, "name") : opt(json, "title");
        if (title.isEmpty()) return null;

        Movie movie = new Movie(0, title,
                "tv".equals(type) ? opt(json, "original_name") : opt(json, "original_title"),
                opt(json, "overview"),
                "tv".equals(type) ? opt(json, "first_air_date") : opt(json, "release_date"),
                json.optDouble("vote_average", 0),
                0, 0, "", 0);
        movie.demo = false;
        movie.id = json.optInt("id");
        movie.mediaType = type;
        movie.voteCount = json.optInt("vote_count", 0);
        movie.posterPath = opt(json, "poster_path");
        movie.backdropPath = opt(json, "backdrop_path");
        int genreId = 0;
        JSONArray genres = json.optJSONArray("genre_ids");
        if (genres != null && genres.length() > 0) genreId = genres.optInt(0);
        movie.genre = genreName(genreId);
        return movie;
    }

    public String year() {
        return releaseDate != null && releaseDate.length() >= 4 ? releaseDate.substring(0, 4) : "待定";
    }

    public String typeLabel() {
        return "tv".equals(mediaType) ? "剧集" : "电影";
    }

    public String metaLine() {
        StringBuilder builder = new StringBuilder(year()).append(" · ").append(typeLabel());
        if (runtime > 0) builder.append(" · ").append(runtime).append(" 分钟");
        return builder.toString();
    }

    public String shortDate() {
        if (releaseDate == null || releaseDate.length() < 10) return "待定";
        return Integer.parseInt(releaseDate.substring(5, 7)) + "月"
                + Integer.parseInt(releaseDate.substring(8, 10)) + "日";
    }

    public String posterUrl() {
        return imageUrl(posterPath, "w342");
    }

    public String backdropUrl() {
        String path = backdropPath == null || backdropPath.isEmpty() ? posterPath : backdropPath;
        return imageUrl(path, "w780");
    }

    private String imageUrl(String path, String size) {
        if (path == null || path.isEmpty() || "null".equals(path)) return "";
        return "https://image.tmdb.org/t/p/" + size + (path.startsWith("/") ? path : "/" + path);
    }

    private static String opt(JSONObject json, String key) {
        if (json == null || json.isNull(key)) return "";
        String value = json.optString(key, "");
        return "null".equals(value) ? "" : value;
    }

    private static String genreName(int id) {
        switch (id) {
            case 28: return "动作";
            case 12: return "冒险";
            case 16: return "动画";
            case 35: return "喜剧";
            case 80: return "犯罪";
            case 99: return "纪录";
            case 18: return "剧情";
            case 14: return "奇幻";
            case 36: return "历史";
            case 27: return "恐怖";
            case 9648: return "悬疑";
            case 10749: return "爱情";
            case 878: return "科幻";
            case 53: return "惊悚";
            case 10752: return "战争";
            case 37: return "西部";
            default: return "";
        }
    }
}
