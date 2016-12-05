package com.sung.demo.democollections.CategoryTAG;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;

public class CategoryTag extends SungActivity {
    private String TAG="CategoryTag";
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tag);

        initUI();
    }

    private void initUI(){
        getSupportActionBar().hide();

        container = (LinearLayout) findViewById(R.id.kind_container);

        for (int i=0;i<20;i++){
            creatSingleItem("item",i);
        }
    }

    private void creatSingleItem(String text,int flag){
        View inflate = getLayoutInflater().inflate(R.layout.single_text, null);
        TextView item= (TextView) inflate.findViewById(R.id.text);
        item.setText(text+flag);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryList();
            }
        });

        container.addView(inflate);
    }

    private void showCategoryList(){
        startActivity(new Intent(CategoryTag.this,PopwindowView.class)
            .putExtra("HEIGHT",
                    getSupportActionBar().getHeight()+getStatusBarHeight()+container.getMeasuredHeight()));
    }

    /*
    * 获取状态栏高度
    * */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
