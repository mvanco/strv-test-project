package com.matusvanco.weather.android.service;

/**
 * Interface for callbacks used for responses.
 */
public interface ServiceCallback<ST, ET> {

    /**
     * Called from service on success response.
     *
     * @param result response result of SuccessType
     */
    void passSuccess(ST result);

    /**
     * Called from itself on success response.
     *
     * @param result response result of SuccessType
     */
    void onSuccess(ST result);

    /**
     * Called from service on error response.
     *
     * @param error response result of ErrorType
     */
    void passError(ET error);

    /**
     * Called from itself on error response.
     *
     * @param error response result of ErrorType
     */
    void onError(ET error);

    /**
     * Cancel the request in progress.
     */
    void cancel();

    /**
     * @return whether request is being canceled
     */
    boolean isCancelled();
}