package com.example.demoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    @Inject
    CountryRepository repository;
    RecyclerView recyclerView;
    CountryAdapter adapter;
    ProgressBar progressBar;
    TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App) getApplication()).getAppComponent().inject(this);

        setupUI();
        loadCountries();
    }

    private void setupUI() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        adapter = new CountryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadCountries() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        repository.fetchCountries(new CountryRepository.Callback() {
            @Override
            public void onSuccess(List<Country> countries) {
                progressBar.setVisibility(View.GONE);
                adapter.setCountries(countries);
            }

            @Override
            public void onError(String error) {
                Log.e("Errors", error);
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("⚠️ " + error);
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
