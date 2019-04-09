package com.example.myapplication;//класс отвечает за получение данных о погоде через API OpenWeatherMap.
import android.content.Context;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.myapplication.R;
//open an HttpURLConnection, Add the API key obtained in the first step as a header through the setRequestProperty method.
//Check if JSON data is fetched with the help of the response code. If the request worked right and data was fetched, 200 response code is returned as a success.
//Return this JsonObject data.

public class FetchWeather {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=";//=metric
    public static JSONObject getJSON(Context context, String city, String metric){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API + metric, city));

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));


            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            if (reader == null)
                System.out.println("Testing null!!!");
            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            System.out.println("Error " + e.toString());
            return null;
        }
    }
}