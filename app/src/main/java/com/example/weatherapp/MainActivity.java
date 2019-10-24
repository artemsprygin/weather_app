package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String WEATHERURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ru&APPID=%s";
    private String API_KEY = "KEY";
    private EditText editTextCity;
    private TextView textViewWeather;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
        constraintLayout = findViewById(R.id.MainConstrain);
    }

    public void showWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHERURL, city, API_KEY);
            task.execute(url);
        } else {
            Toast.makeText(this, "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder res = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    res.append(line);
                    line = bufferedReader.readLine();
                }
                return res.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nТемпература: %s\nНа улице: %s", city, temp, description);
                textViewWeather.setText(weather);
                if (description.equals("пасмурно")) {
                    constraintLayout.setBackgroundResource(R.drawable.cloudy);
                } else if (description.equals("дождь") || description.equals("легкий дождь")) {
                    constraintLayout.setBackgroundResource(R.drawable.rainy);
                } else if (description.equals("слегка облачно")) {
                    constraintLayout.setBackgroundResource(R.drawable.cloudssmall);
                } else if (description.equals("ясно")) {
                    constraintLayout.setBackgroundResource(R.drawable.sunny);
                } else if (description.equals("туман")) {
                    constraintLayout.setBackgroundResource(R.drawable.fog);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
