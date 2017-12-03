package com.treskie.conrad.flashcardsplus.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


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
    public boolean updateCard(String newFirstPart, String newSecondPart,int cardId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,newFirstPart);
        contentValues.put(COL3,newSecondPart);
        long result = db.update(TABLENAME,contentValues,COLID+" = ?",new String[]{Integer.toString(cardId)});
        return result != -1;
    }

    public boolean deleteCard(int cardId, int deckId){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLENAME,COLID+" = ? AND "+COLIDENTIFIER+" = ?",new String[]{String.valueOf(cardId), String.valueOf(deckId)});
        return result != -1;
    }

    public boolean deleteCardsByDeck(int deckId){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLENAME,COLIDENTIFIER+" = ?",new String[]{String.valueOf(deckId)});
        return result != -1;
    }

    //TODO: Refactor this into something more appropriate like getDeckData
    public Cursor getData(int deckId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+ " WHERE IDENTIFIER="+deckId;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getSpecificCard(int cardId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+ " WHERE _ID = "+cardId;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean exportCardData(Context mContext) {
        File sdCard = Environment.getExternalStorageDirectory();
        String backupDBPath  = getDatabaseName();
        String currentDBPath = mContext.getDatabasePath(backupDBPath).toString();
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sdCard, backupDBPath);
        try {
            FileChannel source = new FileInputStream(currentDB).getChannel();
            FileChannel destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean importCardData(Context mContext) {
        File sdCard = Environment.getExternalStorageDirectory();
        String backupDBPath = getDatabaseName();
        String targetDBPath = mContext.getDatabasePath(backupDBPath).toString();
        File targetDB = new File(targetDBPath);
        File backupDB = new File(sdCard, backupDBPath);
        try {
            FileChannel source = new FileInputStream(backupDB).getChannel();
            FileChannel destination = new FileOutputStream(targetDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



}
