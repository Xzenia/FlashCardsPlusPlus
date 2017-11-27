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
        String web = "<html> <body> <img src = \"file://storage/emulated/0/Pictures/Screenshots/Screenshot_2017-03-31-13-15-19.png\"/> </body> </html>";
        wbImageWebView.loadData(web,"text/html; charset=utf-8", "UTF-8");
    }

    public void addImageFromGallery(View v){
        Intent chooseImage = new Intent(Intent.ACTION_GET_CONTENT);
        chooseImage.setType("image/*");
        startActivityForResult(chooseImage, 0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String realPath;
            // SDK < API11
            //realPath = getRealPathFromURI_BelowAPI11(this, data.getData());
            //toastMessage(realPath);

        }

    }


    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
