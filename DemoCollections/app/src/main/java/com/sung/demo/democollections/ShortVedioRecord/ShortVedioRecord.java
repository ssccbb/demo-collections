package com.sung.demo.democollections.ShortVedioRecord;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.MediaRecorderConfig;

public class ShortVedioRecord extends SungActivity implements View.OnClickListener{
    private String VEDIO_URL=null;
    private TextView hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortvedio_record);
        getSupportActionBar().hide();
        initView();
    }

    private void initView(){
        hintText = (TextView) findViewById(R.id.hint);
        Button record= (Button) findViewById(R.id.record);
        Button play= (Button) findViewById(R.id.play);
        record.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record:
                startRecord();
                break;
            case R.id.play:
                if (VEDIO_URL!=null)
                    startActivity(new Intent(this, VideoPlayerActivity.class).putExtra(
                            "path", VEDIO_URL));
                else
                    Toast.makeText(this, "暂无可预览视频", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void startRecord(){
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(true)
                .smallVideoWidth(480)
                .smallVideoHeight(360)
                .recordTimeMax(6 * 1000)
                .maxFrameRate(20)
                .minFrameRate(8)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.afterSmallVideoRecorder(this, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (data==null)
                    break;

                VEDIO_URL=data.getStringExtra(MediaRecorderActivity.VIDEO_URI);
                hintText.setText("VEDIO INFO\nOUTPUT_DIRECTORY:"
                        +data.getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY)
                        +"\nVIDEO_URI:"+VEDIO_URL);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
