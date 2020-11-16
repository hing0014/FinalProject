package com.example.finalProject;

import android.annotation.SuppressLint;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TicketMaster extends AppCompatActivity
{
    private ArrayList<TicketEvent> events;
    private TicketMasterListAdapter myAdapter;
    private ProgressBar theBar;
    String city;
    String radius;
    SQLiteDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        theBar = findViewById(R.id.loadBar);
        theBar.setVisibility(View.VISIBLE);

        ListView myList = findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new TicketMasterListAdapter());

        Button searchButton = findViewById(R.id.searchButton);
        TicketMasterQuery tickQuer = new TicketMasterQuery();
        searchButton.setOnClickListener(click ->
        {
            EditText cityText = findViewById(R.id.citySearch);
            city = cityText.toString();
            EditText radiusText = findViewById(R.id.radius);
            radius = radiusText.toString();
            boolean isInt = true;
            try {
                int num = Integer.parseInt(radius);
            } catch (NumberFormatException e) {
                isInt = false;
            }
            if(isInt)
            {
                if(URLUtil.isValidUrl("https://app.ticketmaster.com/discovery/v2/events.json?apikey=9xSSOAi25vaqiTP1UGfMa1fxycNnJPpd&city=" + city + "&radius=" + radius))
                {
                    tickQuer.execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=9xSSOAi25vaqiTP1UGfMa1fxycNnJPpd&city=" + city + "&radius=" + radius, city);
                }
                else Toast.makeText(getApplicationContext(),"INVALID CITY: city not found", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(),"INVALID RADIUS: please enter a whole number", Toast.LENGTH_SHORT).show();
            myAdapter.notifyDataSetChanged();
        });
    }
    private class TicketMasterQuery extends AsyncTask<String, Integer, String>
    {
        String city;

        //Type 1
        @Override
        protected String doInBackground(String... debates)
        {
            city = debates[1];
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
                JSONObject jObjEmbed = jObject.getJSONObject("_embedded");
                JSONArray jsonEventArray = jObjEmbed.getJSONArray("events");
                int eventArrayLength = jsonEventArray.length();
                for(int i = 0; i < eventArrayLength; i++)
                {
                    JSONObject jsonEvent = jsonEventArray.getJSONObject(i);
                    String eventName= jsonEvent.getString("name");

                    JSONObject jsonEventDates = jsonEvent.getJSONObject("dates");
                    JSONObject jsonEventStart = jsonEventDates.getJSONObject("start");
                    String startDate = jsonEventStart.getString("localDate");

                    JSONObject jsonEventPriceRanges = jsonEvent.getJSONObject("priceRanges");
                    double ticketPriceMin = jsonEventPriceRanges.getDouble("min");
                    double ticketPriceMax = jsonEventPriceRanges.getDouble("max");

                    String eventUrl = jsonEvent.getString("url");

                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(TicketMasterOpener.COL_CITY, city);
                    newRowValues.put(TicketMasterOpener.COL_EVENT_NAME, eventName);
                    newRowValues.put(TicketMasterOpener.COL_START_DATE, startDate);
                    newRowValues.put(TicketMasterOpener.COL_MIN_PRICE, ticketPriceMin);
                    newRowValues.put(TicketMasterOpener.COL_MAX_PRICE, ticketPriceMax);
                    newRowValues.put(TicketMasterOpener.COL_URL, eventUrl);

                    long newId = dataBase.insert(TicketMasterOpener.TABLE_NAME, null, newRowValues);

                    events.add(new TicketEvent(city, eventName, startDate, ticketPriceMin, ticketPriceMax, eventUrl, newId));
                    Log.i("Event Created", "Event name: " + eventName);
                    publishProgress(i/eventArrayLength*100);
                }
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

        }
    }

    private class TicketMasterListAdapter extends BaseAdapter
    {

        public int getCount() { return events.size();}

        public Object getItem(int position){return position;}

        public long getItemId(int position) { return position; }

        public View getView(int position, View view, ViewGroup parent)
        {
            TicketEvent arEl = events.get(position);
            LayoutInflater inflater = getLayoutInflater();
            if(view == null) view = inflater.inflate(R.layout.row_ticket_master_event, parent, false);
            TextView messageText = view.findViewById(R.id.eventRowName);
            messageText.setText(arEl.getEventName());

            return view;
        }
    }

    private void loadDataFromDatabase()
    {
        //get a database connection:
        TicketMasterOpener dbOpener = new TicketMasterOpener(this);
        dataBase = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer
        // We want to get all of the columns. Look at MyOpener.java for the definitions:

        String [] columns = {
                TicketMasterOpener.COL_ID,
                TicketMasterOpener.COL_CITY,
                TicketMasterOpener.COL_EVENT_NAME,
                TicketMasterOpener.COL_START_DATE,
                TicketMasterOpener.COL_MIN_PRICE,
                TicketMasterOpener.COL_MAX_PRICE,
                TicketMasterOpener.COL_URL};
        //query all the results from the database:
        Cursor results = dataBase.query(false, TicketMasterOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        //Now the results object has rows of results that match the query.
        //find the column indices:
        int cityColumnIndex = results.getColumnIndex(TicketMasterOpener.COL_CITY);
        int eventNameColIndex = results.getColumnIndex(TicketMasterOpener.COL_EVENT_NAME);
        int startDateColIndex = results.getColumnIndex(TicketMasterOpener.COL_START_DATE);
        int minPriceColIndex = results.getColumnIndex(TicketMasterOpener.COL_MIN_PRICE);
        int maxPriceColIndex = results.getColumnIndex(TicketMasterOpener.COL_MAX_PRICE);
        int urlColIndex = results.getColumnIndex(TicketMasterOpener.COL_URL);
        int idColIndex = results.getColumnIndex(TicketMasterOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String city = results.getString(cityColumnIndex);
            String eventName = results.getString(eventNameColIndex);
            String startDate = results.getString(startDateColIndex);
            double minPrice = Double.parseDouble(results.getString(minPriceColIndex));
            double maxPrice = Double.parseDouble(results.getString(maxPriceColIndex));
            String url = results.getString(urlColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            events.add(new TicketEvent(city, eventName, startDate, minPrice, maxPrice, url, id));
        }
    }

    private static class TicketEvent
    {
        String city;
        String eventName;
        String startDate;
        double ticketPriceMin;
        double ticketPriceMax;
        String url;
        long index;

        private TicketEvent(String city, String eventName, String startDate, double ticketPriceMin, double ticketPriceMax, String url, long index)
        {
            this.city = city;
            this.eventName = eventName;
            this.startDate = startDate;
            this.ticketPriceMin = ticketPriceMin;
            this.ticketPriceMax = ticketPriceMax;
            this.url = url;
            this.index = index;

        }

        public String getCity()
        {
            return city;
        }
        public String getEventName()
        {
            return eventName;
        }
        public String getStartDate(){ return startDate; }
        public double getTicketPriceMin(){ return ticketPriceMin; }
        public double getTicketPriceMax(){ return ticketPriceMax; }
        public String getUrl(){ return url; }
        public long getId() { return index; }
    }
}
