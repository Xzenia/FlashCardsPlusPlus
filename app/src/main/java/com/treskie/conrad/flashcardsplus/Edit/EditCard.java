package com.treskie.conrad.flashcardsplus.Edit;

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

import com.treskie.conrad.flashcardsplus.Browser.CardBrowser;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.R;

public class EditCard extends AppCompatActivity {
    private EditText etFirstPart;
    private EditText etSecondPart;
    private String oldFirstPart;
    private String oldSecondPart;
    private FlashCardDatabaseController dc;
    private int cardId;
    private int deckId;
    private static final String TAG = "EditCard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etFirstPart = (EditText) findViewById(R.id.firstPart);
        etSecondPart = (EditText) findViewById(R.id.secondPart);
        dc = new FlashCardDatabaseController(this);
        setInitialValues();
    }

    private void setInitialValues(){
        Bundle getInitialInfo = getIntent().getExtras();
        deckId = getInitialInfo.getInt("deckId");
        cardId = getInitialInfo.getInt("cardId");
        Cursor data = dc.getSpecificCard(cardId);
        while (data.moveToNext()) {
            oldFirstPart = data.getString(2);
            oldSecondPart = data.getString(3);
        }
        etFirstPart.setText(oldFirstPart);
        etSecondPart.setText(oldSecondPart);
    }

    public boolean onSupportNavigateUp(){
        goBackToCardBrowser();
        return true;
    }

    public void saveToDatabase(View v) {
        Intent goToCardViewer = new Intent(this, CardBrowser.class);
        String newFirstPart = etFirstPart.getText().toString();
        String newSecondPart = etSecondPart.getText().toString();
        if (oldFirstPart.equals(newFirstPart) && oldSecondPart.equals(newSecondPart)) {
            toastMessage("One or both fields are unchanged!");
        } else {
            dc.setCardId(cardId);
            dc.setDeckIdentifier(deckId);
            dc.setFirstPart(newFirstPart);
            dc.setSecondPart(newSecondPart);
            boolean editData = dc.updateCard();
            if (editData == true) {
                toastMessage("Card successfully edited!");
                goToCardViewer.putExtra("deckId", deckId);
                startActivity(goToCardViewer);
                finish();
            } else {
                toastMessage("Oops! Something went wrong!");
                Log.e(TAG, "Card was not successfully edited!");
            }
        }
    }

    //Top bar menu stuff.
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_edit_deck,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete_card:
                deleteCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCard(){
        Boolean confirmDeletion = dc.deleteCard(cardId,deckId);
        if (confirmDeletion == true){
            toastMessage("Card successfully deleted!");
            goBackToCardBrowser();
        } else {
            toastMessage("Card deletion error!!!");
        }
    }

    private void goBackToCardBrowser(){
        Intent cardBrowserIntent = new Intent (this, CardBrowser.class);
        cardBrowserIntent.putExtra("deckId", deckId);
        startActivity(cardBrowserIntent);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
