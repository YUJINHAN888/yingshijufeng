package com.yjh.cinetrack.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yjh.cinetrack.R;
import com.yjh.cinetrack.data.Movie;
import com.yjh.cinetrack.image.NetworkImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieRailAdapter extends RecyclerView.Adapter<MovieRailAdapter.ViewHolder> {
    public interface OnMovieClick {
        void onMovieClick(Movie movie);
    }

    private final List<Movie> items = new ArrayList<>();
    private final boolean showReleaseDate;
    private final OnMovieClick listener;

    public MovieRailAdapter(boolean showReleaseDate, OnMovieClick listener) {
        this.showReleaseDate = showReleaseDate;
        this.listener = listener;
    }

    public void submit(List<Movie> movies) {
        items.clear();
        if (movies != null) items.addAll(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = items.get(position);
        holder.title.setText(movie.title);
        holder.meta.setText(movie.metaLine());
        if (movie.demo && movie.posterRes != 0) {
            holder.poster.setImageResource(movie.posterRes);
        } else {
            NetworkImageLoader.load(holder.poster, movie.posterUrl(),
                    R.drawable.bg_poster_placeholder);
        }

        if (movie.score > 0) {
            holder.score.setVisibility(View.VISIBLE);
            holder.score.setText(String.format(Locale.CHINA, "★ %.1f", movie.score));
        } else {
            holder.score.setVisibility(View.GONE);
        }

        if (showReleaseDate) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(movie.shortDate());
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMovieClick(movie);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView poster;
        final TextView title;
        final TextView meta;
        final TextView score;
        final TextView badge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            meta = itemView.findViewById(R.id.meta);
            score = itemView.findViewById(R.id.score);
            badge = itemView.findViewById(R.id.badge);
        }
    }
}
