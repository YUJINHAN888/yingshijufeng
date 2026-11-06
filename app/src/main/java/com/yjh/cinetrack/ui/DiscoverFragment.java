package com.yjh.cinetrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.yjh.cinetrack.MainActivity;
import com.yjh.cinetrack.R;
import com.yjh.cinetrack.data.DemoCatalog;
import com.yjh.cinetrack.data.Movie;
import com.yjh.cinetrack.data.SettingsStore;
import com.yjh.cinetrack.image.NetworkImageLoader;
import com.yjh.cinetrack.net.ApiCallback;
import com.yjh.cinetrack.net.TMDbClient;

import java.util.List;
import java.util.Locale;

public class DiscoverFragment extends Fragment {
    private Movie hero;
    private MovieRailAdapter trendingAdapter;
    private MovieRailAdapter upcomingAdapter;
    private ImageView heroImage;
    private TextView heroTitle;
    private TextView heroMeta;
    private TextView heroOverview;
    private TextView trendingSource;
    private TextView upcomingSource;
    private TextView bannerTitle;
    private TextView bannerDesc;
    private TextView bannerAction;
    private ProgressBar syncProgress;
    private View apiBanner;

    private int loadToken;
    private boolean loading;
    private boolean networkError;
    private Boolean lastKeyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        heroImage = view.findViewById(R.id.hero_image);
        heroTitle = view.findViewById(R.id.hero_title);
        heroMeta = view.findViewById(R.id.hero_meta);
        heroOverview = view.findViewById(R.id.hero_overview);
        trendingSource = view.findViewById(R.id.trending_source);
        upcomingSource = view.findViewById(R.id.upcoming_source);
        bannerTitle = view.findViewById(R.id.api_banner_title);
        bannerDesc = view.findViewById(R.id.api_banner_desc);
        bannerAction = view.findViewById(R.id.api_banner_action);
        syncProgress = view.findViewById(R.id.sync_progress);
        apiBanner = view.findViewById(R.id.api_banner);

