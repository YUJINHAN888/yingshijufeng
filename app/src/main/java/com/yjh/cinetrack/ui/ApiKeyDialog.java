package com.yjh.cinetrack.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.yjh.cinetrack.R;
import com.yjh.cinetrack.data.SettingsStore;
import com.yjh.cinetrack.net.ApiCallback;
import com.yjh.cinetrack.net.TMDbClient;
import com.yjh.cinetrack.util.Ui;
import com.yjh.cinetrack.util.Ui;

public final class ApiKeyDialog {
    private ApiKeyDialog() {}

    public static void show(Activity activity, Runnable onSaved) {
        if (activity == null || activity.isFinishing()) return;
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_api_key, null, false);
        TextInputEditText input = view.findViewById(R.id.api_key_input);
        TextView error = view.findViewById(R.id.api_key_error);
        TextView open = view.findViewById(R.id.open_tmdb);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save_and_connect, null);
        if (SettingsStore.hasKey(activity)) builder.setNeutralButton(R.string.clear_key, null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(ignored -> {
            Button positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positive.setTextColor(Ui.color(activity, R.color.primary));
            Button negative = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
            negative.setTextColor(Ui.color(activity, R.color.text_secondary));
            Button neutral = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL);
            if (neutral != null) neutral.setTextColor(Ui.color(activity, R.color.primary));

            positive.setOnClickListener(v -> {
                String key = input.getText() == null ? "" : input.getText().toString().trim();
                if (key.isEmpty()) {
                    error.setText(R.string.api_needs_key);
                    error.setVisibility(View.VISIBLE);
                    return;
                }
                error.setVisibility(View.GONE);
                positive.setEnabled(false);
                positive.setText(R.string.sync_loading);
                TMDbClient.get(activity).validateKey(key, new ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        SettingsStore.saveKey(activity, key);
                        Ui.toast(activity, activity.getString(R.string.api_saved));
                        dialog.dismiss();
                        if (onSaved != null) onSaved.run();
                    }

                    @Override
                    public void onError(String message) {
                        error.setText(message);
                        error.setVisibility(View.VISIBLE);
                        positive.setEnabled(true);
                        positive.setText(R.string.save_and_connect);
                    }
                });
            });

            if (neutral != null) {
                neutral.setOnClickListener(v -> {
                    SettingsStore.clearKey(activity);
                    dialog.dismiss();
                    if (onSaved != null) onSaved.run();
                });
            }

            open.setOnClickListener(v -> {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.themoviedb.org/settings/api")));
                } catch (Exception ex) {
                    Ui.toast(activity, "无法打开浏览器");
                }
            });
        });
        dialog.show();
    }
}
