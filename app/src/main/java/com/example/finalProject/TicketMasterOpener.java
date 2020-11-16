package com.example.finalProject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TicketMasterOpener extends SQLiteOpenHelper
{
    protected final static String DATABASE_NAME = "TicketMasterDatabase";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "EVENTS";
    public final static String COL_CITY = "CITY";
    public final static String COL_EVENT_NAME = "EVENT NAME";
    public final static String COL_START_DATE = "EVENT NAME";
    public final static String COL_MIN_PRICE = "MIN PRICE";
    public final static String COL_MAX_PRICE = "MAX PRICE";
    public final static String COL_URL = "URL";
    public final static String COL_ID = "_id";


    public TicketMasterOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CITY + " text,"
                + COL_EVENT_NAME + " text,"
                + COL_START_DATE + " text,"
                + COL_MIN_PRICE + " text,"
                + COL_MAX_PRICE + " text,"
                + COL_URL  + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
