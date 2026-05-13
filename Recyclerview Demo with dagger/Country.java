package com.example.demoapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Country {
    @SerializedName("name")
    private Name name;
    @SerializedName("cca3")
    private String code;
    @SerializedName("capital")
    private List<String> capitals;
    @SerializedName("region")
    private String region;
    @SerializedName("population")
    private long population;
    @SerializedName("flags")
    private Flags flags;
    @SerializedName("currencies")
    private Map<String, Currency> currencies;
    @SerializedName("languages")
    private Map<String, String> languages;

    // Safe Getters
    public String getCommonName() {
        return name != null ? name.getCommon() : "Unknown";
    }

    public String getCapital() {
        return (capitals != null && !capitals.isEmpty()) ? capitals.get(0) : "N/A";
    }

    public String getRegion() {
        return region != null ? region : "Unknown";
    }

    public long getPopulation() {
        return population;
    }

    public String getFlagUrl() {
        return flags != null ? flags.getPng() : "";
    }

    public String getCurrencyInfo() {
        if (currencies != null && !currencies.isEmpty()) {
            Currency first = new ArrayList<>(currencies.values()).get(0);
            return first.getName() + " (" + (first.getSymbol() != null ? first.getSymbol() : "") + ")";
        }
        return "N/A";
    }

    public String getLanguageInfo() {
        if (languages != null && !languages.isEmpty()) {
            return new ArrayList<>(languages.values()).get(0);
        }
        return "N/A";
    }

    // Nested Classes
    public static class Name {
        @SerializedName("common")
        private String common;
        @SerializedName("official")
        private String official;

        public String getCommon() {
            return common;
        }

        public String getOfficial() {
            return official;
        }
    }

    public static class Flags {
        @SerializedName("png")
        private String png;
        @SerializedName("svg")
        private String svg;

        public String getPng() {
            return png;
        }
    }

    public static class Currency {
        @SerializedName("name")
        private String name;
        @SerializedName("symbol")
        private String symbol;

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
