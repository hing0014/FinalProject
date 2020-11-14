package com.example.finalProject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalProject.R;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TicketMaster extends AppCompatActivity {
    private ProgressBar theBar;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        theBar = findViewById(R.id.loadBar);
        theBar.setVisibility(View.VISIBLE);
        ForecastQuery forecast = new ForecastQuery();
        forecast.execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=9xSSOAi25vaqiTP1UGfMa1fxycNnJPpd&city=Ottawa&radius=100");
    }
    private class ForecastQuery extends AsyncTask<String, Integer, String>
    {
        //Type 1
        @Override
        protected String doInBackground(String... debates)
        {
            try
            {
                URL url = new URL(debates[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();
                JSONObject jObject = new JSONObject(result);
                UV = jObject.getDouble("value");
                publishProgress(80);
                Log.i("Found", "UV index" + UV);
            }
            catch (Exception ignored)
            {

            }
            publishProgress(100);
            return "Done";
        }

        //Type 2
        public void onProgressUpdate(Integer...value)
        {
            theBar.setProgress(value[0]);
            Log.i("Progress", "Progress is :" + value[0] + "%");
        }
        //Type3
        @SuppressLint("SetTextI18n")
        public void onPostExecute(String fromDoInBackground)
        {
            TextView currentTemperatureET = findViewById(R.id.current_temperature);
            TextView minET = findViewById(R.id.min_temperature);
            TextView maxET = findViewById(R.id.max_temperature);
            TextView UVET = findViewById(R.id.UV_Rating);
            ImageView weatherImage = findViewById(R.id.weatherImage);
            ProgressBar weatherBar = findViewById(R.id.indeterminateBar);

            currentTemperatureET.setText("Current Temp: "+currentTemperature);
            minET.setText("Minimum Temp: "+min);
            maxET.setText("Maximum Temp: "+max);
            UVET.setText("UV Index: "+UV);
            weatherImage.setImageBitmap(image);
            weatherBar.setVisibility(View.INVISIBLE);
            Log.i("HTTP", fromDoInBackground);
        }
    }
}