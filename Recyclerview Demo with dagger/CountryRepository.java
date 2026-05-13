package com.example.demoapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class CountryRepository {
    private final CountriesApi api;

    public CountryRepository(CountriesApi api) {
        this.api = api;
    }

    public interface Callback {
        void onSuccess(List<Country> countries);
        void onError(String error);
    }

    public void fetchCountries(Callback callback) {
        api.getAllCountries().enqueue(new retrofit2.Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Unknown Error");
            }
        });
    }
}