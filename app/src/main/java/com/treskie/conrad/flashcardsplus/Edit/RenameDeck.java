package com.treskie.conrad.flashcardsplus.Edit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Controller.DeckDatabaseController;
import com.treskie.conrad.flashcardsplus.MainActivity;
import com.treskie.conrad.flashcardsplus.R;


public class RenameDeck extends AppCompatActivity {

    DeckDatabaseController dc;
    TextView tvDeckName;
    private int deckId;
    private String deckName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);
        dc = new DeckDatabaseController(this);
        tvDeckName = (TextView) findViewById(R.id.deckNameField);
        Bundle getDeckName = getIntent().getExtras();
        deckId = getDeckName.getInt("deckId");
        deckName = getDeckName.getString("deckName");
        tvDeckName.setText(deckName);
    }

    public void saveToDatabase(View v){
        String newDeckName = tvDeckName.getText().toString();
        if (dc.checkIfDeckNameExists(newDeckName)){
            Boolean confirm = dc.renameDeck(newDeckName, deckId);
            Intent goBackToMain = new Intent(this, MainActivity.class);
            if (confirm){
                toastMessage("Deck successfully renamed!");
            } else {
                toastMessage("Deck was not renamed!");
            }
            startActivity(goBackToMain);
            finish();
        } else {
            toastMessage("Deck name already exists!");
        }
    }

    //Makes popup messages. Good for debugging mostly.
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
