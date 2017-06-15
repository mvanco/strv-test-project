package com.matusvanco.weather.android.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.entity.CurrentWeather;
import com.matusvanco.weather.android.entity.LengthUnit;
import com.matusvanco.weather.android.entity.Temp;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.OnPrecipitationIconLoadedListener;
import com.matusvanco.weather.android.service.WeatherService;
import com.matusvanco.weather.android.service.WeatherService.WeatherServiceBroadcastType;
import com.matusvanco.weather.android.view.WeatherParameter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.matusvanco.weather.android.service.WeatherService.WeatherServiceBroadcastType.CURRENT_WEATHER_DATA_RETURNED;

/**
 * Today page after selection in side menu (or shown by default after start).
 */
public class TodayFragment extends android.support.v4.app.Fragment implements OnPrecipitationIconLoadedListener {

    /**
     * City name.
     */
    @BindView(R.id.fragment_today_city)
    TextView cityTextView;

    /**
     * Image of current weather.
     */
    @BindView(R.id.fragment_today_precipitation_icon)
    ImageView precipitationImageView;

    /**
     * Weather text describing briefly weather condition.
     */
    @BindView(R.id.fragment_today_precipitation_text)
    TextView precipitationTextView;

    /**
     * Temperature.
     */
    @BindView(R.id.fragment_today_temperature)
    TextView temperatureTextView;

    /**
     * Humidity parameter.
     */
    @BindView(R.id.fragment_today_weather_parameter_humidity)
    WeatherParameter humidityParameterTextView;

    /**
     * Precipitation parameter.
     */
    @BindView(R.id.fragment_today_weather_parameter_precipitation)
    WeatherParameter precipitationParameterTextView;

    /**
     * Pressure parameter.
     */
    @BindView(R.id.fragment_today_weather_parameter_pressure)
    WeatherParameter pressureParameterTextView;

    /**
     * Wind parameter.
     */
    @BindView(R.id.fragment_today_weather_parameter_wind)
    WeatherParameter windParameterTextView;

    /**
     * Direction parameter.
     */
    @BindView(R.id.fragment_today_weather_parameter_direction)
    WeatherParameter directionParameterTextView;

    /**
     * Key for storing the {@code todayFragmentTextViewsLoaded} field.
     */
    private static final String TODAY_FRAGMENT_TEXT_VIEWS_LOADED_KEY = "com.matusvanco.weather.android.todayFragmentTextViewsLoaded";

    /**
     * Key for storing the {@code todayPrecipitationImageLoaded} field.
     */
    private static final String TODAY_PRECIPITATION_IMAGE_LOADED_KEY = "com.matusvanco.weather.android.todayPrecipitationImageLoaded";

    /**
     * Constant for the conversion between mi/h and m/s.
     */
    private static final Double METRIC_IMPERIAL_SPEED_PARAMETER_CONVERSION_COEFFICIENT = 2.23694;

    /**
     * Unbinder.
     */
    private Unbinder unbinder;

    /**
     * Callback for the event where data of fragment are fully loaded after {@code reloadCurrentWeather()}
     * on {@link WeatherService} has been called (it is also called by default during fragment creation).
     */
    private OnDataLoadedListener onDataLoadedListener;

    /**
     * Singleton instance.
     */
    private static TodayFragment instance; // We need only one instance.

    /**
     * True if there is returned current weather data.
     */
    private boolean todayFragmentTextViewsLoaded = false;

    /**
     * True if the precipitation image has been loaded.
     */
    private boolean todayPrecipitationImageLoaded = false;

    /**
     * @return Singleton instance
     */
    public static TodayFragment getInstance() {
        if (instance == null) {
            instance = new TodayFragment();
        }
        return instance;
    }

