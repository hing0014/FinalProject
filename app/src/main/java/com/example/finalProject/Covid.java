/* Course Name: CST2335_021
 * Class name: CST2335 Graphical Interface Programming
 * Covid19 Case Data
 * Date: November 19, 2020
 * Student Name : Jihyun Park
 * purpose: This is the final project with Teammates
 * This is the SQLite database
 */
package com.example.finalProject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the database for the covid-19 cases
 * This is the main class of Covid, extends AppCompatActivity.
 *
 * @ author Jihyun Park
 **/
public class Covid extends AppCompatActivity {

    ArrayList<CovidEvent> list = new ArrayList<>();
    MyListAdapter myAdapter;
    ImageButton searchButton;
    EditText searchText;
    TextView countryDisp, countryCodeDisp, provinceDisp, casesDisp, statusDisp;
    ProgressBar progressBar;
    String country, countryCode, province, status;
    double cases;
    SQLiteDatabase CovidDB;
    CovidOpener covidOpener;

    /**
     * When the button, Covid-19, of the main page, connected with this page.
     * When the search button is clicked the search button, stored data is showed the covid table which in contained all information that user wants.
     *
     * @param savedInstanceState Bundle object used in the super call of onCreate.
     * @author Jihyun Park
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid);

        ListView myList =  findViewById(R.id.listView);
        myAdapter = new MyListAdapter();
        myList.setAdapter(myAdapter);

    //   loadDataFromDatabase();

        countryDisp = findViewById(R.id.country);
        countryCodeDisp = findViewById(R.id.conCode);
        provinceDisp = findViewById(R.id.province);
        casesDisp = findViewById(R.id.cases);
        statusDisp = findViewById(R.id.status);
        progressBar = findViewById(R.id.progressBar);

        searchButton = findViewById(R.id.magnify);
        searchButton.setOnClickListener((click) ->
        {
            searchText = findViewById(R.id.searchText);
            searchText.getText().toString();

            CovidRequest req = new CovidRequest();
            req.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
            // Toast.makeText(this, R.string.searchText1, Toast.LENGTH_LONG).show();
        });
    }

    /*
     * Extended BaseAdapter that is the bridge between a ListView and the data that backs the list.
     * ListView can display any data provided that it is wrapped in a MyListAdapter.
     * all the methods are override from BaseAdapter
     * This is explin what is inside of the list, number of items, rows.
     * Row-layout that will be positioned at the specified row in the list.
     * @author Jihyun Park
     *
     * */
    class MyListAdapter extends BaseAdapter {
        /*number of items in the list
         * Override
         * @author Jihyun Park
         * */
        @Override
        public int getCount() {
            return list.size();
        }

