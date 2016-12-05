package com.sung.demo.democollections.PcitrueTAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.PcitrueTAG.PicUtils.AddTag2Pic;
import com.sung.demo.democollections.PcitrueTAG.PicUtils.Readbitmap;
import com.sung.demo.democollections.R;

import java.util.ArrayList;
import java.util.logging.Handler;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class AddPictruieTag extends SungActivity implements View.OnClickListener{
    private String TAG="AddPictruieTag";
    private ImageView originalImage,waterMarkImage;//原图&水印后图
    private TextView addText,addTime,progressText;//添加文字&添加时间戳&进度提示文字
    private EditText waterText;//文字
    private Button choosePic;
    private ScrollView hintScroll;

    private Bitmap originalBitmap,waterMarkBitmap;

    private static final int MUTIL_IMAGE_CHOOSE=0,WATERMARK_TEXT=0,WATERMARK_TIME=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictruie_tag);
        init();
    }

    private void init(){
        getSupportActionBar().hide();
        initView();
        initClick();
    }

    private void initView(){
        originalImage = (ImageView) findViewById(R.id.origomal_pic);
        waterMarkImage = (ImageView) findViewById(R.id.water_mark_pic);
        choosePic = (Button) findViewById(R.id.choose_pic);
        addText = (TextView) findViewById(R.id.add_text);
        addTime = (TextView) findViewById(R.id.add_time);
        waterText = (EditText) findViewById(R.id.water_mark_text);
        progressText = (TextView) findViewById(R.id.progress_text);
        hintScroll = (ScrollView) findViewById(R.id.hint_scroll);
    }

    private void initClick(){
        choosePic.setOnClickListener(this);
        addText.setOnClickListener(this);
        addTime.setOnClickListener(this);
        waterMarkImage.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MUTIL_IMAGE_CHOOSE:
                if (resultCode == this.RESULT_OK) {
                    // 获取返回的图片列表
                    ArrayList<String> choosePicPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    // 处理你自己的逻辑 ....
                    Log.e(TAG, "onActivityResult: "+choosePicPath.get(0));
                    originalBitmap = Readbitmap.returnLocalBitmap(choosePicPath.get(0));
                    setOriginalImage(originalBitmap);
                }else {
                    progressText.append("\n获取图片失败！");
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choose_pic:
                startActivityForResult(new Intent(AddPictruieTag.this, MultiImageSelectorActivity.class)
                        .putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false)//相机拍照
                        .putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9)//最大选择数量
                        .putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE)//选择模式
                        ,MUTIL_IMAGE_CHOOSE);
                break;
            case R.id.add_text:
                resetImage(waterMarkImage);
                createWaterMarkImage(WATERMARK_TEXT);
                waterText.setText("");
                break;
            case R.id.add_time:
                resetImage(waterMarkImage);
                createWaterMarkImage(WATERMARK_TIME);
                waterText.setText("");
                break;
            case R.id.water_mark_pic:
                if (waterMarkBitmap!=null)
                    startActivity(new Intent(AddPictruieTag.this,ImageBrowsing.class).putExtra("data",waterMarkBitmap));
                else
                    Log.e(TAG, "water mark image null!");
                break;
            default:
                Toast.makeText(this, "onclick-"+view.getId(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setOriginalImage(Bitmap b){
        resetImage(originalImage);
        originalImage.setImageBitmap(b);
    }

    private void createWaterMarkImage(int type){
        waterText.clearFocus();
        switch (type){
            case WATERMARK_TEXT:
                progressText.append("\n获取水印文字...");
                String str = waterText.getText().toString().trim();
                if (str==null||str.length()==0) {
                    new AlertDialog.Builder(this).setMessage("水印文字必填！").create().show();
                    break;
                }

                progressText.append("\n生成水印图片...");
                if (originalBitmap!=null) {
                    waterMarkBitmap = AddTag2Pic.drawTextToBitmap(this, originalBitmap, str);
                    progressText.setText("\n水印添加成功！");
                }

                break;
            case WATERMARK_TIME:
                progressText.append("\n获取时间戳...");
                String currentTime = AddTag2Pic.getCurrentTime();
                if (currentTime==null||currentTime.length()==0) {
                    new AlertDialog.Builder(this).setMessage("获取时间戳失败！").create().show();
                    break;
                }

                progressText.append("\n生成水印图片...");
                if (originalBitmap!=null) {
                    waterMarkBitmap = AddTag2Pic.drawTextToBitmap(this, originalBitmap, currentTime);
                }

                break;
            default:
                break;
        }

        if (waterMarkBitmap!=null) {
            setWaterMarkImage(waterMarkImage,waterMarkBitmap);
        }else {
            progressText.append("\n生成水印图片失败！");
        }
    }

    private void resetImage(ImageView v){
        progressText.append("\n重置图片...");
    }

    private void setWaterMarkImage(ImageView v, Bitmap b){
        v.setImageBitmap(b);
        progressText.append("\n图片水印添加成功！");
    }
}
