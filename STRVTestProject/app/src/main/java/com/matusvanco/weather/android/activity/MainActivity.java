package com.matusvanco.weather.android.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.fragment.ForecastFragment;
import com.matusvanco.weather.android.fragment.OnDataLoadedListener;
import com.matusvanco.weather.android.fragment.TodayFragment;
import com.matusvanco.weather.android.service.WeatherService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Main activity with side menu and mToolbar which shows Today and Forecast pages as the fragments.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnDataLoadedListener {

    /**
     * TAG for {@link TodayFragment}.
     */
    private static final String TODAY_FRAGMENT_TAG = "TodayFragment";

    /**
     * TAG for {@link ForecastFragment}.
     */
    private static final String FORECAST_FRAGMENT_TAG = "ForecastFragment";

    /**
     * Key for the {@code mFragmentType} field.
     */
    private static final String FRAGMENT_TYPE_KEY = "com.matusvanco.weather.android.mFragmentType";

    /**
     * Current settings.
     */
    SharedPreferences mPreferences;

    /**
     * Upper color bar with the title.
     */
    @BindView(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    /**
     * Layout for the side menu.
     */
    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout mDrawer;

    /**
     * Side menu which is animated as mDrawer.
     */
    @BindView(R.id.activity_main_nav_view)
    NavigationView mNavigationView;

    /**
     * Infinite horizontal progress bar below the ActionBar.
     */
    @BindView(R.id.activity_main_content_progress_bar)
    ProgressBar mProgressBar;

    /**
     * Unbinder for {@link ButterKnife}
     */
    private Unbinder mUnbinder;

    /**
     * Handles synchronization between ActionBar hamburger button and side menu.
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * Type of fragment which should be currently shown.
     */
    private MainActivityFragmentType mFragmentType = MainActivityFragmentType.TODAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setupActionBar();

        mNavigationView.setNavigationItemSelectedListener(this);

        /**
         * Set Today screen as inital content of this activity.
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) { // Add initial fragment only during first start.
            TodayFragment todayTodayFragment = TodayFragment.getInstance();
            fragmentTransaction.add(R.id.activity_main_content_fragment_container, todayTodayFragment, TODAY_FRAGMENT_TAG);
            fragmentTransaction.commit();
        } else {
            if (savedInstanceState.containsKey(FRAGMENT_TYPE_KEY)) {
                mFragmentType = MainActivityFragmentType.values()[savedInstanceState.getInt(FRAGMENT_TYPE_KEY)];
            }
        }
        getSupportActionBar().setTitle(mFragmentType.getTitleRes());

        showInfiniteHorizontalProgressBar(); // I am still waiting for loading data.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(FRAGMENT_TYPE_KEY, mFragmentType.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.newIntent(this));
            return true;
        } else if (id == R.id.action_about) {
            getAboutDialog().show();
        }

        Log.d("MainActivity", "Clicked some of options items");

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mFragmentType.getItemId() != item.getItemId()) { // Different option than already shown has been selected.
            mFragmentType = MainActivityFragmentType.fromItemId(item.getItemId());

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (mFragmentType.getItemId() == R.id.nav_today) {
                mFragmentType = MainActivityFragmentType.TODAY;
                fragmentTransaction.replace(R.id.activity_main_content_fragment_container, TodayFragment.getInstance(), TODAY_FRAGMENT_TAG);
            } else if (mFragmentType.getItemId() == R.id.nav_forecast) {
                mFragmentType = MainActivityFragmentType.FORECAST;
                fragmentTransaction.replace(R.id.activity_main_content_fragment_container, ForecastFragment.getInstance(), FORECAST_FRAGMENT_TAG);
            }
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(mFragmentType.getTitleRes());
        } else { // The same option has been selected - fragment is not recreated but only data are reloaded.
            if (mFragmentType.getItemId() == R.id.nav_today) {
                WeatherService.getInstance(this).reloadCurrentWeather(); // When selected again, it works like refresh button.
            } else if (mFragmentType.getItemId() == R.id.nav_forecast) {
                WeatherService.getInstance(this).reloadForecast();
            }
        }

        showInfiniteHorizontalProgressBar();

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDataLoaded() {
        hideInfiniteHorizontalProgerssBar();
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mDrawer.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
    }

    /**
     * @return Dialog for About section.
     */
    private AlertDialog getAboutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.dialog_about_title);
        alertDialogBuilder.setMessage(R.string.dialog_about_message);
        alertDialogBuilder.setPositiveButton(R.string.dialog_about_button_text,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing to do here.
                    }
                });
        final AlertDialog aboutDialog = alertDialogBuilder.create();
        aboutDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) { // Customize the style of message text in order to correspond with design.
                TextView messageTextView = (TextView) aboutDialog.findViewById(android.R.id.message);
                messageTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.dialog_about_messaage_text_color));
                messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dialog_about_message_text_size));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageTextView.getLayoutParams();
                params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dialog_about_message_top_padding), 0, getResources().getDimensionPixelOffset(R.dimen.dialog_about_message_bottom_padding));
                messageTextView.setLayoutParams(params);
            }
        });

        return aboutDialog;
    }

    /**
     * Shows horizontal progress bar.
     */
    private void showInfiniteHorizontalProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide horizontal progress bar.
     */
    private void hideInfiniteHorizontalProgerssBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}