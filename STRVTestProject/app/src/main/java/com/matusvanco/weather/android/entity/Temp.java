
package com.matusvanco.weather.android.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Temp temp = (Temp) o;

        if (day != null ? !day.equals(temp.day) : temp.day != null) return false;
        if (min != null ? !min.equals(temp.min) : temp.min != null) return false;
        if (max != null ? !max.equals(temp.max) : temp.max != null) return false;
        if (night != null ? !night.equals(temp.night) : temp.night != null) return false;
        if (eve != null ? !eve.equals(temp.eve) : temp.eve != null) return false;
        return morn != null ? morn.equals(temp.morn) : temp.morn == null;

    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (night != null ? night.hashCode() : 0);
        result = 31 * result + (eve != null ? eve.hashCode() : 0);
        result = 31 * result + (morn != null ? morn.hashCode() : 0);
        return result;
    }
}
