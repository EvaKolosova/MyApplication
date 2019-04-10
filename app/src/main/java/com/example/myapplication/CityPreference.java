package com.example.myapplication;
import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){ return prefs.getString("city", "NIZHNY NOVGOROD"); }
        //return prefs.getString("city", "Nizhny Novgorod, Nizhny Novgorod Oblast, Russia"); }

    String getLatitude(){ return prefs.getString("latitude", "56.326887"); }

    String getLongitude(){
        return prefs.getString("longitude", "44.005986");
    }

    void setCity(String city){ prefs.edit().putString("city", city).commit(); }

    void setLatitude(String latitude){ prefs.edit().putString("latitude", latitude).commit(); }

    void setLongitude(String longitude){ prefs.edit().putString("longitude", longitude).commit(); }
}