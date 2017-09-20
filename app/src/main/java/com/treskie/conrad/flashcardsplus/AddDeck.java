package com.treskie.conrad.flashcardsplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddDeck extends AppCompatActivity {
    EditText mDeckName;
    DeckDatabaseController db;
    private static final String TAG = "AddDeck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck);
        mDeckName = (EditText) findViewById(R.id.deckNameField);
        db = new DeckDatabaseController(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void addDeckMethod(View view){
        Intent goToMainActivity = new Intent(this, MainActivity.class);
        String sProductName = mDeckName.getText().toString();
        int id = 111111 + (int) (Math.random() * 999999);
        boolean addData = db.addData(id,sProductName);

        if (addData == true){
            toastMessage("Deck Successfully Added!");
            Log.i(TAG, "Deck successfully added!");
            startActivity(goToMainActivity);
            finish();

        } else {
            toastMessage("Oops! Something went wrong!");
            Log.e(TAG, "Deck was not successfully added!");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}
