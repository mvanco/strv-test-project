package com.matusvanco.weather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.fragment.ForecastFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Handling the settings of units.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * TAG for {@link ForecastFragment}.
     */
    private static final String SETTINGS_FRAGMENT_TAG = "SettingsFragment";

    /**
     * Unbinder for {@link ButterKnife}
     */
    private Unbinder mUnbinder;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUnbinder = ButterKnife.bind(this);

        setupActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); // Default behaviour.
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_settings);
        }
    }
}
