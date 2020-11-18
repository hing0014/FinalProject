package com.example.finalProject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Defines the TicketMaster database format.
 *  <p>
 * Course Name: CST8288_010
 * Class name: TicketMasterOpener
 * Date: November 19, 2020
 *
 * @version 1.0
 * @author Chris HIng
 */
public class TicketMasterOpener extends SQLiteOpenHelper
{
    /**
     * Table final fields for specifying column headers and table definitions.
     */
    protected final static String DATABASE_NAME = "TicketMasterDatabase";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "EVENTS";
    public final static String COL_CITY = "CITY";
    public final static String COL_EVENT_NAME = "EVENT NAME";
    public final static String COL_START_DATE = "START DATE";
    public final static String COL_MIN_PRICE = "MIN PRICE";
    public final static String COL_MAX_PRICE = "MAX PRICE";
    public final static String COL_URL = "URL";
    public final static String COL_IMAGE_STRING = "IMAGE";
    public final static String COL_ID = "_id";

    /**
     * The single argument constructor that takes a Context object.
     * <p>
     *
     * @param ctx Context object used in the super call of SQLiteOpenHelper.
     */
    public TicketMasterOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * Creates a new database.
     * <p>
     * uses SQl to create a database.
     *
     * @param db SQLiteDatabase object used to create a new table.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CITY + " CITY, "
                + COL_EVENT_NAME + " EVENT NAME, "
                + COL_START_DATE + " START DATE, "
                + COL_MIN_PRICE + " MIN PRICE, "
                + COL_MAX_PRICE + " MAX PRICE, "
                + COL_IMAGE_STRING + " IMAGE, "
                + COL_URL  + " URL);");
    }

    /**
     * Rebuilds the database.
     * <p>
     * Rebuilds the database if the two passed in ints are not identical.
     *
     * @param db SQLiteDatabase object.
     * @param oldVersion old database version number.
     * @param newVersion new database version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Downgrades the database.
     * <p>
     * Downgrades the database if the two passed in ints are not identical.
     *
     * @param db SQLiteDatabase object.
     * @param oldVersion old database version number.
     * @param newVersion new database version number.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
