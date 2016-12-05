package com.sung.demo.democollections.PcitrueTAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;

public class ImageBrowsing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browsing);
        getSupportActionBar().hide();
        getBitmap();
    }

    private void getBitmap(){
        Bitmap b = (Bitmap) this.getIntent().getParcelableExtra("data");

        ImageView img= (ImageView) findViewById(R.id.image);
        img.setImageBitmap(b);
    }
}
