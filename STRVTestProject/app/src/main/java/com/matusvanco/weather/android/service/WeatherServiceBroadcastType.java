package com.matusvanco.weather.android.service;

import com.matusvanco.weather.android.api.APIInterface;

/**
 * Type of broadcast according to used service call in {@link APIInterface}.
 */
public enum WeatherServiceBroadcastType {

    CURRENT_WEATHER_DATA_RETURNED("com.matusvanco.weather.android.currentWeatherDataReturned"),
    FORECAST_DATA_RETURNED("com.matusvanco.weather.android.forecastDataReturned");

    String value;

    WeatherServiceBroadcastType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
