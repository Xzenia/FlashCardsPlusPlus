package com.treskie.conrad.flashcardsplus.Browser;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.treskie.conrad.flashcardsplus.Adapter.CardBrowserAdapter;
import com.treskie.conrad.flashcardsplus.Add.AddCard;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.answerRow;
import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.questionRow;

public class CardBrowser extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> cardList;
    private FlashCardDatabaseController dc;
    int deckId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);
        dc = new FlashCardDatabaseController(this);
        ListView listView = (ListView) findViewById(R.id.cardBrowserListView);
        cardList = new ArrayList<HashMap<String, String>>();
        deckId = getIdData();
        Cursor data = dc.getData(deckId);

        while (data.moveToNext()) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(questionRow, data.getString(2));
            temp.put(answerRow, data.getString(3));
            cardList.add(temp);
        }

        CardBrowserAdapter adapter = new CardBrowserAdapter(this, cardList);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_card_browser,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_card:
                goToAddCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToAddCard(){
        Intent addCard = new Intent(this, AddCard.class);
        addCard.putExtra("deckId",deckId);
        startActivity(addCard);
        finish();
    }

    public int getIdData() {
        Bundle getDeckId = getIntent().getExtras();
        int value = getDeckId.getInt("deckId");
        return value;
    }
}

