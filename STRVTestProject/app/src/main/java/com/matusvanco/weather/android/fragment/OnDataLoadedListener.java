package com.matusvanco.weather.android.fragment;

/**
 * All fragments that are using the asynchronous calls use this to notify data has been successfully loaded
 * and the fragment is showing proper content.
 */
public interface OnDataLoadedListener {

    public void onDataLoaded();

}