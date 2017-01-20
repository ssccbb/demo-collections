package com.sung.demo.democollections.CalendarSign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sung.demo.democollections.R;

public class CalendarSign extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarsign_sign_activity);

        initView();
    }

    private void initView(){
        Button list= (Button) findViewById(R.id.list);
        Button scroll= (Button) findViewById(R.id.scroll);
        list.setOnClickListener(this);
        scroll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.list:
                startActivity(new Intent(CalendarSign.this,CalendarListViewActivity.class));
                break;
            case R.id.scroll:
                startActivity(new Intent(CalendarSign.this,CalendarScrollViewActivity.class));
                break;
        }
    }
}
