package com.treskie.conrad.flashcardsplus.Browser;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Adapter.CardBrowserAdapter;
import com.treskie.conrad.flashcardsplus.Add.AddCard;
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
    private ArrayList<HashMap<String,String>> cardList;
    private FlashCardDatabaseController dc;
    private int deckId;
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

}

