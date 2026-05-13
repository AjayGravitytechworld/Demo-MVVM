package com.example.demoapp;

import androidx.lifecycle.MutableLiveData;

import com.example.demoapp.RetrofitClient;
import com.example.demoapp.WeatherApiService;
import com.example.demoapp.ForecastResponse;
import com.example.demoapp.WeatherResponse;
import com.example.demoapp.Constants;
import com.example.demoapp.NetworkResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository Layer - MVVM ka yeh important part hai.
 * ViewModel directly API nahi call karta — Repository karta hai.
 * Repository data source (API/DB) se data laata hai aur ViewModel ko deta hai.
 */
public class WeatherRepository {

    private final WeatherApiService apiService;

    public WeatherRepository() {
        apiService = RetrofitClient.getInstance().getWeatherApiService();
    }

    // ─── Current Weather by City ──────────────────────────────────────────────

    public void getCurrentWeather(String cityName,
                                  MutableLiveData<NetworkResult<WeatherResponse>> liveData) {

        // Pehle loading state post karo
        liveData.postValue(NetworkResult.loading());

        Call<WeatherResponse> call = apiService.getCurrentWeatherByCity(
                cityName,
                Constants.API_KEY,
                Constants.UNITS
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call,
                                   Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success → data ke saath post karo
                    liveData.postValue(NetworkResult.success(response.body()));
                } else {
                    // HTTP error (401, 404, etc.)
                    String errorMsg = getErrorMessage(response.code());
                    liveData.postValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Network failure (no internet, timeout, etc.)
                liveData.postValue(NetworkResult.error(
                        "Network Error: " + t.getMessage()));
            }
        });
    }

    // ─── 5-Day Forecast by City ───────────────────────────────────────────────

    public void get5DayForecast(String cityName,
                                MutableLiveData<NetworkResult<ForecastResponse>> liveData) {

        liveData.postValue(NetworkResult.loading());

        // 40 data points (5 days × 8 per day)
        Call<ForecastResponse> call = apiService.get5DayForecast(
                cityName,
                Constants.API_KEY,
                Constants.UNITS,
                40
        );

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call,
                                   Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(NetworkResult.success(response.body()));
                } else {
                    liveData.postValue(NetworkResult.error(
                            getErrorMessage(response.code())));
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                liveData.postValue(NetworkResult.error(
                        "Network Error: " + t.getMessage()));
            }
        });
    }

    // ─── Error Message Helper ─────────────────────────────────────────────────

    private String getErrorMessage(int code) {
        switch (code) {
            case 401: return "Invalid API Key! Constants.java mein apna key daalo.";
            case 404: return "City not found! Sahi city naam daalo.";
            case 429: return "API limit exceed ho gayi! Thodi der baad try karo.";
            case 500: return "Server error! Baad mein try karo.";
            default:  return "Error: HTTP " + code;
        }
    }
}
