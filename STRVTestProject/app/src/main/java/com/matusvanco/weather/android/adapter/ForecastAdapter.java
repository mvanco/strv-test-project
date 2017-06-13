package com.matusvanco.weather.android.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.entity.List;
import com.matusvanco.weather.android.entity.Temp;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.WeatherService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by matva on 6/13/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private java.util.List<List> forecastItems;

    private TemperatureUnit temperatureUnit;

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.forecast_adapter_row_item_weather_icon)
        public AppCompatImageView icon;

        @BindView(R.id.forecast_adapter_row_item_main_weather)
        public AppCompatTextView mainWeather;

        @BindView(R.id.forecast_adapter_row_item_temperature)
        public AppCompatTextView temperature;


        public ForecastViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ForecastAdapter(java.util.List<List> forecastItems, TemperatureUnit temperatureUnit) {
        this.forecastItems = forecastItems;
        this.temperatureUnit = temperatureUnit;
    }

    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_adapter_row_item, parent, false);
        ForecastViewHolder holder = new ForecastViewHolder(itemView);
        return new ForecastViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        List forecastItem = forecastItems.get(position);


        //holder.icon.
        holder.mainWeather.setText(forecastItem.getWeather().get(0).getMain());
        holder.temperature.setText(forecastItem.getTemp().getFormattedTemp(temperatureUnit, true));
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

}