    /**
     * Listens to data updates. Every page should load asynchronously and show data passed via
     * broadcast. Use this receiver to get all necessary data for your page.
     */
    private BroadcastReceiver todayFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == CURRENT_WEATHER_DATA_RETURNED.getValue()) {
                CurrentWeather currentWeather = WeatherService.getInstance(getContext()).getCurrentWeather();
                loadCurrentWeather(currentWeather);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TODAY_FRAGMENT_TEXT_VIEWS_LOADED_KEY)) {
                todayFragmentTextViewsLoaded = savedInstanceState.getBoolean(TODAY_FRAGMENT_TEXT_VIEWS_LOADED_KEY);
            }

            if (savedInstanceState.containsKey(TODAY_PRECIPITATION_IMAGE_LOADED_KEY)) {
                todayPrecipitationImageLoaded = savedInstanceState.getBoolean(TODAY_PRECIPITATION_IMAGE_LOADED_KEY);
            }
        }

        loadEmptyWeather();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WeatherServiceBroadcastType.CURRENT_WEATHER_DATA_RETURNED.getValue());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(todayFragmentReceiver, intentFilter);

        WeatherService.getInstance(getContext()).reloadCurrentWeather(); // Must be here because we need to automatically refresh after return from Settings.
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(todayFragmentReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onDataLoadedListener = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataLoadedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(TODAY_FRAGMENT_TEXT_VIEWS_LOADED_KEY, todayFragmentTextViewsLoaded);
        outState.putBoolean(TODAY_PRECIPITATION_IMAGE_LOADED_KEY, todayPrecipitationImageLoaded);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPrecipitationIconLoaded() {
        todayPrecipitationImageLoaded = true;
        if (todayPrecipitationImageLoaded && todayFragmentTextViewsLoaded) {
            onDataLoadedListener.onDataLoaded();
        }
    }

    /**
     * Initializes the views with dashboards and correct unit before current data are loaded.
     */
    private void loadEmptyWeather() {
        cityTextView.setText("-");
        precipitationTextView.setText("-");

        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        temperatureTextView.setText(new Temp().getFormattedEmptyTemp(temperatureUnit));
    }

    /**
     * Loads the current weather according to {@link CurrentWeather} instance provided.
     * @param currentWeather Current weather
     */
    private void loadCurrentWeather(CurrentWeather currentWeather) {
        cityTextView.setText(currentWeather.getName());

        WeatherService.getInstance(getContext()).loadPrecipitationImage(
                this, currentWeather.getWeather().get(0).getIcon(), precipitationImageView, this);

        precipitationTextView.setText(currentWeather.getWeather().get(0).getMain());

        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        temperatureTextView.setText(new Temp(currentWeather.getMain().getTemp()).getFormattedTemp(temperatureUnit, false));

        humidityParameterTextView.setWeatherText(String.valueOf(Math.round(currentWeather.getMain().getHumidity())));

        if (currentWeather.getRain() != null) {
            precipitationParameterTextView.setWeatherText(String.valueOf(Math.round(currentWeather.getRain().get3h())));
        } else {
            precipitationParameterTextView.setWeatherText("0");
        }

        pressureParameterTextView.setWeatherText(String.valueOf(Math.round(currentWeather.getMain().getPressure())));
        windParameterTextView.setWeatherText(String.valueOf(convertSpeedToProperUnit(currentWeather.getWind().getSpeed())));
        directionParameterTextView.setWeatherText(currentWeather.getWind().getTextDeg());

        todayFragmentTextViewsLoaded = true;
        if (todayPrecipitationImageLoaded && todayFragmentTextViewsLoaded) {
            onDataLoadedListener.onDataLoaded();
        }
    }

    /**
     * Convert speed to m/s or mi/h according to current set temperature unit.
     * @param speed Speed before conversion
     * @return Speed after conversion
     */
    private int convertSpeedToProperUnit(Double speed) {
        WeatherService service = WeatherService.getInstance(getContext());
        LengthUnit lengthUnit = service.getLengthUnit(); // We want convert accroding to this setting.
        TemperatureUnit temperatureUnit = service.getTemperatureUnit(); // Server request has been made according to this settings.

        Double result = speed;
        if ((lengthUnit == LengthUnit.MILE) && (temperatureUnit == TemperatureUnit.CELSIUS)) { // Server has been queried in metric units so speed is in meters and must be converted.
            result = speed * METRIC_IMPERIAL_SPEED_PARAMETER_CONVERSION_COEFFICIENT;
        } else if ((lengthUnit == LengthUnit.METER) && (temperatureUnit == TemperatureUnit.FAHRENHEIT)) { // Server has been queried in imperial units so speed is in miles and must be converted.
            result = speed / METRIC_IMPERIAL_SPEED_PARAMETER_CONVERSION_COEFFICIENT;
        }

        return (int) Math.round(result);
    }
}
