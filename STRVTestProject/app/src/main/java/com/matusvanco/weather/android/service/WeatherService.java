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
import com.matusvanco.weather.android.entity.ForecastItem;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.fragment.SettingsFragment;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Wrapper arround APIClient provides services used in application. Callbacks are handled using broadcast
 * receivers.
 */
public class WeatherService {

    /**
     * Type of broadcast according to used service call in {@link APIInterface}.
     */
    public enum WeatherServiceBroadcastType {
        CURRENT_WEATHER_DATA_RETURNED("com.matusvanco.weather.android.currentWeatherDataReturned"),
        FORECAST_DATA_RETURNED("com.matusvanco.weather.android.forecastDataReturned");

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

    /**
     * Base URL for loading images from OpenWeatherMap.
     */
    public static final String WEATHER_ICON_BASE_URL = "http://openweathermap.org/img/w/";

    /**
     * Suffix used to build the proper URL for loading images from OpenWeatherMap.
     */
    public static final String WEATHER_ICON_SUFFIX = ".png";

    /**
     * City which is queried on the server OpenWeatherMap.
     */
    private static final String QUERY_PARAMETER_CITY = "Brno,CZ"; // Brno,CZ

    /**
     * Count of requested item in the current weather query.
     */
    private static final int QUERY_PARAMETER_CURRENT_WEATHER_COUNT = 1;

    /**
     * Count of forecast items which are requested.
     */
    private static final int QUERY_PARAMETER_FORECAST_COUNT = 8;

    /**
     * Unique token use for accessing OpenWeatherMap service.
     */
    private static final String QUERY_PARAMETER_APP_ID = "1c8254bc0e4c06431648f7aa6d641537";

    /**
     * Current weather.
     */
    private CurrentWeather currentWeather;

    /**
     * Forecast
     */
    private List<ForecastItem> forecastItems;

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

    /**
     * Lenght unit.
     */
    private LengthUnit lengthUnit;

    /**
     * Temperature unit.
     */
    private TemperatureUnit temperatureUnit;

    /**
     * Returns singleton {@link WeatherService} instance. It is created if it does not exist yet.
     *
     * @param context Context
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
     * @param context Context
     */
    private WeatherService(Context context) {
        this.appContext = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadCurrentSettings();
    }

    /**
     * Get current weather if it has been already obtained from server and previously queried using {@code reloadCurrentWeather} metod.
     *
     * @return {@link CurrentWeather} instance with current weather
     */
    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Start the query on the server to load data about current weather.
     */
    public void reloadCurrentWeather() {
        APIInterface client = APIClient.getClient().create(APIInterface.class);
        Call<CurrentWeather> currentWeatherCall = client.getCurrentWeather(
                QUERY_PARAMETER_CITY,
                getWeatherServiceUnits().getTitle(appContext),
                QUERY_PARAMETER_CURRENT_WEATHER_COUNT,
                QUERY_PARAMETER_APP_ID);

        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                setCurrentWeather(response.body());
                sendBroadcast(WeatherServiceBroadcastType.CURRENT_WEATHER_DATA_RETURNED);
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                // Nothing to do here so far.
            }
        });

    }

    /**
     * Get forecast if it has been already obtained from server and previously queried using {@code reloadForecast} metod.
     *
     * @return {@link List<ForecastItem>} instance with forecast for each day
     */
    public List<ForecastItem> getForecastItems() {
        return forecastItems;
    }

    /**
     * Start the query on the server to load data about forecast.
     */
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
                setForecastItems(response.body().getList());
                sendBroadcast(WeatherServiceBroadcastType.FORECAST_DATA_RETURNED);
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                // Nothing to do here so far.
            }
        });
    }

    /**
     * Loads image into view using Glide library.
     *
     * @param fragment Fragment which has the ImageViews which has to be loaded
     * @param iconTitle Title of icon according to OpenWeatherMap
     * @param view View where the loaded icon will be loaded
     * @param onPrecipitationIconLoadedListener Is used when the icon is successfully loaded
     */
    public void loadPrecipitationImage(Fragment fragment, String iconTitle, ImageView view, final OnPrecipitationIconLoadedListener onPrecipitationIconLoadedListener) {
        if (fragment.getActivity() != null) { // TodayFragment must be already attached to activity.
            Glide.with(fragment).load(WEATHER_ICON_BASE_URL + iconTitle + WEATHER_ICON_SUFFIX)
                .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    onPrecipitationIconLoadedListener.onPrecipitationIconLoaded();
                    return false;
                }
            }).into(view);
        }
    }

    /**
     * @return Current weather server units according to current settings (metric or imperial).
     */
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

    /**
     * Loads current settings from {@link SharedPreferences}.
     */
    private void loadCurrentSettings() {
        String lengthUnitPreference = preferences.getString(
                SettingsFragment.LENGTH_LIST_PREFERENCE_KEY,
                LengthUnit.DEFAULT_INSTANCE.getTitle(appContext));
        lengthUnit = LengthUnit.fromTitle(appContext, lengthUnitPreference);

        String temperatureUnitPreference  = preferences.getString(
                SettingsFragment.TEMPERATURE_LIST_PREFERENCE_KEY,
                TemperatureUnit.DEFAULT_INSTANCE.getTitle(appContext));

        temperatureUnit = TemperatureUnit.fromTitle(appContext, temperatureUnitPreference);
    }

    /**
     * @return Loaded length unit
     */
    public LengthUnit getLengthUnit() {
        return lengthUnit;
    }

    /**
     * @return Loaded temperature unit.
     */
    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    /**
     * Send proper broadcast according to broadcast type.
     *
     * @param weatherServiceBroadcastType Broadcast type
     */
    private void sendBroadcast(WeatherServiceBroadcastType weatherServiceBroadcastType) {
        Intent intent = new Intent(weatherServiceBroadcastType.getValue());
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }

    /**
     * Stores curently loaded current weather into field for future use.
     *
     * @param currentWeather {@link CurrentWeather} instance
     */
    private void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    /**
     * Stores curently loaded forecast into field for future use.
     *
     * @param forecastItems {@link List<ForecastItem>} instance
     */
    private void setForecastItems(List<ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
    }
}
