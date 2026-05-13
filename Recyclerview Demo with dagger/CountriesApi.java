package com.example.demoapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CountriesApi {
    @GET("v3.1/all?fields=name,capital,currencies")
    Call<List<Country>> getAllCountries();
}
