package com.treskie.conrad.flashcardsplus.Browser;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Adapter.CardBrowserAdapter;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.Edit.EditCard;
import com.treskie.conrad.flashcardsplus.R;
import com.treskie.conrad.flashcardsplus.Viewer.CardViewer;

import java.util.ArrayList;
import java.util.HashMap;

import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.answerRow;
import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.cardIdCb;
import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.deckIdCb;
import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.questionRow;

public class CardBrowser extends AppCompatActivity {
    private static final String TAG = "CardBrowser";
    private ArrayList<HashMap<String,String>> cardList;
    private FlashCardDatabaseController dc;
    private int deckId;
    private EditText etFirstPart;
    private EditText etSecondPart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dc = new FlashCardDatabaseController(this);
        ListView listView = (ListView) findViewById(R.id.cardBrowserListView);
        cardList = new ArrayList<>();
        deckId = getIdData();
        Cursor data = dc.getData(deckId);

        while (data.moveToNext()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(cardIdCb, data.getString(0));
            temp.put(deckIdCb, data.getString(1));
            temp.put(questionRow, data.getString(2));
            temp.put(answerRow, data.getString(3));
            cardList.add(temp);
        }

        CardBrowserAdapter adapter = new CardBrowserAdapter(this, cardList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                goToEditActivity(Integer.parseInt(cardList.get(position).get(cardIdCb)), Integer.parseInt(cardList.get(position).get(deckIdCb)));
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_card_browser,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onSupportNavigateUp(){
        goBackToCardViewer();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_card:
                addCardPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addCardPopup(){
        final Dialog addCardDialog = new Dialog(this);
        addCardDialog.setContentView(R.layout.activity_add_card);
        addCardDialog.setTitle("Add a new card");
        etFirstPart = addCardDialog.findViewById(R.id.firstPart);
        etSecondPart = addCardDialog.findViewById(R.id.secondPart);
        Button addCardButton = addCardDialog.findViewById(R.id.confirmButton);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCardSaveToDatabase();
            }
        });
        Button cancelButton = addCardDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCardDialog.dismiss();
            }
        });

    }

    private void addCardSaveToDatabase(){
        int id = 111111 + (int) (Math.random() * 999999);
        /*
            Grabs data from the two text fields
            Look into /res/layout/activity_add_card.xml for said text fields
        */
        String firstPart = etFirstPart.getText().toString();
        String secondPart = etSecondPart.getText().toString();
        boolean confirm = dc.addData(id,deckId,firstPart,secondPart);
        if (confirm){
            toastMessage("Card Successfully Added!");
            finish();
        } else {
            toastMessage("Something went wrong!");
            Log.e(TAG,"AddCard: Card was not successfully added! Probably something wrong with the FlashCardDatabaseController!");
        }
    }

    public void goToEditActivity(int card, int deck){
        Intent editCard = new Intent(this, EditCard.class);
        editCard.putExtra("cardId", card);
        editCard.putExtra("deckId", deck);
        startActivity(editCard);
        finish();
    }

    private void goBackToCardViewer(){
        Intent goToCardViewer = new Intent (this, CardViewer.class);
        goToCardViewer.putExtra("deckId", deckId);
        startActivity(goToCardViewer);
        finish();
    }

    public int getIdData() {
        Bundle getDeckId = getIntent().getExtras();
        int value = getDeckId.getInt("deckId");
        return value;
    }

    //Makes popup messages. Good for debugging mostly.
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}

