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


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DeckDatabaseController mDB;
    private ListView lvListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvListView = (ListView) findViewById(R.id.listView);
        mDB = new DeckDatabaseController(this);
        populateListView();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_deck,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_deck:
                goToAddProductActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToAddProductActivity(){
        Intent addProductActivity = new Intent(this, AddDeck.class);
        startActivity(addProductActivity);
        finish();
    }

    private void populateListView(){
        Cursor data = mDB.getData();
        ArrayList<String> listData = new ArrayList<>();
        //goes through all the entries in the deck database and adds them to the ArrayList

        while(data.moveToNext()){
            listData.add(data.getString(1));
        }

        //  Note: simple_list_item_1 is a list layout that only displays one line of text per entry
        //  TODO: Change the layout to accommodate more information
        final ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        lvListView.setAdapter(adapter);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String deckName = adapterView.getItemAtPosition(i).toString();
                int deckId = 0;
                Log.d(TAG, "onItemClick: User clicked on "+deckName);
                Cursor data = mDB.getIdData(deckName);
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
        goToEditActivity.putExtra("id",id);
        startActivity(goToEditActivity);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