        /* Objects go at row in the list
         * Override
         * @param int position
         * @author Jihyun Park
         * */
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        /* database id at row
         * Override
         * @author Jihyun Park
         * */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /* this returns the layout that will be positioned at the specified row in the list.
         * this view is make a new row, set the text by row, and returns information to the table
         * Override
         * @author Jihyun Park
         * */
        @Override
        public View getView(int position, View v, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.covid_row_layout, parent, false);
            TextView tView = newView.findViewById(R.id.searchText);
            notifyDataSetChanged();
            tView.setText(getItem(position).toString());
            return newView;
        }
    }

    /*
     * This is retrieve any privious data
     * @author Jihyun Park
     * */
    private void loadDataFromDatabase() {
        covidOpener = new CovidOpener(this);
        CovidDB = covidOpener.getWritableDatabase();
        String[] columns = {covidOpener.COL_TITLE, covidOpener.COL_COUNTRY, covidOpener.COL_CONCODE, covidOpener.COL_PROVINCE,
                covidOpener.COL_CASE, covidOpener.COL_STATUS};
        Cursor results = CovidDB.query(false, covidOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        int countryColumnIndex = results.getColumnIndex(covidOpener.COL_COUNTRY);
        int countryCodeColumnIndex = results.getColumnIndex(covidOpener.COL_CONCODE);
        int provinceColumnIndex = results.getColumnIndex(covidOpener.COL_PROVINCE);
        int caseColumnIndex = results.getColumnIndex(covidOpener.COL_CASE);
        int statusColumnIndex = results.getColumnIndex(covidOpener.COL_STATUS);

        while (results.moveToNext()) {
            String country = results.getString(countryColumnIndex);
            String countryCode = results.getString(countryCodeColumnIndex);
            String province = results.getString(provinceColumnIndex);
            Double cases = results.getDouble(caseColumnIndex);
            String status = results.getString(statusColumnIndex);

            list.add(new CovidEvent(country, countryCode, province, cases, status));
        }
    }
//        protected void updateContext(Context c) {
//            //Create a ContentValues object to represent a database row:
//            ContentValues updatedValues = new ContentValues();
//            updatedValues.put(CovidOpener.COL_CASE, c.getCases());
//
//            //now call the update function:
//            CovidDB.update(CovidOpener.TABLE_NAME, updatedValues, CovidOpener.COL_CASE + "= ?", new String[]{Double.toString(c.getCases())});
//        }
//
//        protected void deleteContext(Context c) {
//            CovidDB.delete(CovidOpener.TABLE_NAME, CovidOpener.COL_CASE + "= ?", new String[]{Double.toString(c.getCases())});
//        }


    /* this class has 3 important functions: doInBackground, onProgressUpdate, onPostExecute
     * In order for the interface to be responsive to user input, any long running tasks must be run
     * To start the thread, create an object and call execute()
     *@author: Jihyun Park
     */
    private class CovidRequest extends AsyncTask<String, Integer, String> {

        /* This is contains internet website which is contains information that users want
         * connection with website, bring the data and set the informatino to the table
         * doInBackground() – parameter is array type of String
         * Override mothod
         * @ Author: Jihyun Park
         */
        @Override
        public String doInBackground(String... args) {
            String corona = "";
            try {
                URL url = new URL("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject covidObject = jArray.getJSONObject(i);
                    country = covidObject.getString("Country");
                    countryCode = covidObject.getString("CountryCode");
                    province = covidObject.getString("Province");
                    cases = covidObject.getDouble("Cases");
                    status = covidObject.getString("Status");
                    ContentValues newRowValues = new ContentValues();

                    newRowValues.put(covidOpener.COL_COUNTRY, country);
                    newRowValues.put( covidOpener.COL_CONCODE, countryCode);
                    newRowValues.put(covidOpener.COL_PROVINCE, province);
                    newRowValues.put(covidOpener.COL_CASE, cases);
                    newRowValues.put(covidOpener.COL_STATUS, status);

                    list.add(new CovidEvent(country, countryCode, province, cases, status));
                  }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return corona;
        }

        /*
         * this is the response from a server
         * it shows computation progresses, update GUI
         * @author Jihyin Park
         */
        @Override //Type 2
        public void onProgressUpdate(Integer... args) {
            publishProgress(25);
            publishProgress(50);
            publishProgress(75);
            progressBar.setVisibility(View.VISIBLE);

        }

        /*
         * incoming parameter is the exact same object that was returned by doInBackground.
         * @param String fromDoInBackground
         * @Author Jihyun Park
         */
        protected void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.INVISIBLE);
            countryDisp.setText(country);
            countryCodeDisp.setText(countryCode);
            provinceDisp.setText(province);
            casesDisp.setText(String.valueOf(cases));
            statusDisp.setText(status);
        }
    }

    /*
     * this is the class of the basic information of the related Covid Event
     * @author: Jihyun Park
     * */
    class CovidEvent {

        String country;
        String countryCode;
        String province;
        double cases;
        String status;

        /* This is the class of the covid event
         * @param String type is country, countryCode, province, status and double typs is cases
         * @Author Jihyun Park*/
        private CovidEvent(String country, String countryCode, String province, double cases, String status) {
            this.country = country;
            this.countryCode = countryCode;
            this.province = province;
            this.cases = cases;
            this.status = status;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getProvince() {
            return province;
        }

        public double getCases() {
            return cases;
        }

        public String getStatus() {
            return status;
        }
    }
}


