package com.example.finalProject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class RecipeSearchPage extends AppCompatActivity {

    EditText searchBar;
    ListView recipeList;
    Button searchRecipes;
    ProgressBar loadingBar;
    ArrayList<RecipeGetters> recipes = new ArrayList<>();
    RecipePageAdapter theAdapter;
    SQLiteDatabase recipeDB;
    ViewRecipesFromURL seeRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search_page);
        searchBar = findViewById(R.id.searchRecipe);
        searchRecipes = findViewById(R.id.searchButtonRecipe);
        loadingBar = findViewById(R.id.recipeProgress);
        loadingBar.setVisibility(View.VISIBLE);
        recipeList = findViewById(R.id.recipesList);
        loadFromDatabase();
        searchRecipes.setOnClickListener( click -> {
            String searchResult = searchBar.toString();
        });

        theAdapter = new RecipePageAdapter();
        recipeList.setAdapter(theAdapter);

        recipeList.setOnItemClickListener((parent, view, pos, id) -> {
            showRecipe(pos);
        });
        ViewRecipesFromURL viewRecipes = new ViewRecipesFromURL();
        viewRecipes.execute("http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3");//should I split this url?
    }

    private class RecipePageAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecipeGetters getRecipe = recipes.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.recipe_row_layout, parent, false); // why chris used old view?
            TextView rowText = newView.findViewById(R.id.recipeTextRow);
            rowText.setText(getRecipe.getTitle());//Is a loop needed to populate all rows?

            return newView;
        }
    }

    private void loadFromDatabase(){
        RecipePageOpener recipeOpener = new RecipePageOpener(this);
        recipeDB = recipeOpener.getWritableDatabase();
        String[] columns = {recipeOpener.COL_ID,
                recipeOpener.COL_TITLE,
                recipeOpener.COL_HREF,
                recipeOpener.COL_INGREDIENTS} ;
        Cursor results = recipeDB.query(false, recipeOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        int titleColumnIndex = results.getColumnIndex(recipeOpener.COL_TITLE);
        int hrefColumnIndex = results.getColumnIndex(recipeOpener.COL_HREF);
        int ingredientsColumnIndex = results.getColumnIndex(recipeOpener.COL_INGREDIENTS);
        int idColumnIndex = results.getColumnIndex(recipeOpener.COL_ID);
        while(results.moveToNext())
        {
            String title = results.getString(titleColumnIndex);
            String href = results.getString(hrefColumnIndex);
            String ingredients = results.getString(ingredientsColumnIndex);
            long id = results.getLong(idColumnIndex);

            //add the new Contact to the array list:
            recipes.add(new RecipeGetters(title, href, ingredients, id));
        }
    }

    protected void showRecipe(int position){
        RecipeGetters recipe = recipes.get(position);
        //View recipeAlert_view = getLayoutInflater().inflate(R.layout.recipe_alertdialog_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(recipe.getTitle());
        builder.setMessage(recipe.getHrefURL() +"/n" + recipe.getIngredients());
       // builder.setView(recipeAlert_view);
        builder.setPositiveButton("Favourite", (click, b) -> {
            Toast.makeText(getApplicationContext(),"Recipe has been favourited", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Close", (click, b) -> {

        });
        builder.setNeutralButton("Open in Browser", (click, b) -> {

        });
    }
    private class ViewRecipesFromURL extends AsyncTask< String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String encode = URLEncoder.encode(strings[0], "UTF-8");
                URL url = new URL(encode);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                String title, href, ingredients;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(xpp.getName().equals("title")){
                        xpp.next();
                        title = xpp.getText();
                    }
                    if (xpp.getName().equals("href")){
                        xpp.next();
                        href = xpp.getText();
                    }
                    if (xpp.getName().equals("ingredients")){
                        xpp.next();
                        ingredients = xpp.getText();
                    }
                }

            }catch (Exception e){

            }
            return "Done";
        }
        public void onProgressUpdate(Integer ... args) {
            loadingBar.setProgress(args[0]);
            Log.i("Loading Bar", "Loading..."+args[0]+"% complete");
        }

        public void onPostExecute(String fromDoInBackground) {
            Log.i("Loading Bar", "Loading Complete");
        }
    }
}