
package com.matusvanco.weather.android.entity;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matusvanco.weather.android.R;

import java.text.DecimalFormat;
import java.util.Locale;

public class Temp {

    @SerializedName("day")
    @Expose
    private Double day;
    @SerializedName("min")
    @Expose
    private Double min;
    @SerializedName("max")
    @Expose
    private Double max;
    @SerializedName("night")
    @Expose
    private Double night;
    @SerializedName("eve")
    @Expose
    private Double eve;
    @SerializedName("morn")
    @Expose
    private Double morn;

    public Temp() {
    }

    public Temp(Double day) {
        this.day = day;
    }

    public String getFormattedTemp(TemperatureUnit temperatureUnit, boolean useUnit) {
        DecimalFormat format = new DecimalFormat("#0");
        if (useUnit) {
            return format.format(day) + temperatureUnit.getUnitSign();
        } else {
            return format.format(day) + "°";
        }
    }

    public String getFormattedEmptyTemp(TemperatureUnit temperatureUnit) {
        return "- " + temperatureUnit.getUnitSign();
    }

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getNight() {
        return night;
    }

    public void setNight(Double night) {
        this.night = night;
    }

    public Double getEve() {
        return eve;
    }

    public void setEve(Double eve) {
        this.eve = eve;
    }

    public Double getMorn() {
        return morn;
    }

    public void setMorn(Double morn) {
        this.morn = morn;
    }

}
