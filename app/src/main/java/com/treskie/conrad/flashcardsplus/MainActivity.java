package com.treskie.conrad.flashcardsplus;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.treskie.conrad.flashcardsplus.Add.AddDeck;
import com.treskie.conrad.flashcardsplus.Controller.DeckDatabaseController;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.Viewer.CardViewer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DeckDatabaseController deckController;
    FlashCardDatabaseController flashCardController;
    private ListView lvListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvListView = (ListView) findViewById(R.id.listView);
        deckController = new DeckDatabaseController(this);
        flashCardController = new FlashCardDatabaseController(this);
        populateListView();
    }

    public void goToAddProductActivity(){
        Intent addProductActivity = new Intent(this, AddDeck.class);
        startActivity(addProductActivity);
        finish();
    }

    private void populateListView(){
        Cursor data = deckController.getData();
        ArrayList<String> listData = new ArrayList<>();
        //goes through all the entries in the deck database and adds them to the ArrayList
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }
        //  Note: simple_list_item_1 is a list layout that only displays one line of text per entry
        final ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        lvListView.setAdapter(adapter);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String deckName = adapterView.getItemAtPosition(i).toString();
                int deckId = 0;
                Log.d(TAG, "onItemClick: User clicked on "+deckName);
                Cursor data = deckController.getIdData(deckName);
                while (data.moveToNext()){
                    deckId = data.getInt(0);
                }
                if (deckId > 0){
                    Log.d(TAG, "onItemClick: The Deck ID is: " + deckId);
                    viewItem(deckId);
                } else {
                    Log.e(TAG, "onItemClick: The Deck ID is: " + deckId);
                    toastMessage("Deck is invalid or does not exist!");
                }
            }
        });
    }

    public void viewItem(int id){
        Intent goToEditActivity = new Intent (this, CardViewer.class);
        goToEditActivity.putExtra("deckId",id);
        startActivity(goToEditActivity);
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
                goToAddProductActivity();
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
    //TODO: Backups should be zipped up.
    private void backupData() {
        boolean confirmDeckExport = deckController.exportDeckData(this);
        boolean confirmCardExport = flashCardController.exportCardData(this);
        if (confirmDeckExport && confirmCardExport) {
            toastMessage("Deck and Card DB export successful!!!");
        } else if (!confirmDeckExport) {
            toastMessage("Deck DB export failed!!!");
            Log.e(TAG, "Deck DB failed to export!!!");
        } else if (!confirmCardExport) {
            toastMessage("Card DB export failed!!!");
            Log.e(TAG, "Card DB failed to export!!!");
        } else {
            toastMessage("Unknown error occurred!!!");
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
            toastMessage("Deck DB import failed!!!");
            Log.e(TAG, "Deck DB failed to import!!!");
        } else if (!confirmCardImport && confirmDeckImport) {
            toastMessage("Card DB import failed!!!");
            Log.e(TAG, "Card DB import to export!!!");
        } else {
            toastMessage("Unknown error occurred!!!");
            Log.e(TAG, "All else-if cases have failed in backupData()!");
        }
    }

    private void goToMainActivity(){
        Intent goToMain = new Intent (this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    //Makes popup messages. Good for debugging mostly.
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
