package com.treskie.conrad.flashcardsplus.Viewer;

import android.app.Activity;
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
    private TextView tvSecondPart;

    private ArrayList<String> firstPartArray;
    private ArrayList<String> secondPartArray;

    private GestureDetectorCompat gestureObject;
    private int indexNumber = 0;
    private int idNumber = 0;
    private static final String TAG = "CardViewer";
    private Intent goBackToMainActivity;
    private int tapSwitch = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        dc = new FlashCardDatabaseController(this);
        goBackToMainActivity = new Intent(this,MainActivity.class);
        tvFirstPart = (TextView) findViewById(R.id.firstPart);
        tvSecondPart = (TextView) findViewById(R.id.secondPart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idNumber = getIdFromMainActivity();
        gestureObject = new GestureDetectorCompat(this, new detectGestureMethod());
        getCards(idNumber);
        if (idNumber > 0){
            Log.i(TAG,"Deck ID is received by CardViewer. "+idNumber);
            /*
                getCards(idNumber) - gets all cards on deck and puts them in the ArrayList

                getIndexFromPreviousActivity - gets the index number of the ArrayLists to
                keep track of its position

                showCard - obviously shows the current card to the user.

             */
            indexNumber = getIndexFromPreviousActivity();
            showCard(indexNumber);
            tvSecondPart.setVisibility(View.INVISIBLE);

        } else {
            Log.d(TAG,"Deck ID was not successfully received by CardViewer!");
            startActivity(goBackToMainActivity);
            finish();
        }
    }

    public boolean onSupportNavigateUp(){
        Intent goToMain = new Intent (this, MainActivity.class);
        startActivity(goToMain);
        return true;
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
            tvFirstPart.setText(firstPartArray.get(indexNumber));
            tvSecondPart.setText(secondPartArray.get(indexNumber));
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
            previousCard.putExtra("index", firstPartArray.size() - 1);
            previousCard.putExtra("deckId",idNumber);
            startActivity(previousCard);
            finish();
        } else {
            previousCard.putExtra("index", indexNumber - 1);
            previousCard.putExtra("deckId",idNumber);
            startActivity(previousCard);
            finish();
        }
    }

    public void goToSpecificCard(int index){
        Intent startCard = new Intent(this, CardViewer.class);
        startCard.putExtra("index", index);
        startCard.putExtra("deckId",idNumber);
        startActivity(startCard);
        finish();
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
            if (tapSwitch == 0){
                tvSecondPart.setVisibility(View.VISIBLE);
                tapSwitch = 1;
            } else {
                tvSecondPart.setVisibility(View.INVISIBLE);
                tapSwitch = 0;
            }

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

    public void goToCardList(){
        Intent goToCardList = new Intent(this, CardBrowser.class);
        goToCardList.putExtra("deckId", idNumber);
        startActivityForResult(goToCardList, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED){
            goToSpecificCard(0);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
