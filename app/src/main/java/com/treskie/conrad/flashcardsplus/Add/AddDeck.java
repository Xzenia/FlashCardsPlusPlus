package com.treskie.conrad.flashcardsplus.Add;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Controller.DeckDatabaseController;
import com.treskie.conrad.flashcardsplus.MainActivity;
import com.treskie.conrad.flashcardsplus.R;

public class AddDeck extends AppCompatActivity {
    private EditText mDeckName;
    private DeckDatabaseController db;
    private static final String TAG = "AddDeck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDeckName = (EditText) findViewById(R.id.deckNameField);
        db = new DeckDatabaseController(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void addDeckMethod(View view){
        Intent goToMainActivity = new Intent(this, MainActivity.class);
        String sProductName = mDeckName.getText().toString();
        int id = 111111 + (int) (Math.random() * 999999);

        if (db.checkIfDeckNameExists(sProductName)){
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
        } else {
            toastMessage("Deck name already exists!");
        }
    }

    public boolean onSupportNavigateUp(){
        MainActivity mc = new MainActivity();
        mc.goToMainActivity();
        return true;
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}
