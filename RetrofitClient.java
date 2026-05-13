package com.example.demoapp;

import com.example.demoapp.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

// Singleton pattern - Retrofit ka ek hi instance poori app mein
public class RetrofitClient {

    private static RetrofitClient instance;
    private final WeatherApiService weatherApiService;

    private RetrofitClient() {
        // Logging interceptor - Logcat mein API request/response dikhega
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient with timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Retrofit instance build karo
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApiService = retrofit.create(WeatherApiService.class);
    }

    // Singleton getInstance
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public WeatherApiService getWeatherApiService() {
        return weatherApiService;
    }
}
