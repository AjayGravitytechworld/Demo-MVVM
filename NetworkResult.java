package com.example.demoapp;

// Generic wrapper - API ke teen states handle karta hai: Loading, Success, Error
public class NetworkResult<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String message;

    private NetworkResult(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // Loading state
    public static <T> NetworkResult<T> loading() {
        return new NetworkResult<>(Status.LOADING, null, null);
    }

    // Success state - data ke saath
    public static <T> NetworkResult<T> success(T data) {
        return new NetworkResult<>(Status.SUCCESS, data, null);
    }

    // Error state - message ke saath
    public static <T> NetworkResult<T> error(String message) {
        return new NetworkResult<>(Status.ERROR, null, message);
    }

    public Status getStatus() { return status; }
    public T getData() { return data; }
    public String getMessage() { return message; }

    public boolean isLoading() { return status == Status.LOADING; }
    public boolean isSuccess() { return status == Status.SUCCESS; }
    public boolean isError() { return status == Status.ERROR; }
}
