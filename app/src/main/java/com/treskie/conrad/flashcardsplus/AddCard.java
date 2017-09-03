package com.treskie.conrad.flashcardsplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddCard extends AppCompatActivity {
    EditText mFirstPart;
    EditText mSecondPart;
    FlashCardDatabaseController dc;
    private static final String TAG = "AddCard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        mFirstPart = (EditText) findViewById(R.id.firstPart);
        mSecondPart = (EditText) findViewById(R.id.secondPart);
        dc = new FlashCardDatabaseController(this);


    }
    public void saveToDatabase(View v){
        Intent goToDeckViewer = new Intent(this, MainActivity.class);

        //get ID number to identify what deck the card is part of.
        Bundle getInfo = getIntent().getExtras();
        int getDeckId = getInfo.getInt("deckId");

        //generates an individual id number for the card. Might remove this soon.
        int id = 111111 + (int) (Math.random() * 999999);

        /*
            Grabs data from the two text fields
            Look into /res/layout/activity_add_card.xml for said text fields
        */
        String firstPart = mFirstPart.getText().toString();
        String secondPart = mSecondPart.getText().toString();

        boolean confirm = dc.addData(id,getDeckId,firstPart,secondPart);

        if (confirm == true){
            toastMessage("Card Successfully Added!");
            startActivity(goToDeckViewer);
            finish();
        } else {
            toastMessage("Something went wrong!");
            Log.e(TAG,"AddCard: Card was not successfully added! Probably something wrong with the FlashCardDatabaseController!");
        }
        startActivity(goToDeckViewer);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }




    

}
