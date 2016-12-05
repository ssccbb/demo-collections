package com.sung.demo.democollections.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.sung.demo.democollections.App;
import com.sung.demo.democollections.CategoryTAG.CategoryTag;
import com.sung.demo.democollections.IMChat.view.ChatLogin;
import com.sung.demo.democollections.PcitrueTAG.AddPictruieTag;
import com.sung.demo.democollections.R;
import com.sung.demo.democollections.MainActivity.RCView.RcViewAdapter;
import com.sung.demo.democollections.MainActivity.RCView.RcViewDecoration;
import com.sung.demo.democollections.ShortVedioRecord.ShortVedioRecord;
import com.sung.demo.democollections.VideoAndBarrage.VideoPlay;

/*
*   demo list
*
* */
public class CollectionList extends SungActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RcViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_view_layout);

        getSupportActionBar().hide();
        App.getInstance();

        initRcView();
        //创建并设置Adapter
        mAdapter = new RcViewAdapter(getDummyDatas());
        mRecyclerView.setAdapter(mAdapter);
        //这句就是添加我们自定义的分隔线
        mRecyclerView.addItemDecoration(
                new RcViewDecoration(this, RcViewDecoration.VERTICAL_LIST));
        mAdapter.setOnItemClickListener(new RcViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data, int position) {
                switch (position){
                    case 0:
                        startActivity(new Intent(CollectionList.this, AddPictruieTag.class));
                        break;
                    case 1:
                        startActivity(new Intent(CollectionList.this, VideoPlay.class));
                        break;
                    case 2:
                        startActivity(new Intent(CollectionList.this, CategoryTag.class));
                        break;
                    case 3:
                        startActivity(new Intent(CollectionList.this, ChatLogin.class));
                        break;
                    case 4:
                        startActivity(new Intent(CollectionList.this, ShortVedioRecord.class));
                        break;
                    default:
                        Toast.makeText(CollectionList.this, data, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initRcView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
    }

    private String[] getDummyDatas(){
        return new String[]{"ADD PICTRUE TAG",
                "VIDEO & BARRAGE",
                "CATEGORY TAG LIST",
                "IM CHAT",
                "SHORT VEDIO RECORD"};
    }
}
