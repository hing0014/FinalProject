package com.example.finalProject;

/**
 * @version 1.0
 * @author Kasia Kuzma
 * Course CST2335
 * Lab Section 021
 * RecipeGetters Class
 *
 * This class allows for recipes to be created and stored here for the database, based on the given api file
 */
public class RecipeGetters {

    /**
     * the recipe title, recipe url, and recipe ingredients to be initialized
     */
    protected String title, hrefURL, ingredients;
    /**
     * the id for each recipe in the database
     */
    protected long recipeID;

    /**
     * creates a new recipe in the database based on the attributes from the given file
     * @param title title of Recipe
     * @param hrefURL href url of Recipe to open in browser
     * @param ingredients ingredients of recipe
     * @param recipeID id of recipe for created database
     */
    public RecipeGetters(String title, String hrefURL, String ingredients, long recipeID){
        this.title = title;
        this.hrefURL = hrefURL;
        this.ingredients = ingredients;
        this.recipeID = recipeID;
    }

    /**
     * getter for the title of the recipe
     * @return title
     */
    public String getTitle(){return title;}

    /**
     * getter for the url that leads to the full recipe
     * @return hrefURL
     */
    public String getHrefURL(){return hrefURL;}

    /**
     * getter for the ingredients of the recipe
     * @return ingredients
     */
    public String getIngredients(){return ingredients;}

    /**
     * getter for the id of the recipe in the database
     * @return recipeID
     */
    public long getRecipeID(){return recipeID;}
}

