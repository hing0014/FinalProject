package com.example.finalProject;

import android.annotation.SuppressLint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
/**
 * Links between the XML and ticketmaster database.
 *  <p>
 * Course Name: CST8288_010
 * Class name: TicketMaster
 * Date: November 19, 2020
 *
 * @version 1.0
 * @author Chris HIng
 */
public class TicketMaster extends AppCompatActivity
{
    /**
     * Fields for storing the database information for use throughout the class.
     */
    private ArrayList<TicketEvent> events;
    private TicketMasterListAdapter myAdapter;
    private ProgressBar theBar;
    String city;
    String radius;
    SQLiteDatabase dataBase;

    /**
     * Creates and manages the click listeners of the button.
     * <p>
     * When the search button is clicked the editText fields stored data is passed to the TicketMasterQuery in the form of the data source URL.
     * That information is then displayed on screen.
     *
     * @param savedInstanceState Bundle object used in the super call of onCreate.
     */
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

    /**
     * Handles the link to the data and creation fo the database.
     *  <p>
     * Course Name: CST8288_010
     * Class name: TicketMasterQuery
     * Date: November 19, 2020
     *
     * @version 1.0
     * @author Chris HIng
     */
    private class TicketMasterQuery extends AsyncTask<String, Integer, String>
    {
        String city;

        /*
         * Real Red (Mar 3 '09 at 9:37). Get file name from URL [Webpage]. Retrieved from
         * https://stackoverflow.com/questions/605696/get-file-name-from-url
         */
        /**
         * Gathers the TicketMaster data and builds the database.
         * <p>
         * Connects to the passed in URL.
         * Converts websites JSON file to a string.
         * Extracts the required information out of the string and into and array of TicketEvent objects.
         *
         * @param debates An array of Strings, the first string being the TicketMaster URL, and the second being the city name.
         */
        @Override
        protected String doInBackground(String... debates)
        {
            Bitmap image = null;
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

                    String imageURLString = "";
                    JSONArray jsonImages = jsonEvent.getJSONArray("images");
                    for(int ii = 0; ii < jsonImages.length(); ii++)
                    {
                        JSONObject jasonImageDetails = jsonImages.getJSONObject(ii);

                        if(jasonImageDetails.getString("ratio").equals("4_3"))
                        {
                            imageURLString = jasonImageDetails.getString("url");
                            break;
                        }
                    }

                    if(!(imageURLString.equals("")))
                    {
                        URL imageUrlObject = new URL(imageURLString);
                        HttpURLConnection connection = (HttpURLConnection) imageUrlObject.openConnection();
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200)
                        {
                            image = BitmapFactory.decodeStream(connection.getInputStream());
                        }

                        String imageName = imageURLString.substring( imageURLString.lastIndexOf('/')+1);

                        FileOutputStream outputStream = openFileOutput( imageName, Context.MODE_PRIVATE);

                        image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        FileInputStream fis = null;
                        try {    fis = openFileInput(String.valueOf(image));   }
                        catch (FileNotFoundException e) {    e.printStackTrace();  }
                        Bitmap bm = BitmapFactory.decodeStream(fis);
                    }

                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(TicketMasterOpener.COL_CITY, city);
                    newRowValues.put(TicketMasterOpener.COL_EVENT_NAME, eventName);
                    newRowValues.put(TicketMasterOpener.COL_START_DATE, startDate);
                    newRowValues.put(TicketMasterOpener.COL_MIN_PRICE, ticketPriceMin);
                    newRowValues.put(TicketMasterOpener.COL_MAX_PRICE, ticketPriceMax);
                    String imageString = encodeTobase64(image);
                    newRowValues.put(TicketMasterOpener.COL_IMAGE_STRING, imageString);
                    newRowValues.put(TicketMasterOpener.COL_URL, eventUrl);

                    long newId = dataBase.insert(TicketMasterOpener.TABLE_NAME, null, newRowValues);

