
package com.matusvanco.weather.android.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForecastItem {

    @SerializedName("dt")
    @Expose
    private Integer dt;

    @SerializedName("temp")
    @Expose
    private Temp temp;

    @SerializedName("pressure")
    @Expose
    private Double pressure;

    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = null;

    @SerializedName("speed")
    @Expose
    private Double speed;

    @SerializedName("deg")
    @Expose
    private Integer deg;

    @SerializedName("clouds")
    @Expose
    private Integer clouds;

    @SerializedName("rain")
    @Expose
    private Double rain;

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public void setDeg(Integer deg) {
        this.deg = deg;
    }

    public Integer getClouds() {
        return clouds;
    }

    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    public Double getRain() {
        return rain;
    }

    public void setRain(Double rain) {
        this.rain = rain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForecastItem that = (ForecastItem) o;

        if (dt != null ? !dt.equals(that.dt) : that.dt != null) return false;
        if (temp != null ? !temp.equals(that.temp) : that.temp != null) return false;
        if (pressure != null ? !pressure.equals(that.pressure) : that.pressure != null)
            return false;
        if (humidity != null ? !humidity.equals(that.humidity) : that.humidity != null)
            return false;
        if (weather != null ? !weather.equals(that.weather) : that.weather != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (deg != null ? !deg.equals(that.deg) : that.deg != null) return false;
        if (clouds != null ? !clouds.equals(that.clouds) : that.clouds != null) return false;
        return rain != null ? rain.equals(that.rain) : that.rain == null;

    }

    @Override
    public int hashCode() {
        int result = dt != null ? dt.hashCode() : 0;
        result = 31 * result + (temp != null ? temp.hashCode() : 0);
        result = 31 * result + (pressure != null ? pressure.hashCode() : 0);
        result = 31 * result + (humidity != null ? humidity.hashCode() : 0);
        result = 31 * result + (weather != null ? weather.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (deg != null ? deg.hashCode() : 0);
        result = 31 * result + (clouds != null ? clouds.hashCode() : 0);
        result = 31 * result + (rain != null ? rain.hashCode() : 0);
        return result;
    }
}
