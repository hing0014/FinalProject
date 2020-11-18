package com.example.finalProject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */
public class RecipePageOpener extends SQLiteOpenHelper{

    protected final static String DATABASE_NAME = "RecipesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "RECIPES";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_HREF = "HREF";
    public final static String COL_INGREDIENTS = "INGREDIENTS";
    public final static String COL_ID = "_id";

    /**
     * @param ctx
     */
    public RecipePageOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_HREF + " text,"
                + COL_INGREDIENTS + " text);");
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
