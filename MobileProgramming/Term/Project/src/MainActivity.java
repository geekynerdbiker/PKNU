package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private Button goBack;
    String City = "Buasn", Country = "KR";
    EditText CityEditText, CountryEditText;
    private Button setParseValues;
    DecimalFormat dFormatter = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CityEditText = (EditText) findViewById(R.id.CityEditText);
        CountryEditText = (EditText) findViewById(R.id.CountryEditText);
        setParseValues = (Button) findViewById(R.id.setValues);
        setParseValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City = CityEditText.getText().toString();
                Country = CountryEditText.getText().toString();
                Toast.makeText(getApplicationContext(), "설정되었습니다!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getWeather(View v) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + City + "," + Country + "&appid=034e4c1a00d9f959337a5f7b1cccd8eb";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        parseJsonAndUpdateUI(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "지역을 설정해주세요.", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(stringRequest);
    }


    private void parseJsonAndUpdateUI(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject main = obj.getJSONObject("main");
            JSONObject wind = obj.getJSONObject("wind");
            JSONObject sys = obj.getJSONObject("sys");

            double temperature = main.getDouble("temp") - 273;
            double feelsLike = main.getDouble("feels_like") - 273;
            int humidity = main.getInt("humidity");
            double windSpeed = wind.getDouble("speed");

            TextView temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
            TextView feelsText = (TextView) findViewById(R.id.feelsText);
            TextView windTextView = (TextView) findViewById(R.id.windTextView);
            TextView humidityTextView = (TextView) findViewById(R.id.humidityTextView);
            TextView CountryText = (TextView) findViewById(R.id.CountryText);


            temperatureTextView.setText("" + dFormatter.format(temperature) + " °C");
            feelsText.setText("" + dFormatter.format(feelsLike) + " °C");
            windTextView.setText("" + dFormatter.format(windSpeed) + " ms⁻¹");
            humidityTextView.setText("" + dFormatter.format(humidity) + " %");
            LinearLayout myLayout2 = (LinearLayout) findViewById(R.id.myLayout2);

            if (temperature > 25) {
                Toast.makeText(getApplicationContext(), "오늘은 더울거에요.", Toast.LENGTH_LONG).show();
                myLayout2.setBackgroundColor(Color.parseColor("#db8677"));
            } else if (temperature < 5) {
                Toast.makeText(getApplicationContext(), "오늘은 추울거에요.", Toast.LENGTH_LONG).show();
                myLayout2.setBackgroundColor(Color.parseColor("#91b0db"));
            } else {
                Toast.makeText(getApplicationContext(), "오늘은 적당해요.", Toast.LENGTH_LONG).show();
                myLayout2.setBackgroundColor(Color.parseColor("#86db6f"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
