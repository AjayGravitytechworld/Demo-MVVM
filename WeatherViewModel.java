package com.example.demoapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demoapp.ForecastResponse;
import com.example.demoapp.WeatherResponse;
import com.example.demoapp.WeatherRepository;
import com.example.demoapp.Constants;
import com.example.demoapp.NetworkResult;

/**
 * ViewModel Layer - MVVM ka heart.
 * - UI (Activity/Fragment) se alag rehta hai
 * - Screen rotate ho toh bhi data survive karta hai
 * - Repository se data maangta hai aur LiveData ke zariye UI ko deta hai
 */
public class WeatherViewModel extends ViewModel {

    private final WeatherRepository repository;

    // Current Weather LiveData
    private final MutableLiveData<NetworkResult<WeatherResponse>> _currentWeather
            = new MutableLiveData<>();
    public LiveData<NetworkResult<WeatherResponse>> currentWeather = _currentWeather;

    // Forecast LiveData
    private final MutableLiveData<NetworkResult<ForecastResponse>> _forecast
            = new MutableLiveData<>();
    public LiveData<NetworkResult<ForecastResponse>> forecast = _forecast;

    // Currently searched city name
    private final MutableLiveData<String> _currentCity = new MutableLiveData<>();
    public LiveData<String> currentCity = _currentCity;

    public WeatherViewModel() {
        repository = new WeatherRepository();
        // App open hone par default city load karo
        fetchWeatherData(Constants.DEFAULT_CITY);
    }

    /**
     * Dono API calls ek saath karo — Current Weather + Forecast
     */
    public void fetchWeatherData(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) return;

        String city = cityName.trim();
        _currentCity.setValue(city);

        // Current weather
        repository.getCurrentWeather(city, _currentWeather);

        // 5-day forecast
        repository.get5DayForecast(city, _forecast);
    }

    /**
     * Pull-to-refresh ke liye — last searched city ke saath refresh karo
     */
    public void refreshData() {
        String city = _currentCity.getValue();
        if (city != null) {
            fetchWeatherData(city);
        }
    }
}
