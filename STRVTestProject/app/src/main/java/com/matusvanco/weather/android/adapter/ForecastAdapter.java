package com.matusvanco.weather.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.entity.ForecastItem;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.OnAddPrecipitationIconToLoadListener;
import com.matusvanco.weather.android.service.OnPrecipitationIconLoadedListener;
import com.matusvanco.weather.android.service.WeatherService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Used for obtaining one row in the Forecast page.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> implements OnPrecipitationIconLoadedListener {

    /**
     * This is used for Glide to handle mFragment states.
     */
    private Fragment mFragment;

    /**
     * Items which are shown byt this adapter.
     */
    private List<ForecastItem> mForecastItems;

    /**
     * Temperature unit according to which the content is adapted.
     */
    private TemperatureUnit mTemperatureUnit;

    /**
     * Raised when there is recognized new image to be load.
     */
    private OnAddPrecipitationIconToLoadListener mOnAddPrecipitationIconToLoadListener;

    /**
     * Raised when there is loaded one more image.
     */
    private OnPrecipitationIconLoadedListener onPrecipitationIconLoaded;

    /**
     * Shows forecast for 7 days, starting from tomorrow. Parent mFragment which holds this adapter
     * must implement {@code OnAddPrecipitationIconToLoadListener} and {@code OnPrecipitationIconLoadedListener}
     * in order to handle progress bar properly.
     * @param fragment Parent framgent which uses this adapter
     * @param forecastItems Set of forecast items to be shown
     * @param temperatureUnit Temperature unit (°C or °F) to be used in print
     */
    public ForecastAdapter(Fragment fragment, List<ForecastItem> forecastItems, TemperatureUnit temperatureUnit) {
        this.mFragment = fragment;
        this.mForecastItems = forecastItems;
        this.mTemperatureUnit = temperatureUnit;

        try {
            this.onPrecipitationIconLoaded = (OnPrecipitationIconLoadedListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " must implement OnPrecipitationIconLoadedListener");
        }

        try {
            this.mOnAddPrecipitationIconToLoadListener = (OnAddPrecipitationIconToLoadListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " must implement OnAddPrecipitationIconToLoadListener");
        }
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fragment_forecast_row_item_weather_icon)
        public ImageView icon;

        @BindView(R.id.fragment_forecast_row_item_main_weather)
        public TextView mainWeather;

        @BindView(R.id.fragment_forecast_row_item_temperature)
        public TextView temperature;


        public ForecastViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_forecast_row_item, parent, false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = mForecastItems.get(position);
        mOnAddPrecipitationIconToLoadListener.onAddPrecipitationIconToLoad();
        WeatherService.getInstance(mFragment.getContext()).loadPrecipitationImage(mFragment, forecastItem.getWeather().get(0).getIcon(), holder.icon, this);
        holder.mainWeather.setText(getLongWeatherText(forecastItem.getWeather().get(0).getMain(), position));
        holder.temperature.setText(forecastItem.getTemp().getFormattedTemp(mTemperatureUnit, true));
    }

    @Override
    public int getItemCount() {
        return mForecastItems.size();
    }

    @Override
    public void onPrecipitationIconLoaded() {
        onPrecipitationIconLoaded.onPrecipitationIconLoaded(); // Delegation to upper Fragment.
    }

    /**
     * @param weatherText Weather text returned from OpenWeatherMap
     * @param position Index position in the list
     * @return Long title used in the forecast item row in format "<Weather text> on <Day of week>"
     */
    private String getLongWeatherText(String weatherText, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, position + 1); // First (0 index) is 'tomorrow' in this list.
        Date date = calendar.getTime();
        String dayOfTheWeek = dateFormat.format(date);

        String preposition = mFragment.getString(R.string.fragment_forecast_row_item_preposition);

        return String.format("%s %s %s", weatherText, preposition, dayOfTheWeek);
    }

    public void setForecastItems(List<ForecastItem> forecastItems, TemperatureUnit temperatureUnit) {
        this.mForecastItems = forecastItems;
        this.mTemperatureUnit = temperatureUnit;
    }
}
