package com.yjh.cinetrack.data;

public class DemoMovie {
    public int id;
    public String title;
    public String originalTitle;
    public String overview;
    public String releaseDate;
    public double score;
    public int posterRes;
    public int backdropRes;
    public String genre;
    public int runtime;

    public DemoMovie(int id, String title, String originalTitle, String overview, String releaseDate,
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
    }

    public String year() {
        return releaseDate != null && releaseDate.length() >= 4 ? releaseDate.substring(0, 4) : "待定";
    }

    public String metaLine() {
        StringBuilder builder = new StringBuilder(year()).append(" · 电影");
        if (runtime > 0) builder.append(" · ").append(runtime).append(" 分钟");
        return builder.toString();
    }

    public String shortDate() {
        if (releaseDate == null || releaseDate.length() < 10) return "待定";
        return Integer.parseInt(releaseDate.substring(5, 7)) + "月"
                + Integer.parseInt(releaseDate.substring(8, 10)) + "日";
    }
}
