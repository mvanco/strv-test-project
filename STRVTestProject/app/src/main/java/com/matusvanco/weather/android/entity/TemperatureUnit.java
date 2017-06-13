package com.matusvanco.weather.android.entity;

import android.content.Context;

import com.matusvanco.weather.android.R;

import java.util.Locale;

/**
 * Created by matva on 6/12/2017.
 */

public enum TemperatureUnit {
    CELSIUS(R.string.fragment_settings_temperature_celsius, "°C"),
    FAHRENHEIT(R.string.fragment_settings_temperature_fahrenheit, "°F");

    public static final TemperatureUnit DEFAULT_INSTANCE = TemperatureUnit.CELSIUS;

    private int titleRes;
    private String unitSign;

    TemperatureUnit(int titleRes, String unitSign) {
        this.titleRes = titleRes;
        this.unitSign = unitSign;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public String getTitle(Context context) {
        return context.getString(getTitleRes());
    }

    public static TemperatureUnit fromTitle(Context context, String title) {
        for (TemperatureUnit temperatureUnit : values()) {
            if (temperatureUnit.getTitle(context).equals(title)) {
                return temperatureUnit;
            }
        }
        throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Unknown Temp.TemperatureUnit %s.", title));
    }

    public String getUnitSign() {
        return unitSign;
    }
}