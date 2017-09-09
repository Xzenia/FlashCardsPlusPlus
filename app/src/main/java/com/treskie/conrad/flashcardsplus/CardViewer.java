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
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class CardViewer extends AppCompatActivity {

    FlashCardDatabaseController dc;

    EditText etFirstPart;
    EditText etSecondPart;

    ArrayList<String> firstPartArray;
    ArrayList<String> secondPartArray;

    int indexNumber = 0;
    int idNumber = 0;

    private static final String TAG = "CardViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        dc = new FlashCardDatabaseController(this);
        Intent goBackToMainActivity = new Intent(this,MainActivity.class);
        etFirstPart = (EditText) findViewById(R.id.firstPart);
        etSecondPart = (EditText) findViewById(R.id.secondPart);

        idNumber = getIdFromMainActivity();
        if (idNumber > 0){
            Log.i(TAG,"Deck ID is received by CardViewer.");
            /*
                getCards(idNumber) - gets all cards on deck and puts them in the ArrayList

                getIndexFromPreviousActivity - gets the index number of the ArrayLists to
                keep track of its position

                showCard - obviously shows the current card to the user.

             */
            getCards(idNumber);
            indexNumber = getIndexFromPreviousActivity();
            showCard(indexNumber);

        } else {
            Log.d(TAG,"Deck ID was not successfully received by CardViewer!");
            startActivity(goBackToMainActivity);
            finish();
        }

    }

    private int getIdFromMainActivity(){
        Bundle getId = getIntent().getExtras();
        int id = getId.getInt("id");
        return id;
    }

    private int getIndexFromPreviousActivity(){
        Bundle getIndex = getIntent().getExtras();
        int index = getIndex.getInt("index");
        return index;
    }

    //TODO: Find a better way that doesn't involve ArrayLists
    private void getCards(int id){
        Cursor data = dc.getData(id);
        firstPartArray = new ArrayList<>();
        secondPartArray = new ArrayList<>();
        while (data.moveToNext()) {
            firstPartArray.add(data.getString(2));
            secondPartArray.add(data.getString(3));
        }
    }

    private void showCard(int indexNumber){
        if (firstPartArray.isEmpty()){
            toastMessage("There are no cards in deck!");
        } else {
            etFirstPart.setText(firstPartArray.get(indexNumber));
            etSecondPart.setText(secondPartArray.get(indexNumber));
        }

    }

    public void goToNextCard(View v){
        Intent goToNextCard = new Intent(this, CardViewer.class);

        if (indexNumber >= firstPartArray.size()-1){
            toastMessage("This is the end...");
        } else {
            goToNextCard.putExtra("index", indexNumber + 1);
            goToNextCard.putExtra("id",idNumber);
            startActivity(goToNextCard);
            finish();
        }
    }

    public void goToPreviousCard(View v){
        Intent goToNextCard = new Intent(this, CardViewer.class);
        if (indexNumber == 0){
            toastMessage("You are in the first card!");
        } else {
            goToNextCard.putExtra("index", indexNumber - 1);
            goToNextCard.putExtra("id",idNumber);
            startActivity(goToNextCard);
            finish();
        }

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToAddCardActivity(){
        Intent goToAddCard = new Intent(this, AddCard.class);
        goToAddCard.putExtra("deckId",idNumber);
        startActivity(goToAddCard);
        finish();
    }

    //Makes popup messages. Good for debugging mostly.
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
