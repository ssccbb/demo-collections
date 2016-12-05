package com.sung.demo.democollections.VideoAndBarrage;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.sung.demo.democollections.Contants;
import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;
import com.sung.demo.democollections.VideoAndBarrage.MediaHelper.FileUtils;
import com.sung.demo.democollections.VideoAndBarrage.MediaHelper.MediaHelper;
import java.util.Timer;
import java.util.TimerTask;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.CenterLayout;

/*
* 横屏模式
* */
public class VedioPlayLandscape extends SungActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback,View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "VideoPlay[Media Player]";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private CenterLayout mPreviewContainer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    /*
    * 控制ui
    * */
    private ImageView play;
    private RelativeLayout controlUI;
    private TextView vedioFullTime;
    private SeekBar vedioSeekbar;

    /*
    * ui控制变量
    * */
    private static final int HIDE_PLAY_ACTION=0;
    private static final int HIDE_CONTROL_ACTION=1;

    /*
    * 信息更新定时器
    * */
    private Timer mTimer;
    private boolean SEEKBAR_IN_MOVING=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    /*
    * 更新的数据
    * */
    private long currentPosition=0;

    /*
    * 播放辅助
    * */
    private boolean FRIST_PLAY=true;//没有播放过
    private boolean START_PLAY=false;//播放标志
    private Animation fadeOut;//第一帧图片动画

    private Handler hideControlUI=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HIDE_PLAY_ACTION:
                    setPlayBtnStatus(0);
                    break;
                case HIDE_CONTROL_ACTION:
                    setControlUI(0);
                    break;
                default:
                    Log.e(TAG, "handleMessage - empty msg!");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*
    * 更新时间handler
    * */
    private Handler updateTimeText=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what!=0)
                vedioFullTime.setText(MediaHelper.formatDuring(msg.what,true));
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_vedio_play_landscape);
        getSupportActionBar().hide();
        //获取播放进度
        getPlayingInfo();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        initVatmioPlayer();
        ActionInit();
    }

    /*
    * activity暂时退出
    * */
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    /*
    * activity完全退出
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    public void finish() {
        Log.e(TAG, "enterFullModel3: "+mMediaPlayer.getCurrentPosition()
                +"/"+MediaHelper.formatDuring(mMediaPlayer.getCurrentPosition(),true));
        setResult(0,new Intent(VedioPlayLandscape.this,VideoPlay.class)
                .putExtra("CONTINUE_POSITION",(int) mMediaPlayer.getCurrentPosition()));

        if (mTimer!=null)
            mTimer.cancel();
        if (mMediaPlayer!=null)
            mMediaPlayer.pause();

        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_play_normal));
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_CONTROL_ACTION, Contants.HIDE_UI_DELAY_TIME);
        super.finish();
    }

    private void getPlayingInfo(){
        currentPosition=this.getIntent().getIntExtra("PLAY_POSITION",0);
        path=this.getIntent().getStringExtra("PATH");
        Log.e(TAG, "enterFullModel2: "+currentPosition
                +"/"+MediaHelper.formatDuring(currentPosition,true));
    }

    /*
        * 初始化播放 & ui初始
        * */
    private void initVatmioPlayer(){
        mPreview = (SurfaceView) findViewById(R.id.surface);
        mPreviewContainer = (CenterLayout) findViewById(R.id.surface_container);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        Log.e("path2",path);
        if (!FileUtils.isFileExists(path))
            new AlertDialog.Builder(VedioPlayLandscape.this).setMessage("当前文件路径\n"+path+"\n设置失败！").show();
    }

    /*
    * 播放前的工作
    * */
    private void playVideo() {
        doCleanUp();
        try {
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer();
            Log.e("load","load");
            mMediaPlayer.setDataSource(path);
            Log.e("load","load2");
            mMediaPlayer.setDisplay(holder);
            Log.e("load","load3");
            mMediaPlayer.prepareAsync();
            Log.e("load","load4");
            mMediaPlayer.setOnBufferingUpdateListener(this);
            Log.e("load","load5");
            mMediaPlayer.setOnCompletionListener(this);
            Log.e("load","load6");
            mMediaPlayer.setOnPreparedListener(this);
            Log.e("load","load7");
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            Log.e("load","load8");
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            Log.e("load","load9");
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        // Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    /*
    * 播放完成的监听
    * */
    public void onCompletion(MediaPlayer arg0) {
        Log.e(TAG, "onCompletion called");
        mediaPause();

        //播放完成时把seekbar设置到最大，防止timer少跳1s
        vedioSeekbar.setProgress(vedioSeekbar.getMax());
        currentPosition=0;
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.e(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;

        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    /*
    * prepareAsync 监听
    * */
    public void onPrepared(MediaPlayer mediaplayer) {
        Log.e(TAG, "onPrepared called");
        initVedioMsg();
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    /*
    * surface状态监听
    * */
    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.e(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.e(TAG, "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated called");
        Log.e("playvideo","playvideo");
        playVideo();
    }

    /*
    * 释放
    * */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /*
    * mediaplayer数据源清除
    * */
    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    /*
    * 播放
    * */
    private void startVideoPlayback() {
        Log.e(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        MediaHelper.setSurfaceViewMeasure(mPreviewContainer,mPreview,
                mMediaPlayer.getVideoWidth(),mMediaPlayer.getVideoHeight());

        playContinue(currentPosition);
    }

    /*
    * 从当前进度开始播放
    * */
    private void playContinue(long currentPosition){
        vedioSeekbar.setProgress((int) currentPosition);
        if (mMediaPlayer != null && (vedioSeekbar.getMax() > currentPosition))
            mMediaPlayer.seekTo((int) currentPosition);

        int leftDuration = mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition();
        if (vedioFullTime != null && leftDuration >= 0)
            updateTimeText.sendEmptyMessage(leftDuration);

        SEEKBAR_IN_MOVING=false;
        mediaPlay();

        START_PLAY=true;
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_CONTROL_ACTION,Contants.HIDE_UI_DELAY_TIME);
    }

    /*
    * 设置中间播放按钮的状态
    * */
    private void setPlayBtnStatus(int visible){
        switch (visible){
            case 0://invisile
                play.setVisibility(View.INVISIBLE);
                break;
            case 1://visible
                play.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        //改变播放按钮状态的同时该变控制UI的状态
        setControlUI(visible);
    }

    /*
    * 设置上下的ui显示状态
    * */
    private void setControlUI(int visible){
        switch (visible){
            case 0://invisile
                controlUI.setVisibility(View.INVISIBLE);
                break;
            case 1://visible
                controlUI.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /*
    * 动作初始化
    * */
    private void ActionInit(){
        play = (ImageView) findViewById(R.id.play);
        //控制ui面板
        controlUI = (RelativeLayout) findViewById(R.id.control_ui);
        ImageView finish= (ImageView) findViewById(R.id.vedio_back);
        ImageView fullPlay= (ImageView) findViewById(R.id.full_player);
        vedioFullTime = (TextView) findViewById(R.id.vedio_fulltime);
        vedioSeekbar = (SeekBar) findViewById(R.id.vedio_seekbar);

        mPreview.setOnClickListener(this);
        play.setOnClickListener(this);
        finish.setOnClickListener(this);
        fullPlay.setOnClickListener(this);
        vedioSeekbar.setOnSeekBarChangeListener(this);

        fadeOut = AnimationUtils.loadAnimation(VedioPlayLandscape.this, R.anim.fade_out);
        vedioFullTime.setText("00:00");
    }

    /*
    * setOnSeekBarChangeListener监听
    * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //拖动进行时
        if (SEEKBAR_IN_MOVING) {
            //Log.e(TAG, "seekbar 拖动进度 - " + i);
            if (mMediaPlayer != null && (seekBar.getMax() > i))
                mMediaPlayer.seekTo(i);

            int leftDuration = mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition();
            if (vedioFullTime != null && leftDuration >= 0)
                updateTimeText.sendEmptyMessage(leftDuration);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //拖动开始
        //Log.e(TAG, "seekbar 拖动开始！ ");
        SEEKBAR_IN_MOVING=true;

        if (!FRIST_PLAY)
            mediaPause();

        if (hideControlUI!=null)
            hideControlUI.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //拖动结束
        //Log.e(TAG, "seekbar 拖动结束！ ");
        SEEKBAR_IN_MOVING=false;
        mediaPlay();

        START_PLAY=true;
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_CONTROL_ACTION,Contants.HIDE_UI_DELAY_TIME);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                //路径判断
                if (!FileUtils.isFileExists(path))
                    new AlertDialog.Builder(VedioPlayLandscape.this).setMessage("当前文件路径\n"+path+"\n设置失败！").show();

                Log.e(TAG, "PLAY - START_PLAY:"+START_PLAY );
                //停止隐藏ui的handler
                hideControlUI.removeCallbacksAndMessages(null);

                //播放判断
                if (START_PLAY)
                    mediaPause();
                else
                    mediaPlay();

                //更改播放标志
                START_PLAY=!START_PLAY;
                //Log.e(TAG, "AFTER - START_PLAY:"+START_PLAY);
                break;
            case R.id.surface:
                setPlayBtnStatus(1);

                //移除ui控制handler任务
                if (FRIST_PLAY)
                    break;

                hideControlUI.sendEmptyMessageDelayed(
                        HIDE_PLAY_ACTION, Contants.HIDE_UI_DELAY_TIME);
                hideControlUI.sendEmptyMessageDelayed(
                        HIDE_CONTROL_ACTION, Contants.HIDE_UI_DELAY_TIME);
                break;
            case R.id.vedio_back:
                exitFullModel();
                break;
            case R.id.full_player:
                exitFullModel();
                break;
            default:
                Toast.makeText(this, "CLICK ID - "+view.getId(), Toast.LENGTH_SHORT).show();
                break;
        };
    }

    /*
    * 退出全屏
    * */
    private void exitFullModel(){
        finish();
    }

    /*
    * 播放
    * */
    private void mediaPlay(){
        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_pause_normal));

        if (FRIST_PLAY)
            FRIST_PLAY=!FRIST_PLAY;

        //隐藏ui
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_PLAY_ACTION,Contants.HIDE_UI_DELAY_TIME);
        if (mMediaPlayer!=null)
            mMediaPlayer.start();
        upDatePlayingTimeInfo();
    }

    /*
    * 暂停
    * */
    private void mediaPause(){//pause
        if (mTimer!=null)
            mTimer.cancel();
        if (mMediaPlayer!=null)
            mMediaPlayer.pause();

        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_play_normal));
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_CONTROL_ACTION,Contants.HIDE_UI_DELAY_TIME);
    }

    /*
    * 初始化该显示的视频数据
    * */
    private void initVedioMsg(){
        long duration = mMediaPlayer.getDuration();
        String fromatDuration = MediaHelper.formatDuring(duration,true);
        int fromatSecDuration = MediaHelper.duration2seconds(duration);
        Log.e(TAG, "initVedioMsg - duration:"+duration+"/"+fromatDuration+"/"+fromatSecDuration);

        if (vedioFullTime!=null)
            vedioFullTime.setText(fromatDuration);
        if (vedioSeekbar!=null)
            vedioSeekbar.setMax((int) duration);

        TextView vedioName= (TextView) findViewById(R.id.vedio_name);
        vedioName.setText(path);
    }


    private void upDatePlayingTimeInfo(){
        Log.e(TAG, "update vedio info timer called!" );
        mTimer = new Timer();
        mTimer.schedule(new VedioPlayLandscape.playingTimeUpdate(),0,1000);
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出播放器", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }

    /*
    * timer 更新进度条以及时长倒计时
    * */
    class playingTimeUpdate extends TimerTask {

        @Override
        public void run() {
            //seekbar在拖动，不在播放中，对象为空都不更新进度（用户体验）
            if(SEEKBAR_IN_MOVING==true
                    |START_PLAY==false|mMediaPlayer==null)
                return;

            progressCahnge();
            timeTextChange();
        }

        private void progressCahnge(){
            //防止进度条后跳
            if (vedioSeekbar.getProgress()<currentPosition)
                vedioSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
            currentPosition=mMediaPlayer.getCurrentPosition();
        }

        private void timeTextChange(){
            //倒计时
            int leftDuration = mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition();
            if (vedioFullTime!=null&&leftDuration>=0)
                updateTimeText.sendEmptyMessage(leftDuration);
        }
    }
}

