
package com.matusvanco.weather.android.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("message")
    @Expose
    private Double message;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("sunrise")
    @Expose
    private Integer sunrise;

    @SerializedName("sunset")
    @Expose
    private Integer sunset;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMessage() {
        return message;
    }

    public void setMessage(Double message) {
        this.message = message;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }

    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sys sys = (Sys) o;

        if (type != null ? !type.equals(sys.type) : sys.type != null) return false;
        if (id != null ? !id.equals(sys.id) : sys.id != null) return false;
        if (message != null ? !message.equals(sys.message) : sys.message != null) return false;
        if (country != null ? !country.equals(sys.country) : sys.country != null) return false;
        if (sunrise != null ? !sunrise.equals(sys.sunrise) : sys.sunrise != null) return false;
        return sunset != null ? sunset.equals(sys.sunset) : sys.sunset == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (sunrise != null ? sunrise.hashCode() : 0);
        result = 31 * result + (sunset != null ? sunset.hashCode() : 0);
        return result;
    }
}
