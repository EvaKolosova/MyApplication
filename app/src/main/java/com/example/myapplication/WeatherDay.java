package com.example.myapplication;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.text.*;

import java.util.Calendar;
import java.util.List;

public class WeatherDay {
    public class Cordinates {
        long latitude;
        long longitude;
    }

    public class WeatherMain {
        Double temp;
        int pressure;
        int humidity;
    }

    public class WeatherDescription {
        String description;
        int id;
    }

    public class WeatherWind {
        int speed;
    }

    public class WeatherSys {
        long sunrise;
        long sunset;
    }

    @SerializedName("coord")
    private List<Cordinates> cordDescription;

    @SerializedName("main")
    private List<WeatherMain> mainDescription;

    @SerializedName("weather")
    private List<WeatherDescription> weatherDescription;

    @SerializedName("sys")
    private List<WeatherSys> sysDescription;

    @SerializedName("wind")
    private int speed;

    @SerializedName("dt")
    private long timestamp;

    public WeatherDay(List<Cordinates> cordDescription, List<WeatherDescription> weatherDescription,  List<WeatherMain> mainDescription, WeatherWind speed, List<WeatherSys> sysDescription) { // WeatherMain temp, ,
        this.cordDescription = cordDescription;
        this.mainDescription = mainDescription;
        this.weatherDescription = weatherDescription;
        this.sysDescription = sysDescription;
    }

//    public WeatherDay(long myLatitude, long myLongitude, String tempStyle, long key) {
//        setLongitude(myLongitude);
//        setLatitude(myLatitude);
//    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp * 1000);
        return date;
    }

    public void setLongitude(long lon){
        cordDescription.get(0).longitude = lon;
    }

    public void setLatitude(long lat){
        cordDescription.get(0).latitude = lat;
    }

    public String getTemp() { return String.valueOf(mainDescription.get(0).temp); }

    public String getTempInteger() { return String.valueOf(getTemp()); }

    public String getTempWithDegree() { return String.valueOf(getTemp()) + "\u00B0"; }

    public int getId() { return weatherDescription.get(0).id; }

    public String getPressure() { return String.valueOf(mainDescription.get(0).pressure); }

    public String getHumidity() { return String.valueOf((mainDescription.get(0).humidity)); }

    public String getDescription() { return weatherDescription.get(0).description; }

    public String getWind() { return String.valueOf(speed); }

    public String getSunrise(){ return String.valueOf(sysDescription.get(0).sunrise); }

    public String getSunset(){ return String.valueOf(sysDescription.get(0).sunset); }
}