package com.matusvanco.weather.android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client to OpenWeatherMap to obtain relevant weather text data.
 */
public class APIClient {

    /**
     * Base URL for loading weather data from OpenWeatherMap.
     */
    public static final String BASE_URL = "http://api.openweathermap.org";

    /**
     * @return Instance of {@link Retrofit} which is used to create client with interface {@link APIInterface}
     */
    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
