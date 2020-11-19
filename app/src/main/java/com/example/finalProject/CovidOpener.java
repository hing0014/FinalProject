package com.example.finalProject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* This is the class of the covidOpener extends by SQliteHelper.
 * This class makes table in order to store information of the Covid cases
 * @author: jihyun Park
 */
public class CovidOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "CovidDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "Covid";
    public final static String COL_TITLE = "Covid Case";
    public final static String COL_COUNTRY = "Country";
    public final static String COL_CONCODE = "Country Code";
    public final static String COL_PROVINCE = "Province";
    public final static String COL_CASE = "Cases";
    public final static String COL_STATUS = "Status";

    /*@param Context ctx
     * @author Jihyun Park
     * this is the inherited constructor
     * */
    public CovidOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /* this is onCreate function for the creating the tavle
     * @param SQLiteDatabase db
     * @ Author: Jihyun Park
     * */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + "  text," + COL_COUNTRY + " text," + COL_CONCODE + " TEXT," + COL_PROVINCE + " text,"
                + COL_CASE + " double," + COL_STATUS + " text);");
    }

    /*this is for using upgrading of the table
    * it is Overrided
     * @param SQLiteDatabase db, int oldVersion, int newVersion
     * @ahuthor Jihyun Park
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*this is for using downgrading of the table
     * it is Overrided
     * @param SQLiteDatabase db, int oldVersion, int newVersion
     * @author Jihyun Park
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
