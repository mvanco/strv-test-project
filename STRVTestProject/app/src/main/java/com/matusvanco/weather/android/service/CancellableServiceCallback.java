package com.matusvanco.weather.android.service;

import android.util.Log;

/**
 * Cancellable callback used for responses.
 */
public abstract class CancellableServiceCallback<ST, ET> implements ServiceCallback<ST, ET> {

    private static final String LOG_TAG = "CancellableCallback";

    /**
     * Whether request is being canceled.
     */
    private volatile boolean cancelled = false;

    private Object lock = new Object();

    @Override
    public void passSuccess(ST result) {
        synchronized (lock) {
            if (!cancelled) {
                onSuccess(result);
            } else {
                Log.i(LOG_TAG, "onSuccess() not called. Callback was canceled.");
            }
        }
    }

    /**
     * Called on success response.
     *
     * @param result response result of SuccessType
     */
    @Override
    public void onSuccess(ST result) {

    }

    @Override
    public void passError(ET error) {
        synchronized (lock) {
            if (!cancelled) {
                onError(error);
            } else {
                Log.i(LOG_TAG, "onError() not called. Callback was canceled.");
            }
        }
    }

    /**
     * Called on error response.
     *
     * @param error response result of ErrorType
     */
    @Override
    public void onError(ET error) {

    }

    /**
     * Cancel the request in progress.
     */
    @Override
    public boolean isCancelled() {
        synchronized (lock) {
            return cancelled;
        }
    }

    /**
     * @return whether request is being canceled
     */
    @Override
    public void cancel() {
        synchronized (lock) {
            cancelled = true;
        }
    }
}