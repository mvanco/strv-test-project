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

import static com.matusvanco.weather.android.service.WeatherServiceBroadcastType.FORECAST_DATA_RETURNED;

/**
 * Forecast page after selection in side menu.
 */
public class ForecastFragment extends Fragment implements OnPrecipitationIconLoadedListener, OnAddPrecipitationIconToLoadListener {

    /**
     * Key for storing the {@code todayFragmentTextViewsLoaded} field.
     */
    public static final String FORECAST_FRAGMENT_ITEMS_LOADED_KEY = "com.matusvanco.weather.android.mForecastFragmentItemsLoaded";

    /**
     * Key for storing the {@code todayPrecipitationImageLoaded} field.
     */
    public static final String FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY = "com.matusvanco.weather.android.mForecastPrecipitationIconsToLoad";

    /**
     * View that shows list of forecast items.
     */
    @BindView(R.id.fragment_forecast_recycler_view)
    RecyclerView mRecyclerView;

    /**
     * Listens to data updates. Every page should load asynchronously and show data passed via
     * broadcast. Use this receiver to get all necessary data for your page.
     */
    private BroadcastReceiver mForecastFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FORECAST_DATA_RETURNED.getValue().equals(action)) {
                List<ForecastItem> forecastItems = WeatherService.getInstance(getContext()).getForecastItems();
                setForecastItems(forecastItems);
                loadForecast();
            }
        }
    };

    /**
     * List of forecast items currently shown in this fragment.
     */
    List<ForecastItem> forecastItems = new ArrayList<>();

    /**
     * Temperature unit of currently shown items.
     */
    TemperatureUnit temperatureUnit = TemperatureUnit.DEFAULT_INSTANCE;

    /**
     * Unbinder.
     */
    private Unbinder mUnbinder;

    /**
     * Callback for the event where data of fragment are fully loaded after {@code reloadForecast()}
     * on {@link WeatherService} has been called (it is also called by default during fragment creation).
     */
    private  OnDataLoadedListener mOnDataLoadedListener;

    /**
     * Adapter used to create item view shown in this fragment.
     */
    private ForecastAdapter mAdapter;

    /**
     * True if there is returned list of weater texts with temperatures from server to show.
     */
    private boolean mForecastFragmentItemsLoaded = false;

    /**
     * How many icons should be still load (progress bar must be active).
     */
    private int mForecastPrecipitationIconsToLoad = 0;

    /**
     * @return instance of this fragment
     */
    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FORECAST_FRAGMENT_ITEMS_LOADED_KEY)) {
                mForecastFragmentItemsLoaded = savedInstanceState.getBoolean(FORECAST_FRAGMENT_ITEMS_LOADED_KEY);
            }

            if (savedInstanceState.containsKey(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY)) {
                mForecastPrecipitationIconsToLoad = savedInstanceState.getInt(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY);
            }
        }

        temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        mAdapter = new ForecastAdapter(this, new ArrayList<ForecastItem>(), temperatureUnit);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FORECAST_DATA_RETURNED.getValue());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mForecastFragmentReceiver, intentFilter);

        List<ForecastItem> forecastItems = WeatherService.getInstance(getContext()).getForecastItems();
        if (forecastItems != null) { // If there is already loaded data it is used for initialization of fragment.
            if (!forecastItems.equals(this.forecastItems)) { // There are different items, we need to reload.
                setForecastItems(forecastItems);
                loadForecast();
                return;
            }
        }

        WeatherService.getInstance(getContext()).loadCurrentSettings();
        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        if (this.temperatureUnit != temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
            WeatherService.getInstance(getContext()).reloadForecast(); // We need to update data to be shown in proper unit even if the items are the same.
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mForecastFragmentReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnDataLoadedListener = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataLoadedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FORECAST_FRAGMENT_ITEMS_LOADED_KEY, mForecastFragmentItemsLoaded);
        outState.putInt(FORECAST_PRECIPITATION_ICONS_TO_LOAD_KEY, mForecastPrecipitationIconsToLoad);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onPrecipitationIconLoaded() {
        mForecastPrecipitationIconsToLoad--;
        Log.d("ForecastFragment", "Image is loaded, " + mForecastPrecipitationIconsToLoad + " to load");
        if (isDataLoaded()) {
            mOnDataLoadedListener.onDataLoaded();
        }
    }

    @Override
    public void onAddPrecipitationIconToLoad() {
        mForecastPrecipitationIconsToLoad++;
        Log.d("ForecastFragment", "New image to load, totally " + mForecastPrecipitationIconsToLoad);
    }

    private void loadForecast() {
        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit(); // Unit can be also changed from settings.
        mAdapter.setForecastItems(forecastItems, temperatureUnit);
        mAdapter.notifyDataSetChanged();

        mForecastFragmentItemsLoaded = true;
        if (isDataLoaded()) {
            mOnDataLoadedListener.onDataLoaded();
        }
    }

    private boolean isDataLoaded() {
        return (mForecastPrecipitationIconsToLoad == 0) && mForecastFragmentItemsLoaded;
    }

    private void setForecastItems(List<ForecastItem> forecastItems) {
        if (this.forecastItems != null) {
            this.forecastItems.clear();
        }
        this.forecastItems.addAll(forecastItems);
    }
}