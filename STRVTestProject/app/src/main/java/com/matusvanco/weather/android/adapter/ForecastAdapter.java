package com.matusvanco.weather.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.entity.ForecastItem;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.OnPrecipitationIconLoadedListener;
import com.matusvanco.weather.android.service.WeatherService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by matva on 6/13/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> implements OnPrecipitationIconLoadedListener {

    /**
     * This is used for Glide to handle fragment states.
     */
    private Fragment fragment;

    private java.util.List<ForecastItem> forecastItems;

    private TemperatureUnit temperatureUnit;

    private OnPrecipitationIconLoadedListener callback;

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fragment_forecast_row_item_weather_icon)
        public AppCompatImageView icon;

        @BindView(R.id.fragment_forecast_row_item_main_weather)
        public AppCompatTextView mainWeather;

        @BindView(R.id.fragment_forecast_row_item_temperature)
        public AppCompatTextView temperature;


        public ForecastViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ForecastAdapter(Fragment fragment, java.util.List<ForecastItem> forecastItems, TemperatureUnit temperatureUnit, OnPrecipitationIconLoadedListener callback) {
        this.fragment = fragment;
        this.forecastItems = forecastItems;
        this.temperatureUnit = temperatureUnit;
        this.callback = callback;
    }

    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_forecast_row_item, parent, false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = forecastItems.get(position);
        WeatherService.getInstance(fragment.getContext()).loadPrecipitationImage(fragment, forecastItem.getWeather().get(0).getIcon(), holder.icon, this);
        holder.mainWeather.setText(getLongWeatherText(forecastItem.getWeather().get(0).getMain(), position));
        holder.temperature.setText(forecastItem.getTemp().getFormattedTemp(temperatureUnit, true));
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    @Override
    public void onPrecipitationIconLoaded() {
        callback.onPrecipitationIconLoaded(); // Delegation to upper Fragment.
    }

    private String getLongWeatherText(String weatherText, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, position + 1); // First (0 index) is 'tomorrow' in this list.
        Date date = calendar.getTime();
        String dayOfTheWeek = dateFormat.format(date);

        String preposition = fragment.getString(R.string.fragment_forecast_row_item_preposition);

        return String.format("%s %s %s", weatherText, preposition, dayOfTheWeek);
    }
}