                    events.add(new TicketEvent(city, eventName, startDate, ticketPriceMin, ticketPriceMax, eventUrl, image, newId));
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

        /**
         * Gathers the TicketMaster data and builds the database.
         * <p>
         * Connects to the passed in URL.
         * Converts websites JSON file to a string.
         * Extracts the required information out of the string and into and array of TicketEvent objects.
         *
         * @param value An array of Integers used to store in the log and update the on screen progress bar.
         */
        public void onProgressUpdate(Integer...value)
        {
            theBar.setProgress(value[0]);
            Log.i("Progress", "Progress is :" + value[0] + "%");
        }

    }

    /**
     * Adds the rows to the screen.
     *  <p>
     * Course Name: CST8288_010
     * Class name: TicketMasterListAdapter
     * Date: November 19, 2020
     *
     * @version 1.0
     * @author Chris HIng
     */
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

            ImageView gotImage = view.findViewById(R.id.eventImage);
            gotImage.setImageBitmap(arEl.getImage());

            return view;
        }
    }

    private void loadDataFromDatabase()
    {
        TicketMasterOpener dbOpener = new TicketMasterOpener(this);
        dataBase = dbOpener.getWritableDatabase();
        String [] columns = {
                TicketMasterOpener.COL_ID,
                TicketMasterOpener.COL_CITY,
                TicketMasterOpener.COL_EVENT_NAME,
                TicketMasterOpener.COL_START_DATE,
                TicketMasterOpener.COL_MIN_PRICE,
                TicketMasterOpener.COL_MAX_PRICE,
                TicketMasterOpener.COL_IMAGE_STRING,
                TicketMasterOpener.COL_URL};
        Cursor results = dataBase.query(false, TicketMasterOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        int cityColumnIndex = results.getColumnIndex(TicketMasterOpener.COL_CITY);
        int eventNameColIndex = results.getColumnIndex(TicketMasterOpener.COL_EVENT_NAME);
        int startDateColIndex = results.getColumnIndex(TicketMasterOpener.COL_START_DATE);
        int minPriceColIndex = results.getColumnIndex(TicketMasterOpener.COL_MIN_PRICE);
        int maxPriceColIndex = results.getColumnIndex(TicketMasterOpener.COL_MAX_PRICE);
        int urlColIndex = results.getColumnIndex(TicketMasterOpener.COL_URL);
        int imageStringColIndex = results.getColumnIndex(TicketMasterOpener.COL_URL);
        int idColIndex = results.getColumnIndex(TicketMasterOpener.COL_ID);
        while(results.moveToNext())
        {
            String city = results.getString(cityColumnIndex);
            String eventName = results.getString(eventNameColIndex);
            String startDate = results.getString(startDateColIndex);
            double minPrice = Double.parseDouble(results.getString(minPriceColIndex));
            double maxPrice = Double.parseDouble(results.getString(maxPriceColIndex));
            String url = results.getString(urlColIndex);
            Bitmap imageDecoded = decodeBase64(results.getString(imageStringColIndex));
            long id = results.getLong(idColIndex);
            events.add(new TicketEvent(city, eventName, startDate, minPrice, maxPrice, url, imageDecoded, id));
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
        Bitmap image;

        private TicketEvent(String city, String eventName, String startDate, double ticketPriceMin, double ticketPriceMax, String url, Bitmap image, long index)
        {
            this.city = city;
            this.eventName = eventName;
            this.startDate = startDate;
            this.ticketPriceMin = ticketPriceMin;
            this.ticketPriceMax = ticketPriceMax;
            this.url = url;
            this.index = index;
            this.image = image;
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
        public Bitmap getImage() {return image;}
        public long getId() { return index; }
    }

    /*
     * Amol Suryawanshi (Apr 22 '16 at 10:28). converting Java bitmap to byte array [Webpage]. Retrieved from
     * https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    /*
     * Amol Suryawanshi (Apr 22 '16 at 10:28). converting Java bitmap to byte array [Webpage]. Retrieved from
     * https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
