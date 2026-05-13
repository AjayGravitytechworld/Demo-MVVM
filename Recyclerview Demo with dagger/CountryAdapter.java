package com.example.demoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private List<Country> countries = new ArrayList<>();
    private final Context context;

    public CountryAdapter(Context context) {
        this.context = context;
    }

    public void setCountries(List<Country> list) {
        this.countries = list != null ? list : Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        holder.bind(countries.get(position));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCapital, tvRegion, tvPopulation, tvCurrency, tvLanguage;
        ImageView ivFlag;

        CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCapital = itemView.findViewById(R.id.tvCapital);
            tvRegion = itemView.findViewById(R.id.tvRegion);
            tvPopulation = itemView.findViewById(R.id.tvPopulation);
            tvCurrency = itemView.findViewById(R.id.tvCurrency);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            ivFlag = itemView.findViewById(R.id.ivFlag);
        }

        void bind(Country country) {
            tvName.setText(country.getCommonName());
            tvCapital.setText("🏛️ " + country.getCapital());
            tvRegion.setText("🌍 " + country.getRegion());
            tvPopulation.setText("👥 " + NumberFormat.getNumberInstance().format(country.getPopulation()));
            tvCurrency.setText("💰 " + country.getCurrencyInfo());
            tvLanguage.setText("🗣️ " + country.getLanguageInfo());

            if (!android.text.TextUtils.isEmpty(country.getFlagUrl())) {
                Glide.with(itemView.getContext()).load(country.getFlagUrl()).placeholder(android.R.color.darker_gray).error(android.R.color.holo_red_dark).into(ivFlag);
            }
        }
    }
}