package com.treskie.conrad.flashcardsplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class FlashCardDatabaseController extends SQLiteOpenHelper{
    private static final String TAG = "FlashCardDatabase";

    private static final String TABLENAME = "card";
    private static final String COLID = "_ID";
    private static final String COLIDENTIFIER="IDENTIFIER";
    private static final String COL2 = "firstPart";
    private static final String COL3 = "secondPart";
    private static final int DATABASEVERSION = 3;


    public FlashCardDatabaseController(Context context){
        super(context,TABLENAME,null,DATABASEVERSION);
    }

    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE "+ TABLENAME + "(_ID INTEGER PRIMARY KEY, "+
                COLIDENTIFIER +" TEXT, "+ COL2 +" TEXT, "+ COL3 +" TEXT);";
        db.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }

    public boolean addData(int id,int identifier, String firstPart, String secondPart){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLID,id);
        contentValues.put(COLIDENTIFIER,identifier);
        contentValues.put(COL2,firstPart);
        contentValues.put(COL3,secondPart);
        Log.d(TAG, "ADDING VALUES TO CARD WITH IDENTIFIER: "+identifier);
        long result = db.insert(TABLENAME,null,contentValues);
        return result != -1;
    }

    //TODO: Replace firstPart with card index number
    public boolean updateCard(String oldFirstPart, String oldSecondPart,String newFirstPart, String newSecondPart,int deckId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,newFirstPart);
        contentValues.put(COL3,newSecondPart);
        long result = db.update(TABLENAME,contentValues,COL2+" = ? AND "+COL3+" = ? AND "+COLIDENTIFIER+" = ?",new String[]{oldFirstPart,oldSecondPart,Integer.toString(deckId)});
        return result != -1;
    }


    public Cursor getData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+ " WHERE IDENTIFIER="+id;
        Cursor data = db.rawQuery(query,null);
        return data;
    }


}
