package com.matusvanco.weather.android.activity;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import com.matusvanco.weather.android.R;

/**
 * Created by matva on 6/15/2017.
 */
public enum MainActivityFragmentType {
    TODAY(R.string.activity_main_drawer_today, R.id.nav_today),
    FORECAST(R.string.activity_main_drawer_forecast, R.id.nav_forecast);

    /**
     * Int resource of fragment title.
     */
    final int titleRes;

    /**
     * Id of item in mDrawer or action bar.
     */
    final int itemId;

    /**
     * Default fragment type instance.
     */
    private static MainActivityFragmentType defaultInstance = MainActivityFragmentType.TODAY;

    /**
     * Create new instance of MainActivityFragmentType from item id.
     *
     * @param itemId Which item id is used for creation
     * @return Instance of {@link MainActivityFragmentType}
     */
    public static MainActivityFragmentType fromItemId(@IdRes int itemId) {
        for (MainActivityFragmentType fragmentType : MainActivityFragmentType.values()) {
            if (fragmentType.itemId == itemId) {
                return fragmentType;
            }
        }
        return defaultInstance;
    }

    MainActivityFragmentType(@StringRes int titleRes, @IdRes int itemId) {
        this.titleRes = titleRes;
        this.itemId = itemId;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public int getItemId() {
        return itemId;
    }
}
