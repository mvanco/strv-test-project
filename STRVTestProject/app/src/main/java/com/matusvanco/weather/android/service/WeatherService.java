package com.matusvanco.weather.android.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.matusvanco.weather.android.api.APIClient;
import com.matusvanco.weather.android.api.APIInterface;
import com.matusvanco.weather.android.entity.CurrentWeather;
import com.matusvanco.weather.android.entity.Forecast;
import com.matusvanco.weather.android.entity.LengthUnit;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.fragment.SettingsFragment;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matva on 6/12/2017.
 */

public class WeatherService {

    public enum WeatherServiceBroadcastType {
        CURRENT_WEATHER_DID_CHANGE("com.matusvanco.weather.android.currentWeatherDidChange"),
        FORECAST_DID_CHANGE("com.matusvanco.weather.android.forecastDidChange");

        String value;

        WeatherServiceBroadcastType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static WeatherServiceBroadcastType fromValue(String value) {
            for (WeatherServiceBroadcastType type : WeatherServiceBroadcastType.values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Unknown OffersServiceBroadcastType %s.", value));
        }
    }

    public static final String WEATHER_ICON_BASE_URL = "http://openweathermap.org/img/w/";
    public static final String WEATHER_ICON_SUFFIX = ".png";

    /**
     * Current weather.
     */
    private CurrentWeather currentWeather;

    /**
     * Forecast
     */
    private Forecast forecast;

    /**
     * Callbacks waiting for card offers model.
     */
    //private List<CancellableServiceCallback<CardsModel, Void>> waitingAsyncCardsCallbacks;

    /**
     * Current settings.
     */
    SharedPreferences preferences;

    /**
     * Singleton instance.
     */
    private static WeatherService instance;

    /**
     * Application context.
     */
    private Context appContext;

    private LengthUnit lengthUnit;

    private TemperatureUnit temperatureUnit;

    //private Len lengthUnits;

    private static final String QUERY_PARAMETER_CITY = "Brno,CZ"; // Brno,CZ

    private static final int QUERY_PARAMETER_FORECAST_COUNT = 7;

    private static final String QUERY_PARAMETER_APP_ID = "1c8254bc0e4c06431648f7aa6d641537";

    /**
     * Returns singleton {@link WeatherService} instance.
     * It is created if it does not exist yet.
     *
     * @param context context
     * @return singleton {@link WeatherService} instance
     */
    public static synchronized WeatherService getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherService(context);
        }
        return instance;
    }

    /**
     * Private constructor to prevent unwanted instantiation.
     *
     * @param context context
     */
    private WeatherService(Context context) {
        this.appContext = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadCurrentSettings();
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void reloadCurrentWeather() {
        APIInterface client = APIClient.getClient().create(APIInterface.class);
        Call<CurrentWeather> currentWeatherCall = client.getCurrentWeather(
                QUERY_PARAMETER_CITY,
                getWeatherServiceUnits().getTitle(appContext),
                QUERY_PARAMETER_FORECAST_COUNT,
                QUERY_PARAMETER_APP_ID);

        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                setCurrentWeather(response.body());
                sendBroadcast(WeatherServiceBroadcastType.CURRENT_WEATHER_DID_CHANGE);
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                // Nothing to do here so far.
            }
        });

    }

    public Forecast getForecast() {
        return forecast;
    }

    public void reloadForecast() {
        loadCurrentSettings();

        APIInterface client = APIClient.getClient().create(APIInterface.class);
        Call<Forecast> forecastCall = client.getForecast(
                QUERY_PARAMETER_CITY,
                getWeatherServiceUnits().getTitle(appContext),
                QUERY_PARAMETER_FORECAST_COUNT,
                QUERY_PARAMETER_APP_ID);

        forecastCall.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                setForecast(response.body());
                sendBroadcast(WeatherServiceBroadcastType.FORECAST_DID_CHANGE);
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                // Nothing to do here so far.
            }
        });
    }

    public void loadTodayPrecipitationImage(Fragment fragment, String iconTitle, ImageView view, final OnTodayPrecipitationImageLoadedListener callback) {
        if (fragment.getActivity() != null) { // Fragment must be already attached to activity.
            Glide.with(fragment).load(WEATHER_ICON_BASE_URL + iconTitle + WEATHER_ICON_SUFFIX)
                .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    callback.onTodayPrecipitationImageLoaded();
                    return false;
                }
            }).into(view);
        }
    }

    private WeatherServiceUnits getWeatherServiceUnits() {
        loadCurrentSettings();

        WeatherServiceUnits units = WeatherServiceUnits.DEFAULT_INSTANCE;
        if (temperatureUnit == TemperatureUnit.CELSIUS) {
            units = WeatherServiceUnits.METRIC;
        } else if (temperatureUnit == TemperatureUnit.FAHRENHEIT) {
            units = WeatherServiceUnits.IMPERIAL;
        }
        return units;
    }

    private void loadCurrentSettings() {
        String lengthUnitPreference = preferences.getString(
                SettingsFragment.LENGTH_LIST_PREFERENCE_KEY,
                LengthUnit.DEFAULT_INSTANCE.getTitle(appContext));
        lengthUnit = LengthUnit.fromTitle(appContext, lengthUnitPreference);

        String temperatureUnitPreference  = preferences.getString(
                SettingsFragment.TEMPERATURE_LIST_PREFERENCE_KEY,
                TemperatureUnit.DEFAULT_INSTANCE.getTitle(appContext));

        temperatureUnit = TemperatureUnit.fromTitle(appContext, temperatureUnitPreference);

        // TODO load lengthUnits
    }

    public LengthUnit getLengthUnit() {
        return lengthUnit;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

//    public String getLengthUnits() {
//        return lengthUnits; //TODO is needed?
//    }

    private void sendBroadcast(WeatherServiceBroadcastType weatherServiceBroadcastType) {
        Intent intent = new Intent(weatherServiceBroadcastType.getValue());
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }

    private void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    private void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }
}
