package com.example.myapplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.lang.Thread;
import android.location.LocationManager;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS = 0;
    private static final String TAG = "WEATHER";

    protected ImageButton SettingsButton, RefreshButton;
    protected TextView tvPlace, tvPlaces;
    protected AlertDialog.Builder ad;
    protected Context context;
    protected Location loc = null;
    protected double latitude, longitude;//for changed place
    protected CityPreference cityPreference;//at begin for default settings
    protected Handler handler;
    protected ImageView iconToday;
    protected TextView tempToday, temp1, temp2, temp3, temp4, temp5, temp6;
    protected TextView today, day1, day2, day3, day4, day5, day6;
    protected ImageView icon1, icon2, icon3, icon4, icon5, icon6;
    protected String tempStyle = "metric";//по умолчанию Цельсий
    protected WeatherAPI.ApiInterface api;
    protected int iconType;
    protected long sunrise;
    protected long sunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///*--**--**--*
        tempToday = findViewById(R.id.textView1);
        temp1 = findViewById(R.id.textView7);
        temp2 = findViewById(R.id.textView10);
        temp3 = findViewById(R.id.textView13);
        temp4 = findViewById(R.id.textView16);
        temp5 = findViewById(R.id.textView19);
        temp6 = findViewById(R.id.textView22);
        iconToday = findViewById(R.id.imageView2);
        day1 = findViewById(R.id.textView5);
        day2 = findViewById(R.id.textView8);
        day3 = findViewById(R.id.textView11);
        day4 = findViewById(R.id.textView14);
        day5 = findViewById(R.id.textView17);
        day6 = findViewById(R.id.textView20);
        icon1 = findViewById(R.id.iVIcon1);
        icon2 = findViewById(R.id.iVIcon2);
        icon3 = findViewById(R.id.iVIcon3);
        icon4 = findViewById(R.id.iVIcon4);
        icon5 = findViewById(R.id.iVIcon5);
        icon6 = findViewById(R.id.iVIcon6);

        //временно
        day1.setText("+1");
        day2.setText("+2");
        day3.setText("+3");
        day4.setText("+4");
        day5.setText("+5");
        day6.setText("+6");

        tvPlace = findViewById(R.id.tvPlace);
        tvPlaces = findViewById(R.id.tvPlaces);
        SettingsButton = findViewById(R.id.imageButton2);
        RefreshButton = findViewById(R.id.imageButton);
        cityPreference = new CityPreference(this);
        tvPlace.setText(cityPreference.getCity());

        api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);

        LocationManager lm = (LocationManager) this.getSystemService(context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission has already been granted
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            if (loc != null) {//смог определить геолокацию - возьмем новые данные
                getLocation();
            } else {//не смог получить gps данные - используем старые
                latitude = Double.valueOf(cityPreference.getLatitude());
                longitude = Double.valueOf(cityPreference.getLongitude());
                getLocation();
            }
        } else {
            requestMultiplePermissions();
        }

        //работа с AlertDialog при выборе метрики температуры
        handler = new Handler();
        context = MainActivity.this;
        String title = "Settings";
        String message = "Please, choose:";
        String button1String = "Celsius degrees(℃)";
        String button2String = "Fahrenheit degrees(°F)";
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - C
                tempStyle = "metric";
                Toast.makeText(context, "You have chosen: Celsius degrees(°C)",
                        Toast.LENGTH_LONG).show();
                updateWeatherData(tvPlace.getText().toString());
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - F
                tempStyle = "imperial";
                Toast.makeText(context, "You have chosen: Fahrenheit degrees(°F)",
                        Toast.LENGTH_LONG).show();
                updateWeatherData(tvPlace.getText().toString());
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "You haven't chosen anything...",
                        Toast.LENGTH_LONG).show();
            }
        });

        SettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });
        RefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWeatherData(tvPlace.getText().toString());


                getWeather(day1, icon1, temp1);
            }
        });
        tvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment yoursFragment = new Fragment();
                //FragmentTransaction trans = getFragmentManager().beginTransaction();
                //trans.add(R.id.fragment, yoursFragment);
                //trans.commit();

            }
        });
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getLocation();
            cityPreference.setLatitude(Double.toString(latitude));
            cityPreference.setLongitude(Double.toString(longitude));
            cityPreference.setCity(tvPlace.getText().toString());
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
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                tvPlace.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    tvPlace.setText(addresses.get(0).getLocality()); //+ ", " + addresses.get(0).getCountryName());
                    cityPreference.setCity(addresses.get(0).getLocality());// + ", " + addresses.get(0).getCountryName());//+", " + addresses.get(0).getAdminArea());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQUEST_ACCESS
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
        } else {
            // permission denied
            requestMultiplePermissions();
        }
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject json = FetchWeather.getJSON(getApplicationContext(), latitude, longitude, tempStyle);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {//РИСУНОК УЧИТЫВАЙ!
        try {
            String localTempStyle;
            if (tempStyle.equals("metric"))
                localTempStyle = " ℃";
            else localTempStyle = " °F";

            tvPlace.setText(json.getString("name").toUpperCase(Locale.getDefault()) + ", " + json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            iconType = Integer.parseInt(details.getString("icon"));
            JSONObject detailsTodaySunTime = json.getJSONObject("sys");
            sunrise = Long.parseLong(String.format("%d", detailsTodaySunTime.getInt("sunrise")));
            sunset = Long.parseLong(String.format("%d", detailsTodaySunTime.getInt("sunset")));

            String TodayTemp = String.format("%.2f", main.getDouble("temp")) + localTempStyle + "\n";

            String TodayBigString = details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa";

            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm", new Locale("US"));
            Date currentTime = Calendar.getInstance().getTime();
            String InfoStr = "Last update: " + format.format(currentTime);

            JSONObject detailsTodayWind = json.getJSONObject("wind");
            String TodayWind = "Wind: " + String.format("%d", detailsTodayWind.getInt("speed")) + "\n";//Wind

            String Today = "Today temperature is: " + TodayTemp + TodayWind + TodayBigString + "\n" + InfoStr;
            (tempToday).setText(Today);

//            JSONObject detailsTodayDescrip = json.getJSONArray("weather").getJSONObject(0);
//            String Todaydescrip = detailsTodayDescrip.getString("description")  + ", ";
//            JSONObject detailsTodayIcon = json.getJSONArray("weather").getJSONObject(0);
//            String TodayIconType = detailsTodayIcon.getString("icon")  + ", ";

            //(temp1).setText(TodayTemp);


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    public void getWeather(final TextView tvData, final ImageView ivIcon, final TextView tvTemp) {
        Log.d(TAG, "OK");

        // get weather for today
        Call<WeatherDay> callToday = api.getToday(latitude, longitude, tempStyle, getString(R.string.open_weather_maps_app_id));
        callToday.enqueue(new Callback<WeatherDay>() {
            @Override
            public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {
                Log.e(TAG, "onResponse");
                WeatherDay data = response.body();
                Log.d(TAG,response.body().toString());

                if (response.isSuccessful()) {
                    tvTemp.setText(data.getTempInteger());
                    tvData.setText(data.getDate().toString());
                    setWeatherIcon(iconType, sunrise, sunset, ivIcon);
                }
            }

            @Override
            public void onFailure(Call<WeatherDay> call, Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });

        // get weather forecast
//        Call<WeatherWeekForecast> callForecast = api.getForecast(latitude, longitude, tempStyle, context.getString(R.string.open_weather_maps_app_id));
//        callForecast.enqueue(new Callback<WeatherWeekForecast>() {
//            @Override
//            public void onResponse(Call<WeatherWeekForecast> call, Response<WeatherWeekForecast> response) {
//                Log.e(TAG, "onResponse");
//                WeatherWeekForecast data = response.body();
//
//                if (response.isSuccessful()) {
//                    SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("E");
//
//                    for (WeatherDay day : data.getItems()) {
//                        if (day.getDate().get(Calendar.HOUR_OF_DAY) == 15) {
//                            String date = String.format("%d.%d.%d %d:%d",
//                                    day.getDate().get(Calendar.DAY_OF_MONTH),
//                                    day.getDate().get(Calendar.WEEK_OF_MONTH),
//                                    day.getDate().get(Calendar.YEAR),
//                                    day.getDate().get(Calendar.HOUR_OF_DAY),
//                                    day.getDate().get(Calendar.MINUTE)
//                            );
//                            Log.d(TAG, date);
//                            Log.d(TAG, day.getTempInteger());
//                            Log.d(TAG, "---");
//
//                            // show day of week
//                            String dayOfWeek = formatDayOfWeek.format(day.getDate().getTime());
//                            tvData.setText(dayOfWeek);
//                            setWeatherIcon(iconType, sunrise, sunset, ivIcon);
//                            tvTemp.setText(day.getTempWithDegree());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WeatherWeekForecast> call, Throwable t) {
//                Log.e(TAG, "onFailure");
//                Log.e(TAG, t.toString());
//            }
//        });
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset, ImageView iv) {
        int id = actualId / 100;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                iv.setImageResource(R.drawable.weather_sunny);
            } else {
                iv.setImageResource(R.drawable.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    iv.setImageResource(R.drawable.weather_thunder);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.weather_drizzle);
                    break;
                case 7:
                    iv.setImageResource(R.drawable.weather_foggy);
                    break;
                case 8:
                    iv.setImageResource(R.drawable.weather_cloudy);
                    break;
                case 6:
                    iv.setImageResource(R.drawable.weather_snowy);
                    break;
                case 5:
                    iv.setImageResource(R.drawable.weather_rainy);
                    break;
            }
        }
    }
}
//    private String getTodayDateInStringFormat(){
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("E, d MMMM", Locale.getDefault());
//        return df.format(c.getTime());
//    }
//    private String convertTimeToDay(String time){
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSSS", Locale.getDefault());
//        String days = "";
//        try {
//            Date date = format.parse(time);
//            System.out.println("Our time " + date);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            days = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
//            System.out.println("Our time " + days);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return days;
//    }
