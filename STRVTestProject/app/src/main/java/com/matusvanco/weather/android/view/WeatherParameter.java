package com.matusvanco.weather.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matusvanco.weather.android.R;
import com.matusvanco.weather.android.entity.LengthUnit;
import com.matusvanco.weather.android.entity.TemperatureUnit;
import com.matusvanco.weather.android.service.WeatherService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by matva on 6/10/2017.
 */

public class WeatherParameter extends LinearLayout {

    @BindView(R.id.custom_view_weather_parameter_icon)
    ImageView imageView;

    @BindView(R.id.custom_view_weather_parameter_text)
    TextView textView;

    /**
     * Matches with the {@code R.styleable.WeatherParameter_weatherUnit} id of attribute.
     */
    private static final int WEATHER_UNIT_HUMIDITY = 0;
    private static final int WEATHER_UNIT_PRECIPITATION = 1;
    private static final int WEATHER_UNIT_PRESSURE = 2;
    private static final int WEATHER_UNIT_WIND = 3;
    private static final int WEATHER_UNIT_DIRECTION = 4;

    private int weatherUnit = 0;


    public WeatherParameter(Context context) {
        super(context);
        init(context, null);
    }

    public WeatherParameter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setupParentView();
        inflate(context, R.layout.custom_view_weather_parameter, this);
        ButterKnife.bind(this);
        setupChildrenViews(context, attrs);
    }

    private void setupParentView() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    private void setupChildrenViews(Context context, AttributeSet attrs) {
        if (attrs == null) {
            // Handle wrong state - print error message and end the application.
        }

        /**
         * Get attributes from {@link AttributeSet}.
         */
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeatherParameter, 0, 0);
        Drawable icon = a.getDrawable(R.styleable.WeatherParameter_weatherIcon);
        String text = a.getString(R.styleable.WeatherParameter_weatherText);
        weatherUnit = a.getInt(R.styleable.WeatherParameter_weatherUnitFor, 1);

        /**
         * Bind data to the layout.
         */
        imageView.setImageDrawable(icon);
        textView.setText(text);
    }

    public void setWeatherText(String text) {
        String unitSuffix = "";
        switch (weatherUnit) {
            case WEATHER_UNIT_HUMIDITY:
                unitSuffix = "%";
                break;
            case WEATHER_UNIT_PRECIPITATION:
                unitSuffix = " mm";
                break;
            case WEATHER_UNIT_PRESSURE:
                unitSuffix = " hPa";
                break;
            case WEATHER_UNIT_WIND:
                LengthUnit lengthUnit = WeatherService.getInstance(getContext()).getLengthUnit();
                if (lengthUnit == LengthUnit.METER) {
                    unitSuffix = " m/s";
                } else if (lengthUnit == LengthUnit.MILE) {
                    unitSuffix = " mi/h";
                }

                break;
            case WEATHER_UNIT_DIRECTION:
                unitSuffix = "";
                break;
        }

        textView.setText(text + unitSuffix);
    }
}
