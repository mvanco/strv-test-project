package com.matusvanco.weather.android.api;

import com.matusvanco.weather.android.entity.CurrentWeather;
import com.matusvanco.weather.android.entity.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface used to communication with OpenWeatherMap.
 */
public interface APIInterface {

    /**
     * @param city Name of city
     * @param units Metric or imperial
     * @param count Number of required items
     * @param appId Unique token for access
     * @return Current weather as the {@link CurrentWeather} instance
     */
    @GET("/data/2.5/weather?")
    Call<CurrentWeather> getCurrentWeather(@Query("q") String city, @Query("units") String units, @Query("cnt") int count, @Query("APPID") String appId);

    /**
     * @param city Name of city
     * @param units Metric or imperial
     * @param count Number of required items
     * @param appId Unique token for access
     * @return Forecast as the {@link Forecast} instance
     */
    @GET("/data/2.5/forecast/daily?")
    Call<Forecast> getForecast(@Query("q") String city, @Query("units") String units, @Query("cnt") int count, @Query("APPID") String appId);

}