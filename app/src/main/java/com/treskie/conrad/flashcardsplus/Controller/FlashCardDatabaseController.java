package com.treskie.conrad.flashcardsplus.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.treskie.conrad.flashcardsplus.ZipClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class FlashCardDatabaseController extends SQLiteOpenHelper{
    private static final String TAG = "FlashCardDatabase";
    private static final String TABLENAME = "card";
    private static final String COLID = "_ID";
    private static final String COLIDENTIFIER="DeckId";
    private static final String COL2 = "firstPart";
    private static final String COL3 = "secondPart";
    private static final int DATABASEVERSION = 3;
    private int cardId;
    private int deckIdentifier;
    private String firstPart;
    private String secondPart;

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

    public boolean addData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLID,cardId);
        contentValues.put(COLIDENTIFIER,deckIdentifier);
        contentValues.put(COL2,firstPart);
        contentValues.put(COL3,secondPart);
        Log.d(TAG, "ADDING VALUES TO CARD WITH IDENTIFIER: "+deckIdentifier);
        long result = db.insert(TABLENAME,null,contentValues);
        return result != -1;
    }
    //TODO: Replace firstPart with card index number
    public boolean updateCard(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,firstPart);
        contentValues.put(COL3,secondPart);
        long result = db.update(TABLENAME,contentValues,COLID+" = ? AND "+COLIDENTIFIER+" = ?",new String[]{Integer.toString(cardId), Integer.toString(deckIdentifier)});
        return result != -1;
    }

    public boolean deleteCard(int cardId, int deckIdentifier){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLENAME,COLID+" = ? AND "+COLIDENTIFIER+" = ?",new String[]{String.valueOf(cardId), String.valueOf(deckIdentifier)});
        return result != -1;
    }

    public boolean deleteCardsByDeck(int deckIdentifier){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLENAME,COLIDENTIFIER+" = ?",new String[]{String.valueOf(deckIdentifier)});
        return result != -1;
    }

    //TODO: Refactor this into something more appropriate like getDeckData
    public Cursor getCardId(int deckIdentifier){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COLID+" FROM "+TABLENAME+ " WHERE "+COLIDENTIFIER+"= "+deckIdentifier;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getSpecificCard(int cardId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+ " WHERE "+COLID+"= "+cardId;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getAllCardsByDeck(int deckIdentifier){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+ " WHERE "+COLIDENTIFIER+"= "+deckIdentifier;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public String getDbPath (Context mContext) {
        String tableName = getDatabaseName();
        String dbPath = "";
        try {
            dbPath = mContext.getDatabasePath(tableName).toString();
            return dbPath;
        } catch (Exception e){
            e.printStackTrace();
            return dbPath;
        }

    }

    public boolean importCardData(Context mContext) {
        ZipClass zc = new ZipClass();
        File sdCard = new File(zc.outputLocation);
        String tableName = getDatabaseName();
        String targetDBPath = mContext.getDatabasePath(tableName).toString();
        File targetDB = new File(targetDBPath);
        File backupDB = new File(sdCard, tableName);
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

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getDeckIdentifier() {
        return deckIdentifier;
    }

    public void setDeckIdentifier(int deckIdentifier) {
        this.deckIdentifier = deckIdentifier;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public void setFirstPart(String firstPart) {
        this.firstPart = firstPart;
    }

    public String getSecondPart() {
        return secondPart;
    }

    public void setSecondPart(String secondPart) {
        this.secondPart = secondPart;
    }

}
