package com.yjh.cinetrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yjh.cinetrack.MainActivity;
import com.yjh.cinetrack.R;

public class PlaceholderFragment extends Fragment {
    private static final String ARG_ICON = "icon";
    private static final String ARG_STEP = "step";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";
    private static final String ARG_NEXT = "next";

    public static PlaceholderFragment newInstance(@DrawableRes int icon, String step,
                                                  String title, String desc, String next) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ICON, icon);
        args.putString(ARG_STEP, step);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, desc);
        args.putString(ARG_NEXT, next);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        Bundle args = getArguments();
        if (args != null) {
            ((ImageView) view.findViewById(R.id.placeholder_icon))
                    .setImageResource(args.getInt(ARG_ICON, R.drawable.ic_sparkle));
            ((TextView) view.findViewById(R.id.placeholder_step))
                    .setText(args.getString(ARG_STEP, "STEP 00"));
            ((TextView) view.findViewById(R.id.placeholder_title))
                    .setText(args.getString(ARG_TITLE, "即将开放"));
            ((TextView) view.findViewById(R.id.placeholder_desc))
                    .setText(args.getString(ARG_DESC, ""));
            ((TextView) view.findViewById(R.id.placeholder_next))
                    .setText(args.getString(ARG_NEXT, ""));
        }
        view.findViewById(R.id.btn_back_discover).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDiscover();
            }
        });
        return view;
    }
}
