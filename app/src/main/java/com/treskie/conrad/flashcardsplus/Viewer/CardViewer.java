package com.treskie.conrad.flashcardsplus.Viewer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.Browser.CardBrowser;
import com.treskie.conrad.flashcardsplus.Controller.FlashCardDatabaseController;
import com.treskie.conrad.flashcardsplus.MainActivity;
import com.treskie.conrad.flashcardsplus.R;

import java.util.ArrayList;

public class CardViewer extends AppCompatActivity {

    private FlashCardDatabaseController dc;

    private TextView tvFirstPart;
    private WebView wbSecondPart;

    private ArrayList<String> firstPartArray;
    private ArrayList<String> secondPartArray;

    private GestureDetectorCompat gestureObject;
    private int indexNumber = 0;
    private int idNumber = 0;

    private String sSecondPart;

    private static final String TAG = "CardViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        dc = new FlashCardDatabaseController(this);
        Intent goBackToMainActivity = new Intent(this,MainActivity.class);
        tvFirstPart = (TextView) findViewById(R.id.firstPart);
        wbSecondPart = (WebView) findViewById(R.id.secondPart);
        idNumber = getIdFromMainActivity();
        gestureObject = new GestureDetectorCompat(this, new detectGestureMethod());

        if (idNumber > 0){
            Log.i(TAG,"Deck ID is received by CardViewer.");
            toastMessage(""+idNumber);
            /*
                getCards(idNumber) - gets all cards on deck and puts them in the ArrayList

                getIndexFromPreviousActivity - gets the index number of the ArrayLists to
                keep track of its position

                showCard - obviously shows the current card to the user.

             */
            getCards(idNumber);
            indexNumber = getIndexFromPreviousActivity();
            showCard(indexNumber);
            wbSecondPart.setVisibility(View.INVISIBLE);

        } else {
            Log.d(TAG,"Deck ID was not successfully received by CardViewer!");
            startActivity(goBackToMainActivity);
            finish();
        }
    }

    private int getIdFromMainActivity(){
        Bundle getId = getIntent().getExtras();
        int id = getId.getInt("deckId");
        return id;
    }

    private int getIndexFromPreviousActivity(){
        Bundle getIndex = getIntent().getExtras();
        int index = getIndex.getInt("index");
        return index;
    }

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
            String webDataString = "<center> "+secondPartArray.get(indexNumber)+" </center>";
            sSecondPart = secondPartArray.get(indexNumber);
            tvFirstPart.setText(firstPartArray.get(indexNumber));
            wbSecondPart.loadData(webDataString, "text/html; charset=utf-8", "UTF-8");
        }
    }
    //TODO: Turn goToNextCard and goToPreviousCard into fragments
    public void goToNextCard(){
        Intent nextCard = new Intent(this, CardViewer.class);

        if (indexNumber >= firstPartArray.size() - 1){
            nextCard.putExtra("index", 0);
            nextCard.putExtra("deckId",idNumber);
            startActivity(nextCard);
            finish();
        } else {
            nextCard.putExtra("index", indexNumber + 1);
            nextCard.putExtra("deckId",idNumber);
            startActivity(nextCard);
            finish();
        }
    }

    public void goToPreviousCard(){
        Intent previousCard = new Intent(this, CardViewer.class);
        if (indexNumber == 0){
            toastMessage("You are in the first card!");
        } else {
            previousCard.putExtra("index", indexNumber - 1);
            previousCard.putExtra("deckId",idNumber);
            startActivity(previousCard);
            finish();
        }

    }
    //NOTE: This is temporary.
    public boolean onTouchEvent (MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class detectGestureMethod extends GestureDetector.SimpleOnGestureListener {
        @Override
        //TODO: Replace from_middle and to_middle with better animations.
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            //Swipe right
            if (event2.getX() > event1.getX())
            {
                goToPreviousCard();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
            //swipe left
            else if (event2.getX() < event1.getX())
            {
                goToNextCard();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            wbSecondPart.setVisibility(View.VISIBLE);
            return super.onDoubleTap(e);
        }
    }
    //Top bar menu stuff.
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_card_viewer,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_card_list:
                goToCardList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToCardList(){
        Intent goToCardList = new Intent(this, CardBrowser.class);
        goToCardList.putExtra("deckId", idNumber);
        startActivity(goToCardList);
        finish();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
