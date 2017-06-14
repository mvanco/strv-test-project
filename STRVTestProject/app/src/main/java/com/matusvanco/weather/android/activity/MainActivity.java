package com.matusvanco.weather.android.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
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
     * Current settings.
     */
    SharedPreferences preferences;

    public enum MainActivityFragmentType {
        TODAY(R.string.activity_main_drawer_today, R.id.nav_today),
        FORECAST(R.string.activity_main_drawer_forecast, R.id.nav_forecast),
        SETTINGS(R.string.action_settings, R.id.action_settings);

        /**
         * Int resource of fragment title.
         */
        final int titleRes;

        /**
         * Id of item in drawer or action bar.
         */
        final int itemId;

        MainActivityFragmentType(@StringRes int titleRes, @IdRes int itemId) {
            this.titleRes = titleRes;
            this.itemId = itemId;
        }

        public int getTitleRes() {
            return titleRes;
        }

        public static int getTitleRes(@IdRes int itemId) {
            for (MainActivityFragmentType fragmentType : MainActivityFragmentType.values()) {
                if (fragmentType.itemId == itemId) {
                    return fragmentType.getTitleRes();
                }
            }
            return R.string.empty_string;
        }
    }

    /**
     * Upper color bar with the title.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Layout for the side menu.
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.app_bar_main_progressbar)
    ProgressBar progressBar;

    /**
     * Unbinder for {@link ButterKnife}
     */
    private Unbinder mUnbinder;

    private ActionBarDrawerToggle mToggle;

    private int selectedNavigationItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupActionBar();

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TodayFragment todayTodayFragment = TodayFragment.getInstance();
        fragmentTransaction.add(R.id.fragment_container, todayTodayFragment, TODAY_FRAGMENT_TAG);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(MainActivityFragmentType.TODAY.getTitleRes());
        showInfiniteHorizontalProgressBar(); // I am still waiting for loading data.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.newIntent(this));
            return true;
        } else if (id == R.id.action_about) {
            getAboutDialog().show();
        } else if (id == android.R.id.home) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, TodayFragment.newInstance(), TODAY_FRAGMENT_TAG);
//            transaction.commit();
//            getSupportActionBar().setTitle(MainActivityFragmentType.TODAY.getTitleRes());
//            Log.d("MainActivity", "Clicked back arrow");
            onBackPressed(); // Default behaviour.
            return true;
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
        if (selectedNavigationItemId != item.getItemId()) {
            selectedNavigationItemId = item.getItemId();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (selectedNavigationItemId == R.id.nav_today) {
                fragmentTransaction.replace(R.id.fragment_container, TodayFragment.getInstance(), TODAY_FRAGMENT_TAG);
            } else if (selectedNavigationItemId == R.id.nav_forecast) {
                fragmentTransaction.replace(R.id.fragment_container, ForecastFragment.getInstance(), FORECAST_FRAGMENT_TAG);
            }
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(MainActivityFragmentType.getTitleRes(selectedNavigationItemId));
        } else {
            if (selectedNavigationItemId == R.id.nav_today) {
                WeatherService.getInstance(this).reloadCurrentWeather(); // When selected again, it works like refresh button.
            } else if (selectedNavigationItemId == R.id.nav_forecast) {
                // TODO call appropriate function
            }
        }
        showInfiniteHorizontalProgressBar();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDataLoaded() {
        hideInfiniteHorizontalProgerssBar();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        mToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mToggle);
        drawer.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
    }

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

    private void showInfiniteHorizontalProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideInfiniteHorizontalProgerssBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
