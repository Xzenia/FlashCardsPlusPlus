package com.treskie.conrad.flashcardsplus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Add.AddCard;
import com.treskie.conrad.flashcardsplus.Controller.DeckDatabaseController;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.Viewer.CardViewer;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DeckDatabaseController dc;
    private FlashCardDatabaseController fc;
    private ZipClass zc;
    private ListView lvListView;
    private ListView lvRestoreBackupListView;
    private String deckName = "";
    private int deckId = 0;
    private Dialog nameBackupDialog;
    private EditText etDeckName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvListView = (ListView) findViewById(R.id.listView);
        dc = new DeckDatabaseController(this);
        fc = new FlashCardDatabaseController(this);
        zc = new ZipClass();
        populateListView();
        registerForContextMenu(lvListView);
    }

    private void populateListView(){
        Cursor data = dc.getData();
        //goes through all the entries in the deck database and adds them to the ArrayList
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }
        //  Note: simple_list_item_1 is a list layout that only displays one line of text per entry
        final ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        lvListView.setAdapter(adapter);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                deckName = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: User clicked on " + deckName);
                deckId = getDeckId(deckName);
                if (deckId > 0) {
                    Log.d(TAG, "onItemClick: The Deck ID is: " + deckId);
                    viewItem(deckId);
                } else {
                    Log.e(TAG, "onItemClick: The Deck ID is: " + deckId);
                    toastMessage("Deck is invalid or does not exist!");
                }
            }
        });
    }

    public int getDeckId(String deckName){
        int id = 0;
        Cursor data = dc.getIdData(deckName);
        while (data.moveToNext()) {
            id = data.getInt(0);
        }
        return id;
    }

    //Intents

    public void goToAddCardActivity(){
        Intent addCardActivity = new Intent(this, AddCard.class);
        addCardActivity.putExtra("deckId", deckId);
        startActivity(addCardActivity);
        finish();
    }

    public void viewItem(int id){
        Intent goToEditActivity = new Intent (this, CardViewer.class);
        goToEditActivity.putExtra("deckId",id);
        startActivity(goToEditActivity);
    }

    public void goToMainActivity(){
        Intent goToMain = new Intent (this, MainActivity.class);
        startActivity(goToMain);
    }

    private void deleteDeck() {
        dc.deleteDeck(deckId);
        fc.deleteCardsByDeck(deckId);
        goToMainActivity();
        finish();

    }
    //Top bar menu
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_deck:
                addDeckPopup();
                return true;
            case R.id.action_backup_data:
                backupNamePopup();
                return true;
            case R.id.action_restore_data:
                restoreDataPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Context Menu. Shows up when a listview item is held long enough.
    public void onCreateContextMenu(ContextMenu menu, View contextView, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, contextView, menuInfo);
        //AdapterView.AdapterContextMenuInfo -> extra menu information provided to the onCreateContextMenu
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getMenuInflater();
        deckName = lvListView.getItemAtPosition(info.position).toString();
        menu.setHeaderTitle(deckName);
        deckId = getDeckId(deckName);
        inflater.inflate(R.menu.main_activity_context_menu, menu);
    }
    //If any of the items in the context menu are selected
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.addCard:
                goToAddCardActivity();
                return true;
            case R.id.renameDeck:
                renameDeckPopup(deckName);
                return true;
            case R.id.deleteDeck:
                deleteDeck();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void restoreData(String fileName) {
        zc.unzip(fileName);
        boolean confirmDeckImport = dc.importDeckData(this);
        boolean confirmCardImport = fc.importCardData(this);
        if (confirmDeckImport && confirmCardImport) {
            toastMessage("Deck and Card DB import successful!!!");
            goToMainActivity();
        } else {
            toastMessage("Deck and Card DB import failed!");
        }
        zc.deleteFiles();
    }

    private void restoreDataPopup(){
        final Dialog restoreBackupDialog = new Dialog (this);
        restoreBackupDialog.setContentView(R.layout.activity_restore_backup);
        restoreBackupDialog.setTitle("Restore Backup");
        lvRestoreBackupListView = restoreBackupDialog.findViewById(R.id.backupListView);
        populateRestoreBackupListView();
        lvRestoreBackupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fileName = adapterView.getItemAtPosition(i).toString();
                restoreData(fileName);
            }
        });
        restoreBackupDialog.show();
    }

    private void populateRestoreBackupListView(){
        String path = Environment.getExternalStorageDirectory()+"/flashcardsplusplus/backup/";
        File backupDirectory = new File(path);
        File[] backupFiles = backupDirectory.listFiles();
        String[] fileNames = new String[backupFiles.length];
        for (int counter = 0; counter < backupFiles.length; counter++){
            fileNames[counter] = backupFiles[counter].getName();
        }
        final ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,fileNames);
        lvRestoreBackupListView.setAdapter(adapter);
    }

    //Popups
    private void renameDeckPopup(String deckName){
        final Dialog renameDialog = new Dialog (this);
        renameDialog.setContentView(R.layout.activity_edit_deck);
        renameDialog.setTitle("Rename Deck");
        etDeckName = renameDialog.findViewById(R.id.deckNameField);
        etDeckName.setText(deckName);
        Button editButton = renameDialog.findViewById(R.id.confirmButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameDeckSaveToDatabase();
            }
        });
        Button cancelButton = renameDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameDialog.dismiss();
            }
        });
        renameDialog.show();
    }

    private void addDeckPopup(){
        final Dialog addDeckDialog = new Dialog(this);
        addDeckDialog.setContentView(R.layout.activity_add_deck);
        addDeckDialog.setTitle("Add a new deck");
        etDeckName = addDeckDialog.findViewById(R.id.deckNameField);
        Button confirmBtn = addDeckDialog.findViewById(R.id.addButton);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDeckSaveToDatabase();
            }
        });
        Button cancelButton = addDeckDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDeckDialog.dismiss();
            }
        });
        addDeckDialog.show();
    }
    //Recycles the Edit deck layout. Will probably make a new layout for it in the future.
    private void backupNamePopup(){
        nameBackupDialog = new Dialog (this);
        nameBackupDialog.setContentView(R.layout.activity_edit_deck);
        nameBackupDialog.setTitle("Backup Name");
        final EditText etBackupNameField  = nameBackupDialog.findViewById(R.id.deckNameField);
        Button editButton = nameBackupDialog.findViewById(R.id.confirmButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackupName(etBackupNameField);
            }
        });

        Button cancelButton = nameBackupDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameBackupDialog.dismiss();
            }
        });
        nameBackupDialog.show();
    }

    private void setBackupName(EditText name) {
        String backupName = name.getText().toString();
        if (backupName.isEmpty()){
            toastMessage("Backup Name is invalid!");
        } else {
            backupData(backupName);
        }
    }

    private void backupData(String fileName){
        String[] files = new String[2];
        zc.outputFolder();
        files[0] = fc.getDbPath(this);
        files[1] = dc.getDbPath(this);
        zc.zip(files,fileName);
        toastMessage("Data backup successful. Backup file is located in "+zc.outputLocation);
        nameBackupDialog.dismiss();

    }


    private void renameDeckSaveToDatabase(){
        String newDeckName = etDeckName.getText().toString();
        if (dc.checkIfDeckNameExists(newDeckName)){
            Boolean confirm = dc.renameDeck(newDeckName, deckId);
            if (confirm){
                toastMessage("Deck was successfully renamed!");
                goToMainActivity();
            } else {
                toastMessage("Deck was not successfully renamed!");
            }
        } else {
            toastMessage("A deck with that name already exists!");
        }
    }

    private void addDeckSaveToDatabase(){
        String newDeck = etDeckName.getText().toString();
        if (dc.checkIfDeckNameExists(newDeck)){
            int id = 111111 + (int) (Math.random() * 999999);
            boolean addData = dc.addData(id,newDeck);
            if (addData){
                toastMessage("Deck Successfully Added!");
                Log.i(TAG, "Deck successfully added!");
                viewItem(id);
            } else {
                toastMessage("Oops! Something went wrong! Deck was not saved!");
                Log.e(TAG, "Deck was not successfully added!");
            }
        } else {
            toastMessage("Deck name already exists!");
        }
    }

    //Makes popup messages. Good for debugging mostly.
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
