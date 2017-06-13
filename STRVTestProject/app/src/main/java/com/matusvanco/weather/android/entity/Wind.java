
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

}
