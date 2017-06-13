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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.adapter.ForecastAdapter;
import com.matusvanco.weather.android.entity.List;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.WeatherService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by matva on 6/8/2017.
 */

public class ForecastFragment extends Fragment {

    private static ForecastFragment instance;

    Unbinder unbinder;

    private  OnDataLoadedListener mCallback;

    private ForecastAdapter mAdapter;

    private java.util.List<List> mForecastItems;

    @BindView(R.id.fragment_forecast_recycler_view)
    RecyclerView recyclerView;

    /**
     * Listens to data updates. Every page should load asynchronously and show data passed via
     * broadcast. Use this receiver to get all necessary data for your page.
     */
    private BroadcastReceiver forecastFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            java.util.List<List> forecastItems = WeatherService.getInstance(getContext()).getForecastItems();
            loadForecast(forecastItems);
        }
    };


    public static ForecastFragment getInstance() {
        if (instance == null) {
            instance = new ForecastFragment();
        }
        return instance;
    }

    public ForecastFragment() {
        mForecastItems = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        WeatherService.getInstance(getContext()).reloadForecast();
        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();

        mAdapter = new ForecastAdapter(mForecastItems, temperatureUnit);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeatherService.getInstance(getContext()).reloadForecast();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WeatherService.WeatherServiceBroadcastType.FORECAST_DID_CHANGE.getValue());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(forecastFragmentReceiver, intentFilter);
        WeatherService.getInstance(getContext()).reloadCurrentWeather();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(forecastFragmentReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDataLoadedListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadForecast(java.util.List<List> forecastItems) {
        mForecastItems = forecastItems;
        mAdapter.notifyDataSetChanged();
        mCallback.onDataLoaded();
    }
}