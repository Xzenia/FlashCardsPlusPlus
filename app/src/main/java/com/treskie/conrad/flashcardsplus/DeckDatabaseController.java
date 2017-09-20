package com.treskie.conrad.flashcardsplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DeckDatabaseController extends SQLiteOpenHelper {
    private static final String TAG = "ClassListDatabase";

    private static final String TABLENAME = "deck";
    private static final String COLID = "_ID";
    private static final String COL1 = "name";

    private static final int DATABASEVERSION = 1;

    public DeckDatabaseController(Context context){
        super(context,TABLENAME,null,DATABASEVERSION);
    }

    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE "+ TABLENAME + "(_ID INTEGER PRIMARY KEY, "+
                COL1 +" TEXT);";
        db.execSQL(createTable);

    }

    public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLENAME);
        onCreate(db);
        //TODO: Test this by changing the structure of the database.
    }

    public boolean addData(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLID,id);
        contentValues.put(COL1,name);
        Log.d(TAG, "addData: Adding "+ name + " to "+ TABLENAME);
        long result = db.insert(TABLENAME,null,contentValues);
        return result != -1;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getIdData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+" WHERE "+COL1+ " = '"+ name + "';";
        Cursor data = db.rawQuery(query,null);
        return data;
    }


}
