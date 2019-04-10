package com.example.myapplication;

public class WeatherObject {
    private String dayOfWeek;
    private String dayOfMonth;
    private int weatherIcon;
    private String weatherResult;
    private String weatherResultSmall;

    public WeatherObject(String dayOfWeek, String dayOfMonth, int weatherIcon, String weatherResult, String weatherResultSmall) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.weatherIcon = weatherIcon;
        this.weatherResult = weatherResult;
        this.weatherResultSmall = weatherResultSmall;
    }
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    public String getDayOfMonth() {
        return dayOfMonth;
    }
    public int getWeatherIcon() {
        return weatherIcon;
    }
    public String getWeatherResult() {
        return weatherResult;
    }
    public String getWeatherResultSmall() {
        return weatherResultSmall;
    }
}