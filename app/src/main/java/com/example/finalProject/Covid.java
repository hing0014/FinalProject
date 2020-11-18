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
        String country,countryCode, province, status;
        double cases;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_covid);

            ListView myList = (ListView) findViewById(R.id.listView);
            myAdapter = new MyListAdapter();
            myList.setAdapter(myAdapter);

            countryDisp = findViewById(R.id.country);
            countryCodeDisp = findViewById(R.id.conCode);
            provinceDisp = findViewById(R.id.province);
            casesDisp = findViewById(R.id.cases);
            statusDisp = findViewById(R.id.status);
            progressBar =  findViewById(R.id.progressBar);

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
                    notifyDataSetChanged();
                    tView.setText(getItem(position));
                    //return it to be put in the table
                    return newView;
                }
            }
                                               //Type1     Type2   Type3
 class CovidRequest extends AsyncTask< String, Integer, String> {

    @Override
    public String doInBackground(String... args) {
         String corona = "";
           try {
            //create a URL object of what server to contact:
           URL url = new URL(corona);

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
                country = covidObject.getString("Country");
                countryCode = covidObject.getString("CountryCode");
                province = covidObject.getString("Province");
                cases = covidObject.getDouble("Cases");
                status = covidObject.getString("Status");
                //get the double associated with "value"
                 countryDisp.setText(country);
//               countryCodeDisp.setText(countryCode);
//               provinceDisp.setText(province);
//               casesDisp.setText(String.valueOf(cases));
//               statusDisp.setText(status);
           }

        } catch (Exception e) {
            e.printStackTrace();
       }
      return corona;
    }

    @Override //Type 2
    public void onProgressUpdate(Integer... args) {

        progressBar.setVisibility(View.VISIBLE);

    }

    //Type3
    protected void onPostExecute(String fromDoInBackground) {
          countryDisp.setText(country);
//        countryCodeDisp.setText(countryCode);
//        provinceDisp.setText(province);
//        casesDisp.setText(String.valueOf(cases));
//        statusDisp.setText(status);
//        progressBar.setVisibility(View.VISIBLE);
     }
  }
 }



