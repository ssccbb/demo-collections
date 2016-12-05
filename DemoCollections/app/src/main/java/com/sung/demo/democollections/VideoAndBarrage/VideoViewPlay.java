package com.sung.demo.democollections.VideoAndBarrage;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sung.demo.democollections.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewPlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
        setContentView(R.layout.activity_vedio_view_play);

        getSupportActionBar().hide();

        //video play
        playfunction();
        myOnClick();
    }

    @Override
    protected void onPostResume() {
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onPostResume();
    }

    private void initActivity(){
        Vitamio.isInitialized(VideoViewPlay.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
    }

    /*
    * video view player
    * */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        super.onConfigurationChanged(newConfig);
    }
    /**
     * 加载视频
     */
    VideoView mVideoView;
    void playfunction() {
        String path = "";
        path = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(VideoViewPlay.this, "路径错误", Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView
                    .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            // optional need Vitamio 4.0
                            mediaPlayer.setPlaybackSpeed(1.0f);
                        }
                    });
        }
    }
    /**
     * 监听
     */
    private void myOnClick(){
        //设置一下监听：播放完成的监听，播放准备好的监听，播放出错的监听

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                //开始播放
                progressDialog2.dismiss();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "视频播放完成了", Toast.LENGTH_SHORT).show();
                finish();//退出播放器
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "视频播放出错了",  Toast.LENGTH_SHORT).show();
                return true;
            }
        });
/**
 * 缓冲设置
 *
 */
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (mVideoView.isPlaying()) {
                            mVideoView.pause();

                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mVideoView.start();

                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:

                        break;
                }
                return true;
            }


        });
        mVideoView.setBufferSize(1024);
    }

    //  显示界面后，展示dailog
    ProgressDialog progressDialog2=null;
    @Override
    protected void onResume() {
        super.onResume();
        progressDialog2=new ProgressDialog(VideoViewPlay.this);
        progressDialog2.setMessage("玩命加载中。。。");
        progressDialog2.setCancelable(true);
        progressDialog2.show();
    }

    @Override
    public void finish() {
        mVideoView.stopPlayback();
        super.finish();
    }
}
