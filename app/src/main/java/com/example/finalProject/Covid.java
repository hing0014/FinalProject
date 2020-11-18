    package com.example.finalProject;

    import androidx.appcompat.app.AppCompatActivity;
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
    import com.example.androidlabs.R;
    import org.json.JSONArray;
    import org.json.JSONObject;
    import java.io.BufferedReader;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.ArrayList;

    public class Covid extends AppCompatActivity {
        ArrayList<String> list = new ArrayList<>();
        MyListAdapter myAdapter;
        ImageButton searchButton;
        EditText searchText;
        TextView countryDisp, countryCodeDisp, provinceDisp, casesDisp, statusDisp;
        ProgressBar progressBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_covid);

            ListView myList = findViewById(R.id.listView);
            myAdapter = new MyListAdapter();
            myList.setAdapter(myAdapter);

            countryDisp= findViewById(R.id.countryResult);
            countryCodeDisp= findViewById(R.id.countryCodeResult);
            provinceDisp= findViewById(R.id.provinceResult);
            casesDisp= findViewById(R.id.caseResult);
            statusDisp= findViewById(R.id.statusResult);
            progressBar=  findViewById(R.id.progressBar);

            searchText = findViewById(R.id.searchText);
            searchButton = findViewById(R.id.magnify);
            searchButton.setOnClickListener( (click) ->
            {
                CovidRequest req = new CovidRequest();
                req.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
            });
        }

            class MyListAdapter extends BaseAdapter {

                @Override //number of items in the list
                public int getCount() {
                    return list.size();
                }

                @Override // what string goes at row;
                public String getItem(int position) {
                    return list.get(position);
                }

                @Override // database id at row i
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View v, ViewGroup parent) {  //this returns the layout that will be positioned at the specified row in the list.

                    LayoutInflater inflater = getLayoutInflater();

                    //make a new row:
                    View newView = inflater.inflate(R.layout.row_covidlayout, parent, false);

                    //set what the text should be for this row:
                    TextView tView = newView.findViewById(R.id.searchText);
                    tView.setText(getItem(position));
                    //return it to be put in the table
                    return newView;
                }
            }
                                               //Type1     Type2   Type3
private class CovidRequest extends AsyncTask< String, Integer, String> {
    @Override
    public String doInBackground(String... args) {

           try {
            //create a URL object of what server to contact:
           URL url = new URL(args[0]);

           //open the connection
           HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

           //wait for data:
           InputStream response = urlConnection.getInputStream();

           //JSON reading:   Look at slide 26
           //Build the entire string response:
           BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
           StringBuilder sb = new StringBuilder();

           String line = null;
           while ((line = reader.readLine()) != null) {
               sb.append(line + "\n");
           }
           String result = sb.toString(); //result is the whole string

          // convert string to JSONArray: Look at slide 27:
           JSONArray jArray = new JSONArray(result);

           for (int i=0; i < jArray.length(); i++) {

               JSONObject covidObject = jArray.getJSONObject(i);

               // Pulling items from the array
               String country = covidObject.getString("Country");
               String countryCode = covidObject.getString("CountryCode");
               String province = covidObject.getString("Province");
               Double cases = covidObject.getDouble("Cases");
               String status = covidObject.getString("Status");

               //get the double associated with "value"
               countryDisp.setText((R.string.country) + country);
               countryCodeDisp.setText("R.string.conCode"+ countryCode);
               provinceDisp.setText("R.string.province"+ province);
               casesDisp.setText("R.string.cases"+ cases.toString());
               statusDisp.setText("R.string.status"+ status);

              }
        } catch (Exception e) {
            e.printStackTrace();
       }
      return "";
    }

    //Type 2
    public void onProgressUpdate(Integer... args) {

        progressBar.setVisibility(View.VISIBLE);

    }

    //Type3
    public void onPostExecute(String fromDoInBackground) {
        progressBar.setVisibility(View.VISIBLE);
    }
    }
    }



