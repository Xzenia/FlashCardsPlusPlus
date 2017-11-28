package com.treskie.conrad.flashcardsplus;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DeckDatabaseController deckController;
    private FlashCardDatabaseController flashCardController;
    private ListView lvListView;
    private String deckName = "";
    private int deckId = 0;

    //Popups
    private EditText etDeckName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvListView = (ListView) findViewById(R.id.listView);
        deckController = new DeckDatabaseController(this);
        flashCardController = new FlashCardDatabaseController(this);
        populateListView();
        registerForContextMenu(lvListView);
    }

    private void populateListView(){
        Cursor data = deckController.getData();
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
        Cursor data = deckController.getIdData(deckName);
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
        finish();
        deckController.deleteDeck(deckId);
        flashCardController.deleteCardsByDeck(deckId);
        goToMainActivity();
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
                backupData();
                return true;
            case R.id.action_restore_data:
                restoreData();
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
    //TODO: Backups should be zipped up.
    private void backupData() {
        boolean confirmDeckExport = deckController.exportDeckData(this);
        boolean confirmCardExport = flashCardController.exportCardData(this);
        if (confirmDeckExport && confirmCardExport) {
            toastMessage("Deck and Card DB export successful!!!");
        } else if (!confirmDeckExport) {
            toastMessage("Deck DB is missing! Please copy the backup into your sdcard and try again!");
            Log.e(TAG, "Deck DB failed to export!!!");
        } else if (!confirmCardExport) {
            toastMessage("Card DB is missing! Please copy the backup into your sdcard and try again! ");
            Log.e(TAG, "Card DB failed to export!!!");
        } else {
            toastMessage("Both card and deck DB are missing. Please copy the backup into your sdcard and try again!");
            Log.e(TAG, "All else-if cases have failed in backupData()!");
        }
    }

    private void restoreData() {
        boolean confirmDeckImport = deckController.importDeckData(this);
        boolean confirmCardImport = flashCardController.importCardData(this);
        if (confirmDeckImport && confirmCardImport) {
            toastMessage("Deck and Card DB import successful!!!");
            goToMainActivity();
        } else if (!confirmDeckImport && confirmCardImport) {
            toastMessage("Deck DB import failed but Card import was successful!!!");
            Log.e(TAG, "Deck DB failed to import!!!");
        } else if (!confirmCardImport && confirmDeckImport) {
            toastMessage("Card DB import failed! but Deck import was successful!!");
            Log.e(TAG, "Card DB import to export!!!");
        } else {
            toastMessage("Unknown error occurred!!!");
            Log.e(TAG, "All else-if cases have failed in backupData()!");
        }
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

    private void renameDeckSaveToDatabase(){
        String newDeckName = etDeckName.getText().toString();
        if (deckController.checkIfDeckNameExists(newDeckName)){
            Boolean confirm = deckController.renameDeck(newDeckName, deckId);
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
        if (deckController.checkIfDeckNameExists(newDeck)){
            int id = 111111 + (int) (Math.random() * 999999);
            boolean addData = deckController.addData(id,newDeck);
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
