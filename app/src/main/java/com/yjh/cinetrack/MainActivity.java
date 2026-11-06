package com.yjh.cinetrack;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.yjh.cinetrack.ui.ApiKeyDialog;
import com.yjh.cinetrack.ui.DiscoverFragment;
import com.yjh.cinetrack.ui.PlaceholderFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_DISCOVER = "discover";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_WATCHLIST = "watchlist";
    private static final String TAG_PROFILE = "profile";
    private static final String STATE_TAB = "current_tab";

    private FrameLayout container;
    private BottomNavigationView bottomNav;
    private String currentTag = TAG_DISCOVER;
    private boolean suppressNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.fragment_container);
        bottomNav = findViewById(R.id.bottom_nav);
        applyInsets();

        bottomNav.setOnItemSelectedListener(item -> {
            if (suppressNavigation) return true;
            int id = item.getItemId();
            if (id == R.id.nav_search) selectTab(TAG_SEARCH);
            else if (id == R.id.nav_watchlist) selectTab(TAG_WATCHLIST);
            else if (id == R.id.nav_profile) selectTab(TAG_PROFILE);
            else selectTab(TAG_DISCOVER);
            return true;
        });

        if (savedInstanceState != null) {
            currentTag = savedInstanceState.getString(STATE_TAB, TAG_DISCOVER);
        }
        selectTab(currentTag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TAB, currentTag);
    }

    private void enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }

    private void applyInsets() {
        View root = findViewById(R.id.main_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            container.setPadding(0, bars.top, 0, 0);
            bottomNav.setPadding(0, 0, 0, bars.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    public void selectTab(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (Fragment fragment : manager.getFragments()) transaction.hide(fragment);

        Fragment target = manager.findFragmentByTag(tag);
        if (target == null) {
            target = createFragment(tag);
            transaction.add(R.id.fragment_container, target, tag);
        } else {
            transaction.show(target);
        }
        transaction.commit();
        currentTag = tag;
        syncBottomNav();
    }

    private Fragment createFragment(String tag) {
        if (TAG_SEARCH.equals(tag)) {
            return PlaceholderFragment.newInstance(R.drawable.ic_search, "STEP 03", "搜索",
                    "TMDb 多类型检索、结果筛选和最近搜索，将在第 3 步加入。",
                    "下一步计划：输入即搜、电影 / 剧集筛选、搜索历史。");
        }
        if (TAG_WATCHLIST.equals(tag)) {
            return PlaceholderFragment.newInstance(R.drawable.ic_bookmark, "STEP 05", "我的看单",
                    "本地看单会在第 5 步接入 SQLite，并支持想看 / 在看 / 看过三种状态。",
                    "下一步计划：收藏、状态筛选、统计与底部角标。");
        }
        if (TAG_PROFILE.equals(tag)) {
            return PlaceholderFragment.newInstance(R.drawable.ic_person, "STEP 09", "我的",
                    "个人数据、TMDb Key、海报缓存与通知设置，会在后续步骤汇聚到这里。",
                    "下一步计划：API 配置、缓存管理、通知状态与版本信息。");
        }
        return new DiscoverFragment();
    }

    private void syncBottomNav() {
        int id = R.id.nav_discover;
        if (TAG_SEARCH.equals(currentTag)) id = R.id.nav_search;
        else if (TAG_WATCHLIST.equals(currentTag)) id = R.id.nav_watchlist;
        else if (TAG_PROFILE.equals(currentTag)) id = R.id.nav_profile;
        suppressNavigation = true;
        bottomNav.setSelectedItemId(id);
        suppressNavigation = false;
    }

    public void openDiscover() {
        selectTab(TAG_DISCOVER);
    }

    public void showApiKeyDialog() {
        ApiKeyDialog.show(this, () -> {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_DISCOVER);
            if (fragment instanceof DiscoverFragment) ((DiscoverFragment) fragment).reload();
        });
    }

    public void showRoadmapMessage(String message) {
        Snackbar.make(findViewById(R.id.main_root), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.surface_high))
                .setTextColor(getColor(R.color.text_primary))
                .setActionTextColor(getColor(R.color.primary))
                .show();
    }
}
