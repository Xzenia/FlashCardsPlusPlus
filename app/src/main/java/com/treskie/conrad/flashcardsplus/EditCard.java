package com.treskie.conrad.flashcardsplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class EditCard extends AppCompatActivity {
    EditText etFirstPart;
    EditText etSecondPart;
    int deckIdNumber;
    String oldFirstPart;
    String oldSecondPart;
    FlashCardDatabaseController dc;
    private static final String TAG = "EditCard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        etFirstPart = (EditText) findViewById(R.id.firstPart);
        etSecondPart = (EditText) findViewById(R.id.secondPart);
        dc = new FlashCardDatabaseController(this);
        setInitialValues();
    }

    private void setInitialValues(){
        Bundle getInitialInfo = getIntent().getExtras();
        deckIdNumber = getInitialInfo.getInt("deckId");
        oldFirstPart = getInitialInfo.getString("firstPart");
        oldSecondPart = getInitialInfo.getString("secondPart");
        etFirstPart.setText(oldFirstPart);
        etSecondPart.setText(oldSecondPart);
    }

    public void saveToDatabase(View v) {
        Intent goToCardViewer = new Intent(this, CardViewer.class);

        String newFirstPart = etFirstPart.getText().toString();
        String newSecondPart = etSecondPart.getText().toString();

        if (oldFirstPart.equals(newFirstPart) && oldSecondPart.equals(newSecondPart)) {
            toastMessage("One or both fields are unchanged!");
        } else {
            boolean editData = dc.updateCard(oldFirstPart, oldSecondPart, newFirstPart, newSecondPart,deckIdNumber);
            if (editData == true) {
                toastMessage("Card successfully edited!");
                Log.i(TAG, "Deck successfully edited!");
                goToCardViewer.putExtra("deckId",deckIdNumber);
                startActivity(goToCardViewer);
                finish();
            } else {
                toastMessage("Oops! Something went wrong!");
                Log.e(TAG, "Card was not successfully edited!");
            }
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
