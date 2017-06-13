package com.matusvanco.weather.android.api;

import com.matusvanco.weather.android.entity.CurrentWeather;
import com.matusvanco.weather.android.entity.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by matva on 6/11/2017.
 */

public interface APIInterface {

    @GET("/data/2.5/weather?")
    Call<CurrentWeather> getCurrentWeather(@Query("q") String city, @Query("units") String units, @Query("cnt") int count, @Query("APPID") String appId);

    @GET("/data/2.5/forecast/daily?")
    Call<Forecast> getForecast(@Query("q") String city, @Query("units") String units, @Query("cnt") int count, @Query("APPID") String appId);

}