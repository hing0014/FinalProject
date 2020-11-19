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

/**
 * This class is the main activity for the Recipe Search Page, it manipulates the listview via various methods and classes seen below
 * @author Kasia Kuzma
 * @version 1.0
 * Course CST2335
 * Lab Section 021
 * RecipeSearchPage Class
 */
public class RecipeSearchPage extends AppCompatActivity {

    /**
     *Where the user will search for recipes
     */
    EditText searchBar;
    /**
     * The listview that will show the recipes that match the value in the edittext
     */
    ListView recipeList;
    /**
     * Will activate the database and asynctask to search for matching recipes
     */
    Button searchRecipes;
    /**
     * Will show the loading progress of the recipe page
     */
    ProgressBar loadingBar;
    /**
     *An array of recipes; where the recipes will be stored once retrieved from database/website
     */
    ArrayList<RecipeGetters> recipes = new ArrayList<>();
    /**
     * Instance of the adapter class used to inflate the listview with rows of found recipes
     */
    RecipePageAdapter theAdapter;
    /**
     * Instance of the database class to store and load the database of recipes
     */
    SQLiteDatabase recipeDB;
    /**
     * Instance of the AsyncTask class that runs a background thread to retrieve the recipes from the website
     */
    ViewRecipesFromURL seeRecipes;

    /**
     * Initializes the variables above, loads the database, and calls click listeners for the search button and listview
     * @param savedInstanceState Pre-made bundle with every activity
     */
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
        theAdapter = new RecipePageAdapter();
        recipeList.setAdapter(theAdapter);

        searchRecipes.setOnClickListener( click -> {
            String searchResult = searchBar.toString();
            if(URLUtil.isValidUrl("http://www.recipepuppy.com/api/?q="+ searchResult +"&p=3&format=xml")){
                seeRecipes.execute("http://www.recipepuppy.com/api/?q="+ searchResult +"&p=3&format=xml");
                //how to fill rows with recipe titles?
            }
            else{
                Toast.makeText(getApplicationContext(), "Recipes not found", Toast.LENGTH_SHORT).show();
            }
        });

        recipeList.setOnItemClickListener((parent, view, pos, id) -> showRecipe(pos));

        //ViewRecipesFromURL viewRecipes = new ViewRecipesFromURL();
        //viewRecipes.execute("http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3&format=xml");//should I split this url?
    }

    /**
     * The Adapter class for the listview in order to inflate the listview with rows from another xml file
     */
    private class RecipePageAdapter extends BaseAdapter{

        /**
         * retrieves the amount of rows present
         * @return the size of the recipe array
         */
        @Override
        public int getCount() {
            return recipes.size();
        }

        /**
         * retrieves the contents of a specific row, determined by its position
         * @param position row position
         * @return row position
         */
        @Override
        public Object getItem(int position) {
            return position;
        }

        /**
         * meant for using databases for the row contents, also determined by position
         * @param position numbered position of the row in the listview
         * @return row position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * determines what is to be shown for each row of the listview
         * @param position row position
         * @param convertView old view
         * @param parent parent of the viewgroup
         * @return the new view that is to be inflated
         */
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

    /**
     * Creates a new instance of the Opener class to retrieve a database to fill it with the contents of the recipes
     */
    private void loadFromDatabase(){
        RecipePageOpener recipeOpener = new RecipePageOpener(this);
        recipeDB = recipeOpener.getWritableDatabase();
        String[] columns = {RecipePageOpener.COL_ID,
                RecipePageOpener.COL_TITLE,
                RecipePageOpener.COL_HREF,
                RecipePageOpener.COL_INGREDIENTS} ;
        @SuppressLint("Recycle") Cursor results = recipeDB.query(false, RecipePageOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        int titleColumnIndex = results.getColumnIndex(RecipePageOpener.COL_TITLE);
        int hrefColumnIndex = results.getColumnIndex(RecipePageOpener.COL_HREF);
        int ingredientsColumnIndex = results.getColumnIndex(RecipePageOpener.COL_INGREDIENTS);
        int idColumnIndex = results.getColumnIndex(RecipePageOpener.COL_ID);
        while(results.moveToNext())
        {
            String title = results.getString(titleColumnIndex);
            String href = results.getString(hrefColumnIndex);
            String ingredients = results.getString(ingredientsColumnIndex);
            long id = results.getLong(idColumnIndex);

            recipes.add(new RecipeGetters(title, href, ingredients, id));
        }
    }

    /**
     * Shows an alert dialog that describes the recipe once clicked from the listview, allowing the browser to open or for the recipe to be favourited
     * @param position position of the recipe in the array of recipes
     */
    protected void showRecipe(int position){
        RecipeGetters recipe = recipes.get(position);
        //View recipeAlert_view = getLayoutInflater().inflate(R.layout.recipe_alertdialog_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(recipe.getTitle());
        builder.setMessage(recipe.getHrefURL() +"/n" + recipe.getIngredients());
       // builder.setView(recipeAlert_view);
        builder.setPositiveButton("Favourite", (click, b) -> Toast.makeText(getApplicationContext(),"Recipe has been favourited", Toast.LENGTH_SHORT).show());
        builder.setNegativeButton("Close", (click, b) -> { });
        builder.setNeutralButton("Open in Browser", (click, b) -> {

        });
    }

    /**
     * Creates a background thread to retrieve external data and store it internally, all while allowing the progress to be tracked
     */
    @SuppressLint("StaticFieldLeak")
    private class ViewRecipesFromURL extends AsyncTask< String, Integer, String> {

        /**
         * Retrieves the recipes from the url and uses an xml parser to read the website's data and initialize the variables below
         * @param strings pass in when the execute method is invoked, in this case it is a url
         * @return a string, in this case a string to indicate the completion of the method
         */
        @Override
        protected String doInBackground(String... strings) {
            String title = null;
            String href = null;
            String ingredients = null;
            try {
                String encode = URLEncoder.encode(strings[0], "UTF-8"); //fix
                URL url = new URL(encode);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                publishProgress(50);
                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG){
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
                    eventType = xpp.next();
                }
                ContentValues newRows = new ContentValues();
                newRows.put(RecipePageOpener.COL_TITLE, title);
                newRows.put(RecipePageOpener.COL_HREF, href);
                newRows.put(RecipePageOpener.COL_INGREDIENTS, ingredients);
                long newRowID = recipeDB.insert(TicketMasterOpener.TABLE_NAME, null, newRows);
                recipes.add(new RecipeGetters(title, href, ingredients, newRowID));

            }catch (Exception ignored){

            }
            publishProgress(100);
            return "Done";
        }

        /**
         * Allows for actions to be completed once the progress of the thread has been updated
         * @param args array of integers
         */
        public void onProgressUpdate(Integer ... args) {
            loadingBar.setProgress(args[0]);
            Log.i("Loading Bar", "Loading..."+args[0]+"% complete");
        }

        /**
         * Allows for actions once the thread has finished
         * @param fromDoInBackground the string originally passed to the doInBackground method, a url in this case
         */
        public void onPostExecute(String fromDoInBackground) {
            Log.i("Loading Bar", "Loading Complete");
            loadingBar.setVisibility(View.INVISIBLE);
            theAdapter.notifyDataSetChanged();
        }
    }
}