        RecyclerView trendingList = view.findViewById(R.id.trending_list);
        RecyclerView upcomingList = view.findViewById(R.id.upcoming_list);
        trendingAdapter = new MovieRailAdapter(false, movie ->
                roadmap("《" + movie.title + "》详情页将在第 4 步加入。"));
        upcomingAdapter = new MovieRailAdapter(true, movie ->
                roadmap("《" + movie.title + "》的上映提醒将在第 7 步加入。"));
        trendingList.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.HORIZONTAL, false));
        upcomingList.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.HORIZONTAL, false));
        trendingList.setAdapter(trendingAdapter);
        upcomingList.setAdapter(upcomingAdapter);

        applyDemo(false);
        view.findViewById(R.id.search_entry).setOnClickListener(v ->
                ((MainActivity) requireActivity()).selectTab("search"));
        view.findViewById(R.id.btn_api).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showApiKeyDialog();
            }
        });
        View.OnClickListener detailClick = v ->
                roadmap("《" + hero.title + "》详情页将在第 4 步加入。");
        view.findViewById(R.id.hero_card).setOnClickListener(detailClick);
        MaterialButton heroButton = view.findViewById(R.id.hero_button);
        heroButton.setOnClickListener(detailClick);
        apiBanner.setOnClickListener(v -> {
            if (loading) return;
            if (!SettingsStore.hasKey(requireContext())) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showApiKeyDialog();
                }
            } else if (networkError) {
                loadRemote();
            }
        });

        lastKeyState = SettingsStore.hasKey(requireContext());
        if (lastKeyState) loadRemote();
        else updateBanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null || lastKeyState == null) return;
        boolean current = SettingsStore.hasKey(requireContext());
        if (current != lastKeyState) {
            lastKeyState = current;
            if (current) loadRemote();
            else {
                applyDemo(false);
                updateBanner();
            }
        }
    }

    public void reload() {
        if (getContext() == null) return;
        if (SettingsStore.hasKey(requireContext())) loadRemote();
        else {
            applyDemo(false);
            updateBanner();
        }
    }

    private void loadRemote() {
        if (!isAdded() || getContext() == null) return;
        final int token = ++loadToken;
        loading = true;
        networkError = false;
        lastKeyState = true;
        updateBanner();

        TMDbClient.get(requireContext()).trending(new ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> data) {
                if (token != loadToken || !isAdded()) return;
                if (data == null || data.isEmpty()) {
                    loading = false;
                    networkError = true;
                    applyDemo(true);
                    updateBanner();
                    return;
                }
                applyTrending(data);
                fetchUpcoming(token);
            }

            @Override
            public void onError(String message) {
                if (token != loadToken || !isAdded()) return;
                loading = false;
                networkError = true;
                applyDemo(true);
                updateBanner();
            }
        });
    }

    private void fetchUpcoming(int token) {
        TMDbClient.get(requireContext()).upcoming(new ApiCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> data) {
                if (token != loadToken || !isAdded()) return;
                if (data == null || data.isEmpty()) {
                    applyUpcoming(DemoCatalog.upcoming(), false);
                } else {
                    applyUpcoming(data, true);
                }
                loading = false;
                updateBanner();
            }

            @Override
            public void onError(String message) {
                if (token != loadToken || !isAdded()) return;
                applyUpcoming(DemoCatalog.upcoming(), false);
                loading = false;
                networkError = false;
                updateBanner();
                roadmap("即将上映同步失败，已显示示例片单");
            }
        });
    }

    private void applyDemo(boolean error) {
        if (!isAdded()) return;
        networkError = error;
        loading = false;
        applyTrending(DemoCatalog.trending());
        applyUpcoming(DemoCatalog.upcoming(), false);
        updateBanner();
    }

    private void applyTrending(List<Movie> movies) {
        if (!isAdded() || movies == null || movies.isEmpty()) return;
        hero = movies.get(0);
        renderHero();
        trendingAdapter.submit(movies);
        boolean remote = !hero.demo;
        trendingSource.setText(remote ? R.string.tmdb_source : R.string.local_source);
    }

    private void applyUpcoming(List<Movie> movies, boolean remote) {
        if (!isAdded()) return;
        upcomingAdapter.submit(movies);
        upcomingSource.setText(remote ? R.string.upcoming_tmdb : R.string.upcoming_local);
    }

    private void renderHero() {
        if (hero == null) return;
        heroTitle.setText(hero.title);
        StringBuilder meta = new StringBuilder(hero.year());
        if (hero.genre != null && !hero.genre.isEmpty()) {
            meta.append(" · ").append(hero.genre);
        } else {
            meta.append(" · ").append(hero.typeLabel());
        }
        if (hero.runtime > 0) meta.append(" · ").append(hero.runtime).append(" 分钟");
        if (hero.score > 0) {
            meta.append(hero.demo
                    ? String.format(Locale.CHINA, " · %.1f", hero.score)
                    : String.format(Locale.CHINA, " · TMDb %.1f", hero.score));
        }
        heroMeta.setText(meta);
        heroOverview.setText(hero.overview == null || hero.overview.isEmpty()
                ? "这部影片暂时没有简介。" : hero.overview);

        if (hero.demo && hero.backdropRes != 0) {
            heroImage.setImageResource(hero.backdropRes);
        } else {
            NetworkImageLoader.load(heroImage, hero.backdropUrl(),
                    R.drawable.bg_poster_placeholder);
        }
    }

    private void updateBanner() {
        if (!isAdded()) return;
        boolean hasKey = SettingsStore.hasKey(requireContext());
        loading = loading && hasKey;
        if (hasKey && !networkError && !loading) {
            apiBanner.setVisibility(View.GONE);
            return;
        }
        apiBanner.setVisibility(View.VISIBLE);
        syncProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        bannerAction.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (loading) {
            bannerTitle.setText(R.string.sync_loading);
            bannerDesc.setText("正在获取本周趋势与即将上映");
        } else if (!hasKey) {
            bannerTitle.setText(R.string.connect_tmdb);
            bannerDesc.setText(R.string.connect_tmdb_desc);
            bannerAction.setText(R.string.configure);
        } else {
            bannerTitle.setText(R.string.tmdb_unavailable);
            bannerDesc.setText(R.string.tmdb_fallback);
            bannerAction.setText(R.string.retry);
        }
    }

    private void roadmap(String message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showRoadmapMessage(message);
        }
    }
}
