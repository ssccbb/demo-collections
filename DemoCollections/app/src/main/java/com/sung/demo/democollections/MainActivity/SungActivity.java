package com.sung.demo.democollections.MainActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sung.demo.democollections.R;

import java.util.zip.Inflater;

/**
 * Created by sung on 2016/11/14.
 */

public abstract class SungActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View basic_view = getLayoutInflater().inflate(layoutResID, null);

        //change activity background color
        basic_view.setBackgroundColor(getResources().getColor(R.color.white_normal_bg));

        setContentView(basic_view);
    }
}
