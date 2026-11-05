package com.yjh.cinetrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.yjh.cinetrack.data.DemoMovie;

import java.util.Locale;

public class DiscoverFragment extends Fragment {
    private DemoMovie hero;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hero = DemoCatalog.trending().get(0);

        ImageView heroImage = view.findViewById(R.id.hero_image);
        TextView heroTitle = view.findViewById(R.id.hero_title);
        TextView heroMeta = view.findViewById(R.id.hero_meta);
        TextView heroOverview = view.findViewById(R.id.hero_overview);

        heroImage.setImageResource(hero.backdropRes);
        heroTitle.setText(hero.title);
        heroMeta.setText(String.format(Locale.CHINA, "%s · %s · %s · %.1f",
                hero.year(), hero.genre, hero.runtime + " 分钟", hero.score));
        heroOverview.setText(hero.overview);

        RecyclerView trendingList = view.findViewById(R.id.trending_list);
        RecyclerView upcomingList = view.findViewById(R.id.upcoming_list);
        MovieRailAdapter trendingAdapter = new MovieRailAdapter(false, movie ->
                roadmap("《" + movie.title + "》详情页将在第 4 步加入。"));
        MovieRailAdapter upcomingAdapter = new MovieRailAdapter(true, movie ->
                roadmap("《" + movie.title + "》的上映提醒将在第 7 步加入。"));
        trendingList.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.HORIZONTAL, false));
        upcomingList.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.HORIZONTAL, false));
        trendingList.setAdapter(trendingAdapter);
        upcomingList.setAdapter(upcomingAdapter);
        trendingAdapter.submit(DemoCatalog.trending());
        upcomingAdapter.submit(DemoCatalog.upcoming());

        view.findViewById(R.id.search_entry).setOnClickListener(v ->
                ((MainActivity) requireActivity()).selectTab("search"));
        View.OnClickListener detailClick = v ->
                roadmap("《" + hero.title + "》详情页将在第 4 步加入。");
        view.findViewById(R.id.hero_card).setOnClickListener(detailClick);
        MaterialButton heroButton = view.findViewById(R.id.hero_button);
        heroButton.setOnClickListener(detailClick);
    }

    private void roadmap(String message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showRoadmapMessage(message);
        }
    }
}
