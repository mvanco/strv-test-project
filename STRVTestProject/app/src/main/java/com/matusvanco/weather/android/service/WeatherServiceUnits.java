package com.matusvanco.weather.android.service;

import android.content.Context;

import com.matusvanco.weather.android.R;

/**
 * Represent "metric" or "imperial" string which is used in query on the OpenWeatherMap server.
 */
public enum WeatherServiceUnits {
    METRIC(R.string.api_interface_units_metric),
    IMPERIAL(R.string.api_interface_units_imperial);

    public static final WeatherServiceUnits DEFAULT_INSTANCE = WeatherServiceUnits.METRIC;

    private int titleRes;

    WeatherServiceUnits(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public String getTitle(Context context) {
        return context.getString(getTitleRes());
    }
}