package com.treskie.conrad.flashcardsplus.Add;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.treskie.conrad.flashcardsplus.R;

import java.io.InputStream;


public class AddImage extends AppCompatActivity {

    private Context mContext;
    private WebView wbImageWebView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        mContext = getApplicationContext();
        wbImageWebView = (WebView) findViewById(R.id.imageWebView);
    }

    public void addImageFromGallery(View v){
        Intent chooseImage = new Intent(Intent.ACTION_GET_CONTENT);
        chooseImage.setType("image/*");
        startActivityForResult(chooseImage, 0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImage = data.getData();

        } else{
            toastMessage("If else statement failed!!");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
