package com.treskie.conrad.flashcardsplus;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DeckListViewer extends AppCompatActivity {
    ListView lvDeckViewer;
    FlashCardDatabaseController dc;
    int idNumber;
    private static final String TAG = "DeckListViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_viewer);
        lvDeckViewer = (ListView) findViewById(R.id.cardListView);
        dc = new FlashCardDatabaseController(this);
        getDeckInfo();
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

    }

    // Gets the flashcard deck ID number from MainActivity.
    public void getDeckInfo(){
        Bundle getInfo = getIntent().getExtras();
        idNumber = getInfo.getInt("id");
        toastMessage("ID Number is: "+idNumber);
        getCards(idNumber);
    }

    private void getCards(int id) {
        Cursor data = dc.getData(id);
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(data.getString(2));
        }
        final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        lvDeckViewer.setAdapter(adapter);
        lvDeckViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    /*  When activity_add_card is clicked it takes the ID number of the deck to serve as the
        identifier number for the card to know which deck said card belongs to.
        (see FlashCardDatabaseController)
     */
    private void goToAddCardActivity(){
        Intent addCardInfo = new Intent (this, AddCard.class);
        addCardInfo.putExtra("deckId",idNumber);
        startActivity(addCardInfo);
        finish();
    }

    private void goToCardViewer(){
        Intent goToCardViewer = new Intent (this, CardViewer.class);
        goToCardViewer.putExtra("deckId",idNumber);
        startActivity(goToCardViewer);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    //Top bar menu stuff.
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_card,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_card:
                goToAddCardActivity();
                return true;
            case R.id.action_help:
                goToCardViewer();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
