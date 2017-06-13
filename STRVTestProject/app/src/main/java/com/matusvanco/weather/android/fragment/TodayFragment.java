package com.matusvanco.weather.android.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
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
import com.matusvanco.weather.android.service.OnTodayPrecipitationImageLoadedListener;
import com.matusvanco.weather.android.service.WeatherService;
import com.matusvanco.weather.android.service.WeatherService.WeatherServiceBroadcastType;
import com.matusvanco.weather.android.view.WeatherParameter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by matva on 6/8/2017.
 */

public class TodayFragment extends Fragment implements OnTodayPrecipitationImageLoadedListener {

    @BindView(R.id.fragment_today_city)
    AppCompatTextView cityTextView;

    @BindView(R.id.fragment_today_precipitation_icon)
    AppCompatImageView precipitationImageView;

    @BindView(R.id.fragment_today_precipitation_text)
    AppCompatTextView precipitationTextView;

    @BindView(R.id.fragment_today_temperature)
    AppCompatTextView temperatureTextView;

    @BindView(R.id.fragment_today_weather_parameter_humidity)
    WeatherParameter humidityParameterTextView;

    @BindView(R.id.fragment_today_weather_parameter_precipitation)
    WeatherParameter precipitationParameterTextView;

    @BindView(R.id.fragment_today_weather_parameter_pressure)
    WeatherParameter pressureParameterTextView;

    @BindView(R.id.fragment_today_weather_parameter_wind)
    WeatherParameter windParameterTextView;

    @BindView(R.id.fragment_today_weather_parameter_direction)
    WeatherParameter directionParameterTextView;

    private Unbinder unbinder;

    private OnDataLoadedListener mCallback;

    private static final Double METRIC_IMPERIAL_SPEED_PARAMETER_CONVERSION_COEFFICIENT = 2.23694;

    private static TodayFragment instance; // We need only one instance.

    private boolean todayPrecipitationImageLoaded = false;
    private boolean todayFragmentTextViewsLoaded = false;


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
            CurrentWeather currentWeather = WeatherService.getInstance(getContext()).getCurrentWeather();
            loadCurrentWeather(currentWeather);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadEmptyCurrentWeater();



        WeatherService.getInstance(getContext()).reloadCurrentWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WeatherServiceBroadcastType.CURRENT_WEATHER_DID_CHANGE.getValue());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(todayFragmentReceiver, intentFilter);
        WeatherService.getInstance(getContext()).reloadCurrentWeather();
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
            mCallback = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDataLoadedListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onTodayPrecipitationImageLoaded() {
        todayPrecipitationImageLoaded = true;
        if (todayPrecipitationImageLoaded && todayFragmentTextViewsLoaded) {
            mCallback.onDataLoaded();
        }
    }

    private void loadEmptyCurrentWeater() {
        cityTextView.setText("-");
        precipitationTextView.setText("-");

        TemperatureUnit temperatureUnit = WeatherService.getInstance(getContext()).getTemperatureUnit();
        temperatureTextView.setText(new Temp().getFormattedEmptyTemp(temperatureUnit));
    }

    private void loadCurrentWeather(CurrentWeather currentWeather) {
        cityTextView.setText(currentWeather.getName());

        WeatherService.getInstance(getContext()).loadTodayPrecipitationImage(
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
            mCallback.onDataLoaded();
        }
    }

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
