package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.Locale;
import java.io.IOException;
import java.util.List;
import java.lang.Thread;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS = 0;
    boolean flagAccess = false;//no access
    ImageButton SettingsButton, RefreshButton;
    TextView tvOut;
    TextView tvPlace;
    AlertDialog.Builder ad;
    Context context;
    Location loc = null;
    String locationForEPI;
    double latitude, longitude;//for changed place
    double last_lat, last_lng;//for data at begins
    Handler handler;
    TextView tempToday, temp1, temp2, temp3, temp4, temp5, temp6;
    String tempStyle = "metric";//по умолчанию Цельсий

//    public String getAddress(double lats, double lons) {
//        Geocoder geocoder;
//        double lat = lats;
//        double lon = lons;
//        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(lat, lon, 1);
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//        if (addresses != null) {
//
//            String address = addresses.get(0).getAddressLine(0);
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();
//
//            return address;
//        } else {
//            return "failed";
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempToday = findViewById(R.id.textView1);
        temp1 = findViewById(R.id.textView7);
        temp2 = findViewById(R.id.textView10);
        temp3 = findViewById(R.id.textView13);
        temp4 = findViewById(R.id.textView16);
        temp5 = findViewById(R.id.textView19);
        temp6 = findViewById(R.id.textView22);
        tvOut = findViewById(R.id.textView);
        tvPlace = findViewById(R.id.textViewPlace);
        SettingsButton = findViewById(R.id.imageButton2);
        RefreshButton = findViewById(R.id.imageButton);
        //last_lat = location.getLatitude();
        //last_lng = location.getLongitude();

        //CityPreference cityPreference = new CityPreference(?);
        //tvPlace.setText(cityPreference.getCity());

        LocationManager lm = (LocationManager)this. getSystemService(context.LOCATION_SERVICE);
        while(true)
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Permission has already been granted
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            if(loc != null){
                getLocation();
            }
            break;
        }
        else {
                this.requestMultiplePermissions();
        }

        //работа с AlertDialog при выборе метрики температуры
        handler = new Handler();
        context = MainActivity.this;
        String title = "Settings";
        String message = "Please, choose:";
        String button1String = "Celsius degrees(℃)";
        String button2String = "Fahrenheit degrees(" + "36.6\\u00B2" + "F)";
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - C
                tempStyle = "metric";
                Toast.makeText(context, "You have chosen Celsius degrees(C)",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - F
                tempStyle = "imperial";
                Toast.makeText(context, "You have chosen Fahrenheit degrees(F)",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "You haven't chosen anything...",
                        Toast.LENGTH_LONG).show();
            }
        });

        View.OnClickListener oclBtnSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        };

        SettingsButton.setOnClickListener(oclBtnSettings);
        RefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWeatherData(tvPlace.getText().toString());
            }
        });
    }

    private final LocationListener locationListener = new LocationListener()  {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void getLocation() {
        //String Location = getAddress(loc.getLatitude(),loc.getLongitude());
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
            if (addresses.isEmpty()) {
                tvPlace.setText("Waiting for Location");
            }
            else {
                if (addresses.size() > 0) {
                    tvPlace.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                    locationForEPI = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },REQUEST_ACCESS
                );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
        } else {
            // permission denied
        }
    }

    private void updateWeatherData(final String city){
        new Thread(){
            @Override
            public void run(){
                final JSONObject json = FetchWeather.getJSON(getApplicationContext(), city, tempStyle);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        @Override
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){//РИСУНОК УЧИТЫВАЙ
        try {
            String localTempStyle;
            if(tempStyle == "metric")
                localTempStyle = " ℃";
            else localTempStyle = " 36.6\\\\u00B2\" + \"F";

            tvPlace.setText(json.getString("name").toUpperCase(Locale.getDefault()) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            (temp1).setText(
                    String.format("%.2f", main.getDouble("temp")) + localTempStyle);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

}