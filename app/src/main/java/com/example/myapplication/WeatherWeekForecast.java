package com.example.myapplication;

import com.example.myapplication.WeatherDay;

import java.util.List;

public class WeatherWeekForecast {
    private List<WeatherDay> items;

    public WeatherWeekForecast(List<WeatherDay> items) {
        this.items = items;
    }

    public List<WeatherDay> getItems() {
        return items;
    }
}