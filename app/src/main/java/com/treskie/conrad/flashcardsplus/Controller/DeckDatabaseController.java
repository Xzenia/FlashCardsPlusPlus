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


public class DeckDatabaseController extends SQLiteOpenHelper {
    private static final String TAG = "ClassListDatabase";

    private static final String TABLENAME = "deck";
    private static final String COLID = "_ID";
    private static final String COL1 = "name";

    private static final int DATABASEVERSION = 1;
    private Context mContext;
    public DeckDatabaseController(Context context){
        super(context,TABLENAME,null,DATABASEVERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE "+ TABLENAME + "(_ID INTEGER PRIMARY KEY, "+
                COL1 +" TEXT);";
        db.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLENAME);
        onCreate(db);
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

    public boolean checkIfDeckNameExists(String deckName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLENAME+" WHERE "+COL1+ " = '"+ deckName + "';";
        Cursor data = db.rawQuery(query,null);
        return data != null;
    }

    public boolean deleteDeck(int deckId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLENAME, COLID + " = ?", new String[]{String.valueOf(deckId)});
        return result != -1;
    }

    public boolean renameDeck(String newDeckName, int deckId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, newDeckName);
        long result = db.update(TABLENAME,contentValues,COLID+" = ?",new String[]{Integer.toString(deckId)});
        return result != -1;
    }

    public boolean exportDeckData(Context mContext) {
        File sdCard = Environment.getExternalStorageDirectory();
        String backupDBPath= getDatabaseName();
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

    public boolean importDeckData(Context mContext) {
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
