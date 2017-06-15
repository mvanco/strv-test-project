package com.matusvanco.weather.android.entity;

import android.content.Context;

import com.matusvanco.weather.android.R;

import java.util.Locale;

/**
 * Created by matva on 6/12/2017.
 */

public enum LengthUnit {

    METER(R.string.fragment_settings_length_meter),
    MILE(R.string.fragment_settings_length_mile);

    public static final LengthUnit DEFAULT_INSTANCE = LengthUnit.METER;

    private int titleRes;

    LengthUnit(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public String getTitle(Context context) {
        return context.getString(getTitleRes());
    }

    public static LengthUnit fromTitle(Context context, String title) {
        for (LengthUnit lengthUnit : values()) {
            if (lengthUnit.getTitle(context).equals(title)) {
                return lengthUnit;
            }
        }
        throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Unknown length unit %s.", title));
    }
}