package com.example.demoapp;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.demoapp.R;
import com.example.demoapp.ForecastAdapter;
import com.example.demoapp.databinding.ActivityMainBinding;
import com.example.demoapp.WeatherResponse;
import com.example.demoapp.NetworkResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * MainActivity - View Layer (MVVM ka V)
 * - ViewModel ke LiveData observe karta hai
 * - UI update karta hai
 * - User input handle karta hai
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WeatherViewModel viewModel;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupRecyclerView();
        setupSearchBar();
        setupSwipeRefresh();
        observeLiveData();
    }

    // ─── Setup Methods ────────────────────────────────────────────────────────

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }

    private void setupRecyclerView() {
        forecastAdapter = new ForecastAdapter();
        binding.rvForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvForecast.setAdapter(forecastAdapter);
    }

    private void setupSearchBar() {
        // Search button click
        binding.btnSearch.setOnClickListener(v -> performSearch());

        // Keyboard par "Done" press karo
        binding.etCitySearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshData();
        });
    }

    // ─── LiveData Observers ───────────────────────────────────────────────────

    private void observeLiveData() {
        // Current Weather Observer
        viewModel.currentWeather.observe(this, result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    showCurrentWeatherLoading(true);
                    break;

                case SUCCESS:
                    showCurrentWeatherLoading(false);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    if (result.getData() != null) {
                        updateCurrentWeatherUI(result.getData());
                    }
                    break;

                case ERROR:
                    showCurrentWeatherLoading(false);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });

        // Forecast Observer
        viewModel.forecast.observe(this, result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    binding.progressForecast.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressForecast.setVisibility(View.GONE);
                    if (result.getData() != null && result.getData().getForecastList() != null) {
                        forecastAdapter.setForecastList(result.getData().getForecastList());
                        binding.tvForecastTitle.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    binding.progressForecast.setVisibility(View.GONE);
                    // Forecast error silently handle karo (current weather already show hai)
                    break;
            }
        });
    }

    // ─── UI Update ────────────────────────────────────────────────────────────

    private void updateCurrentWeatherUI(WeatherResponse weather) {
        // Show weather card
        binding.cardCurrentWeather.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);

        // City name + Country
        binding.tvCityName.setText(weather.getCityName() + (weather.getSys() != null ? ", " + weather.getSys().getCountry() : ""));

        // Current date/time
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy | hh:mm a", Locale.ENGLISH);
        binding.tvDateTime.setText(sdf.format(new Date()));

        // Temperature
        if (weather.getMain() != null) {
            binding.tvTemperature.setText(weather.getMain().getTempFormatted());
            binding.tvFeelsLike.setText(weather.getMain().getFeelsLikeFormatted());
            binding.tvTempMinMax.setText("↓ " + (int) weather.getMain().getTempMin() + "°  " + "↑ " + (int) weather.getMain().getTempMax() + "°");
            binding.tvHumidity.setText("💦 Humidity: " + weather.getMain().getHumidity() + "%");
            binding.tvPressure.setText("⬆ Pressure: " + weather.getMain().getPressure() + " hPa");
        }

        // Weather description
        binding.tvWeatherDescription.setText(capitalize(weather.getWeatherDescription()));

        // Wind
        if (weather.getWind() != null) {
            binding.tvWind.setText("💨 Wind: " + weather.getWind().getSpeedFormatted());
        }

        // Visibility
        binding.tvVisibility.setText("👁 Visibility: " + (weather.getVisibility() / 1000) + " km");

        // Clouds
        if (weather.getClouds() != null) {
            binding.tvClouds.setText("☁ Clouds: " + weather.getClouds().getAll() + "%");
        }

        // Weather icon (Glide)
        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getWeatherIcon() + "@4x.png";
        Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon);

        // Background color based on weather condition
        updateBackground(weather.getWeatherMain());
    }

    private void updateBackground(String weatherMain) {
        int bgColor;
        if (weatherMain == null) return;

        switch (weatherMain.toLowerCase()) {
            case "clear":
                bgColor = getResources().getColor(R.color.bg_clear, null);
                break;
            case "clouds":
                bgColor = getResources().getColor(R.color.bg_cloudy, null);
                break;
            case "rain":
            case "drizzle":
                bgColor = getResources().getColor(R.color.bg_rain, null);
                break;
            case "thunderstorm":
                bgColor = getResources().getColor(R.color.bg_thunder, null);
                break;
            case "snow":
                bgColor = getResources().getColor(R.color.bg_snow, null);
                break;
            default:
                bgColor = getResources().getColor(R.color.bg_default, null);
        }
        binding.mainContainer.setBackgroundColor(bgColor);
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    private void performSearch() {
        String city = binding.etCitySearch.getText().toString().trim();
        if (city.isEmpty()) {
            Toast.makeText(this, "Pehle city ka naam daalo!", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.fetchWeatherData(city);
        hideKeyboard();
        binding.etCitySearch.clearFocus();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.etCitySearch.getWindowToken(), 0);
        }
    }

    // ─── Loading State ────────────────────────────────────────────────────────

    private void showCurrentWeatherLoading(boolean isLoading) {
        binding.progressCurrentWeather.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            binding.cardCurrentWeather.setVisibility(View.GONE);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
