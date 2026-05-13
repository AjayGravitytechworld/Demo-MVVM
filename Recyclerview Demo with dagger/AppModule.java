package com.example.demoapp;

import dagger.Module;
import dagger.Provides;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    @Provides
    @javax.inject.Singleton
    public Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson) {
        return new Retrofit.Builder().baseUrl("https://restcountries.com/").addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    @Provides
    @Singleton
    public CountriesApi provideCountriesApi(Retrofit retrofit) {
        return retrofit.create(CountriesApi.class);
    }

    @Provides
    @Singleton
    public CountryRepository provideCountryRepository(CountriesApi api) {
        return new CountryRepository(api);
    }
}
