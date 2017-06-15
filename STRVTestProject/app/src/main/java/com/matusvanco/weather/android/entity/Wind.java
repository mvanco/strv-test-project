
package com.matusvanco.weather.android.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private Double speed;

    @SerializedName("deg")
    @Expose
    private Double deg;

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDeg() {
        return deg;
    }

    public String getTextDeg() {
        try {
            double doubleDeg = deg.doubleValue();
        } catch (NullPointerException e) {
            return "-";
        }
        if (deg > 0 && deg < 23) {
            return "N";
        } else if (deg < 68) {
            return "NE";
        } else if (deg < 113) {
            return "E";
        } else if (deg < 158) {
            return "SE";
        } else if (deg < 203) {
            return "S";
        } else if (deg < 248) {
            return "SW";
        } else if (deg < 293) {
            return "W";
        } else if (deg < 338) {
            return "NW";
        } else {
            return "N";
        }
    }

    public void setDeg(Double deg) {
        this.deg = deg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wind wind = (Wind) o;

        if (speed != null ? !speed.equals(wind.speed) : wind.speed != null) return false;
        return deg != null ? deg.equals(wind.deg) : wind.deg == null;

    }

    @Override
    public int hashCode() {
        int result = speed != null ? speed.hashCode() : 0;
        result = 31 * result + (deg != null ? deg.hashCode() : 0);
        return result;
    }
}
