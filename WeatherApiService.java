package com.example.demoapp;

import com.example.demoapp.ForecastResponse;
import com.example.demoapp.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Retrofit API Interface - Sabhi endpoints yahan define hote hain
public interface WeatherApiService {

    /**
     * Current Weather by City Name
     * Example: /weather?q=Mumbai&appid=KEY&units=metric
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByCity(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    /**
     * Current Weather by Coordinates (Latitude, Longitude)
     * Example: /weather?lat=19.07&lon=72.87&appid=KEY&units=metric
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByCoords(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    /**
     * 5-Day / 3-Hour Forecast by City Name
     * Example: /forecast?q=Mumbai&appid=KEY&units=metric
     * Returns 40 data points (8 per day × 5 days)
     */
    @GET("forecast")
    Call<ForecastResponse> get5DayForecast(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("cnt") int count  // limit number of results
    );
}
