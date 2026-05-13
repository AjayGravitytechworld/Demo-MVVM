package com.example.demoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demoapp.R;
import com.example.demoapp.ForecastResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter - Forecast list ke liye
 * 5-day forecast items ko RecyclerView mein dikhata hai
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastResponse.ForecastItem> forecastList = new ArrayList<>();

    // Naya data set karo — RecyclerView automatically update hoga
    public void setForecastList(List<ForecastResponse.ForecastItem> list) {
        this.forecastList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = forecastList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────────

    static class ForecastViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDate;
        private final TextView tvTime;
        private final TextView tvTemp;
        private final TextView tvDescription;
        private final TextView tvWind;
        private final TextView tvHumidity;
        private final TextView tvPrecip;
        private final ImageView ivWeatherIcon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate        = itemView.findViewById(R.id.tvForecastDate);
            tvTime        = itemView.findViewById(R.id.tvForecastTime);
            tvTemp        = itemView.findViewById(R.id.tvForecastTemp);
            tvDescription = itemView.findViewById(R.id.tvForecastDesc);
            tvWind        = itemView.findViewById(R.id.tvForecastWind);
            tvHumidity    = itemView.findViewById(R.id.tvForecastHumidity);
            tvPrecip      = itemView.findViewById(R.id.tvForecastPrecip);
            ivWeatherIcon = itemView.findViewById(R.id.ivForecastIcon);
        }

        public void bind(ForecastResponse.ForecastItem item) {
            tvDate.setText(item.getDateFormatted());
            tvTime.setText(item.getTimeFormatted());
            tvTemp.setText(item.getTempFormatted());
            tvDescription.setText(capitalize(item.getWeatherDescription()));
            tvPrecip.setText("💧 " + item.getPrecipFormatted());

            if (item.getMain() != null) {
                tvHumidity.setText("💦 " + item.getMain().getHumidity() + "%");
            }

            if (item.getWind() != null) {
                tvWind.setText("💨 " + item.getWind().getSpeedFormatted());
            }

            // Glide se weather icon load karo
            Glide.with(itemView.getContext())
                    .load(item.getIconUrl())
                    .into(ivWeatherIcon);
        }

        private String capitalize(String text) {
            if (text == null || text.isEmpty()) return text;
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
    }
}
