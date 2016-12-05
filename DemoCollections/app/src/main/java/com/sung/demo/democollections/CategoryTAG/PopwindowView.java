package com.sung.demo.democollections.CategoryTAG;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;

public class PopwindowView extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow_view_normal);
        initUI();
    }

    private void initUI(){
        int height = this.getIntent().getIntExtra("HEIGHT", 0);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width=getWith();

        if (height!=0)
            height=getHeight()-height;

        p.height=height;
        p.dimAmount=0.0f;
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.BOTTOM);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        CategoryList categoryList = new CategoryList();
        ft.add(R.id.container,categoryList);
        ft.show(categoryList);
        ft.commit();

        LinearLayout space= (LinearLayout) findViewById(R.id.space);
        space.setOnClickListener(this);
    }

    private int getHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return height;
    }

    private int getWith(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        return width;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.space:
                finish();
                break;
        }
    }
}
