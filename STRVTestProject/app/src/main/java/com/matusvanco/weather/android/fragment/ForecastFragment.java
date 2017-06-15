package com.matusvanco.weather.android.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.adapter.ForecastAdapter;
import com.matusvanco.weather.android.entity.ForecastItem;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.OnAddPrecipitationIconToLoadListener;
import com.matusvanco.weather.android.service.OnPrecipitationIconLoadedListener;
import com.matusvanco.weather.android.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.matusvanco.weather.android.service.WeatherService.WeatherServiceBroadcastType.FORECAST_DATA_RETURNED;

/**
 * Forecast page after selection in side menu.
 */
public class ForecastFragment extends Fragment implements OnPrecipitationIconLoadedListener, OnAddPrecipitationIconToLoadListener {

    /**
     * Key for storing the {@code todayFragmentTextViewsLoaded} field.
     */
    private static final String FORECAST_FRAGMENT_ITEMS_LOADED_KEY = "com.matusvanco.weather.android.forecastFragmentItemsLoaded";

    /**
     * Key for storing the {@code todayPrecipitationImageLoaded} field.
     */
    private static final String FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY = "com.matusvanco.weather.android.forecastPrecipitationIconsToLoad";

    /**
     * Singleton instance of ForecastFragment.
     */
    private static ForecastFragment instance;

    /**
     * Unbinder.
     */
    Unbinder unbinder;

    /**
     * Callback for the event where data of fragment are fully loaded after {@code reloadForecast()}
     * on {@link WeatherService} has been called (it is also called by default during fragment creation).
     */
    private  OnDataLoadedListener onDataLoadedListener;

    /**
     * Adapter used to create item view shown in this fragment.
     */
    private ForecastAdapter mAdapter;

    /**
     * True if there is returned list of weater texts with temperatures from server to show.
     */
    private boolean forecastFragmentItemsLoaded = false;

    /**
     * How many icons should be still load (progress bar must be active).
     */
    private int forecastPrecipitationIconsToLoad = 0;

    /**
     * View that shows list of forecast items.
     */
    @BindView(R.id.fragment_forecast_recycler_view)
    RecyclerView recyclerView;

    /**
     * Listens to data updates. Every page should load asynchronously and show data passed via
     * broadcast. Use this receiver to get all necessary data for your page.
     */
    private BroadcastReceiver forecastFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == FORECAST_DATA_RETURNED.getValue()) {
                List<ForecastItem> forecastItems = WeatherService.getInstance(getContext()).getForecastItems();
                forecastItems.remove(0); // We need to remove first item because it is current weather which is not needed.
                loadForecast(forecastItems);
            }
        }
    };

    /**
     * @return Singleton instance
     */
    public static ForecastFragment getInstance() {
        if (instance == null) {
            instance = new ForecastFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FORECAST_FRAGMENT_ITEMS_LOADED_KEY)) {
                forecastFragmentItemsLoaded = savedInstanceState.getBoolean(FORECAST_FRAGMENT_ITEMS_LOADED_KEY);
            }

            if (savedInstanceState.containsKey(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY)) {
                forecastPrecipitationIconsToLoad = savedInstanceState.getInt(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY);
            }
        }

        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        mAdapter = new ForecastAdapter(this, new ArrayList<ForecastItem>(), temperatureUnit);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FORECAST_DATA_RETURNED.getValue());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(forecastFragmentReceiver, intentFilter);

        WeatherService.getInstance(getContext()).reloadForecast();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(forecastFragmentReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onDataLoadedListener = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataLoadedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FORECAST_FRAGMENT_ITEMS_LOADED_KEY, forecastFragmentItemsLoaded);
        outState.putInt(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY, forecastPrecipitationIconsToLoad);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadForecast(List<ForecastItem> forecastItems) {
        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit(); // Unit can be also changed from settings.
        mAdapter.setForecastItems(forecastItems, temperatureUnit);
        mAdapter.notifyDataSetChanged();

        forecastFragmentItemsLoaded = true;
        if (isDataLoaded()) {
            onDataLoadedListener.onDataLoaded();
        }
    }

    @Override
    public void onPrecipitationIconLoaded() {
        forecastPrecipitationIconsToLoad--;
        Log.d("ForecastFragment", "Image is loaded, " + forecastPrecipitationIconsToLoad + " to load");
        if (isDataLoaded()) {
            onDataLoadedListener.onDataLoaded();
        }
    }

    @Override
    public void onAddPrecipitationIconToLoad() {
        forecastPrecipitationIconsToLoad++;
        Log.d("ForecastFragment", "New image to load, totally " + forecastPrecipitationIconsToLoad);
    }

    private boolean isDataLoaded() {
        return (forecastPrecipitationIconsToLoad == 0) && forecastFragmentItemsLoaded;
    }
}