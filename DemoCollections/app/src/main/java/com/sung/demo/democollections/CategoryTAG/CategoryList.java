package com.sung.demo.democollections.CategoryTAG;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sung.demo.democollections.MainActivity.SodukuListView;
import com.sung.demo.democollections.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 *
 * 层级的列表展示分类
 */
public class CategoryList extends android.app.Fragment {
    private String TAG="CategoryList";
    private View contentView;
    private ArrayList<HashMap<String,Object>> listData1,listData2,listData3;
    private LinearLayout container1,container2,container3;

    public CategoryList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_category_list, container, false);
        initUI();
        getList1Category();
        return contentView;
    }

    private void initUI(){
        container1 = (LinearLayout) contentView.findViewById(R.id.container1);
        container2= (LinearLayout) contentView.findViewById(R.id.container2);
        container3= (LinearLayout) contentView.findViewById(R.id.container3);

        changeWith(1);
    }

    /*
    * listview 1
    * */
    private void getList1Category(){
        listData1=new ArrayList<>();
        for (int i=0;i<5;i++){
            HashMap<String,Object> map=new HashMap<>();
            map.put("id",i);
            map.put("text","first"+i);
            listData1.add(map);
        }

        initList1();
    }

    private void initList1(){
        final SodukuListView list1= (SodukuListView) contentView.findViewById(R.id.list1);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < listData1.size(); i++) {
            String text = (String) listData1.get(i).get("text");
            strings.add(text);
        }
        list1.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.single_list_text,R.id.text,strings));
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getList2Category((int)listData1.get(i).get("id"));
            }
        });
    }

    /*
    * listview 2
    * */
    private void getList2Category(int id){
        //Toast.makeText(getActivity(), id+"", Toast.LENGTH_SHORT).show();
        listData2=new ArrayList<>();
        for (int i=0;i<22;i++){
            HashMap<String,Object> map=new HashMap<>();
            map.put("id",i);
            map.put("text","second"+i);
            listData2.add(map);
        }

        changeWith(2);
        initList2();
    }

    private void initList2(){
        SodukuListView list2= (SodukuListView) contentView.findViewById(R.id.list2);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < listData2.size(); i++) {
            String text = (String) listData2.get(i).get("text");
            strings.add(text);
        }
        list2.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.single_list_text,R.id.text,strings));
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getList3Category((int)listData2.get(i).get("id"));
            }
        });
    }

    /*
    * listview 3
    * */
    private void getList3Category(int id){
        //Toast.makeText(getActivity(), id+"", Toast.LENGTH_SHORT).show();
        listData3=new ArrayList<>();
        for (int i=0;i<33;i++){
            HashMap<String,Object> map=new HashMap<>();
            map.put("id",i);
            map.put("text","third"+i);
            listData3.add(map);
        }

        changeWith(3);
        initList3();
    }

    private void initList3(){
        SodukuListView list3= (SodukuListView) contentView.findViewById(R.id.list3);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < listData3.size(); i++) {
            String text = (String) listData3.get(i).get("text");
            strings.add(text);
        }
        list3.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.single_list_text,R.id.text,strings));
        list3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), (String)listData3.get(i).get("text"), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    private int getHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return height;
    }

    private int getWith(){
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        return width;
    }

    /*
    * 改变层级宽度
    * */
    private void changeWith(int flag){
        LinearLayout.LayoutParams fullLp=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams zeroLp=new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams halfLp=new LinearLayout.LayoutParams(
                getWith()/2, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams thirdLp=new LinearLayout.LayoutParams(
                getWith()/3, LinearLayout.LayoutParams.MATCH_PARENT);

        container1.setLayoutParams(zeroLp);
        container2.setLayoutParams(zeroLp);
        container3.setLayoutParams(zeroLp);

        switch (flag){
            case 1:
                container1.setLayoutParams(fullLp);
                break;
            case 2:
                container1.setLayoutParams(halfLp);
                container2.setLayoutParams(halfLp);
                break;
            case 3:
                container1.setLayoutParams(thirdLp);
                container2.setLayoutParams(thirdLp);
                container3.setLayoutParams(thirdLp);
                break;
        }
    }
}
