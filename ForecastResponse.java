package com.example.demoapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// 5-Day Forecast API ka response model
public class ForecastResponse {

    @SerializedName("cnt")
    private int count;

    @SerializedName("list")
    private List<ForecastItem> forecastList;

    @SerializedName("city")
    private City city;

    public int getCount() { return count; }
    public List<ForecastItem> getForecastList() { return forecastList; }
    public City getCity() { return city; }

    // ─── City Info ─────────────────────────
    public static class City {
        @SerializedName("name")
        private String name;

        @SerializedName("country")
        private String country;

        @SerializedName("timezone")
        private int timezone;

        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public String getName() { return name; }
        public String getCountry() { return country; }
        public int getTimezone() { return timezone; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }

    // ─── Each Forecast Item (every 3 hours) ─
    public static class ForecastItem {
        @SerializedName("dt")
        private long timestamp;

        @SerializedName("main")
        private WeatherResponse.Main main;

        @SerializedName("weather")
        private List<WeatherResponse.Weather> weatherList;

        @SerializedName("wind")
        private WeatherResponse.Wind wind;

        @SerializedName("clouds")
        private WeatherResponse.Clouds clouds;

        @SerializedName("dt_txt")
        private String dateTimeText; // "2024-01-15 12:00:00" format

        @SerializedName("pop")
        private double precipitationProbability;

        public long getTimestamp() { return timestamp; }
        public WeatherResponse.Main getMain() { return main; }
        public List<WeatherResponse.Weather> getWeatherList() { return weatherList; }
        public WeatherResponse.Wind getWind() { return wind; }
        public WeatherResponse.Clouds getClouds() { return clouds; }
        public String getDateTimeText() { return dateTimeText; }
        public double getPrecipitationProbability() { return precipitationProbability; }

        // Helpers
        public String getWeatherDescription() {
            if (weatherList != null && !weatherList.isEmpty()) {
                return weatherList.get(0).getDescription();
            }
            return "N/A";
        }

        public String getWeatherIcon() {
            if (weatherList != null && !weatherList.isEmpty()) {
                return weatherList.get(0).getIcon();
            }
            return "01d";
        }

        public String getIconUrl() {
            return "https://openweathermap.org/img/wn/" + getWeatherIcon() + "@2x.png";
        }

        public String getTempFormatted() {
            if (main != null) {
                return (int) main.getTemp() + "°C";
            }
            return "N/A";
        }

        // Date part: "2024-01-15 12:00:00" → "Jan 15"
        public String getDateFormatted() {
            if (dateTimeText != null && dateTimeText.length() >= 10) {
                String[] parts = dateTimeText.split("-");
                if (parts.length >= 3) {
                    String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May",
                            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    int month = Integer.parseInt(parts[1]);
                    String day = parts[2].substring(0, 2);
                    return months[month] + " " + day;
                }
            }
            return dateTimeText;
        }

        // Time part: "2024-01-15 12:00:00" → "12:00"
        public String getTimeFormatted() {
            if (dateTimeText != null && dateTimeText.length() >= 16) {
                return dateTimeText.substring(11, 16);
            }
            return "";
        }

        public String getPrecipFormatted() {
            return (int) (precipitationProbability * 100) + "%";
        }
    }
}
