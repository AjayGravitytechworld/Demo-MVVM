package com.example.demoapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Current Weather API ka complete response model
// JSON fields ko Java class mein map karta hai
public class WeatherResponse {

    @SerializedName("name")
    private String cityName;

    @SerializedName("dt")
    private long timestamp;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("coord")
    private Coord coord;

    // ─── Getters ───────────────────────────
    public String getCityName() { return cityName; }
    public long getTimestamp() { return timestamp; }
    public List<Weather> getWeatherList() { return weatherList; }
    public Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public Sys getSys() { return sys; }
    public int getVisibility() { return visibility; }
    public Coord getCoord() { return coord; }

    // Weather description & icon
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

    public String getWeatherMain() {
        if (weatherList != null && !weatherList.isEmpty()) {
            return weatherList.get(0).getMain();
        }
        return "Clear";
    }

    // ─── Inner Classes ─────────────────────

    public static class Weather {
        @SerializedName("id")
        private int id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }

        // Icon URL generate karo
        public String getIconUrl() {
            return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }

        // Formatted temperature string
        public String getTempFormatted() {
            return (int) temp + "°C";
        }

        public String getFeelsLikeFormatted() {
            return "Feels like " + (int) feelsLike + "°C";
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        @SerializedName("deg")
        private int deg;

        @SerializedName("gust")
        private double gust;

        public double getSpeed() { return speed; }
        public int getDeg() { return deg; }
        public double getGust() { return gust; }

        public String getSpeedFormatted() {
            return speed + " m/s";
        }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() { return all; }
    }

    public static class Sys {
        @SerializedName("country")
        private String country;

        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }

    public static class Coord {
        @SerializedName("lon")
        private double lon;

        @SerializedName("lat")
        private double lat;

        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }
}
