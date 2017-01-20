package com.sung.demo.democollections.VideoAndBarrage.view;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.sung.demo.democollections.Contants;
import com.sung.demo.democollections.MainActivity.SungActivity;
import com.sung.demo.democollections.R;
import com.sung.demo.democollections.VideoAndBarrage.danmu.Danmu;
import com.sung.demo.democollections.VideoAndBarrage.danmu.DanmuControl;
import com.sung.demo.democollections.VideoAndBarrage.mediaHelper.FileUtils;
import com.sung.demo.democollections.VideoAndBarrage.mediaHelper.MediaHelper;
import com.sung.demo.democollections.VideoAndBarrage.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.CenterLayout;
import master.flame.danmaku.ui.widget.DanmakuView;

public class VideoPlay extends SungActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
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
    private ImageView firstFrameImg,play;//第一帧
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
    private boolean CONTINUE_PLAY=false;//继续播放标志
    //private boolean CONTINUE_PLAY=false;//继续播放标志
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
        setContentView(R.layout.activity_video_play);
        getSupportActionBar().hide();

        initVatmioPlayer();

        setFirstFrame();//设置第一帧缩略图
        ActionInit();

        Button vedioview= (Button) findViewById(R.id.vedio_view);
        vedioview.setOnClickListener(this);
    }

    /*
    * activity暂时退出
    * */
    @Override
    protected void onPause() {
        super.onPause();
        mediaPause();
//        releaseMediaPlayer();
//        doCleanUp();

        if (mDanmuControl!=null)
            mDanmuControl.pause();
    }

    @Override
    protected void onResume() {
        //playVideo();
        if (currentPosition!=0) {
            mMediaPlayer.seekTo((int) currentPosition);
            if (danmuView!=null)
                danmuView.seekTo((long) currentPosition);
        }

        if (mDanmuControl!=null)
            mDanmuControl.resume();
        super.onResume();
    }

    /*
        * activity完全退出
        * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();

        if (mDanmuControl!=null)
            mDanmuControl.destroy();
    }

    @Override
    public void finish() {
        if (mTimer!=null)
            mTimer.cancel();
        if (mMediaPlayer!=null)
            mMediaPlayer.pause();

        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_play_normal));
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_CONTROL_ACTION,Contants.HIDE_UI_DELAY_TIME);
        super.finish();
    }

    /*
    * 初始化播放 & ui初始
    * */
    private void initVatmioPlayer(){
        initDanmuUI();

        mPreview = (SurfaceView) findViewById(R.id.surface);
        mPreviewContainer = (CenterLayout) findViewById(R.id.surface_container);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/super.mp4";///Download
        Log.e("path2",path);
        if (!FileUtils.isFileExists(path))
            new AlertDialog.Builder(VideoPlay.this).setMessage("当前文件路径\n"+path+"\n设置失败！").show();

        setPlayURI(path);
    }

    private void setPlayURI(String path){
        TextView plaUri= (TextView) findViewById(R.id.play_uri);
        plaUri.setText("当前播放uri:"+path+"\n\n注：如果无法播放请在文件管理器根目录下放置一个mp4视频文件，重命名为'super.mp4'");
    }

    /*
    * 播放前的工作
    * */
    private void playVideo() {
        doCleanUp();//释放
        try {
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer();
            Log.e("load","load");
            mMediaPlayer.setDataSource(path);//资源
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
        START_PLAY=false;
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
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }

        initVedioMsg();
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

        mDanmuControl.destroy();
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

        //如果是继续播放则继续 return
        if (CONTINUE_PLAY){
            continuePlay((int)currentPosition);
            return;
        }

        //如果正在播放 return
        if (!START_PLAY)
            return;

        mMediaPlayer.start();
    }

    /*
    * 设置第一帧的显示图像
    * */
    private void setFirstFrame(){
        Bitmap firstFrame = MediaHelper.createVideoThumbnail(path);
        firstFrameImg = (ImageView) findViewById(R.id.first_frame);
        if (firstFrame!=null) {
            firstFrameImg.setImageBitmap(firstFrame);
            firstFrameImg.setVisibility(View.VISIBLE);
        }else {
            removeFirstFrame();
            Log.e(TAG, "setFirstFrame - MediaHelper.createVideoThumbnail get null!");
        }
    }

    /*
    * 设置中间播放按钮的状态
    * */
    private void setPlayBtnStatus(int visible){
        switch (visible){
            case 0://invisile
                play.setVisibility(View.INVISIBLE);

                removeFirstFrame();//移除第一贞
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

    private void removeFirstFrame(){//移除第一帧
        if (firstFrameImg!=null) {
            firstFrameImg.setAnimation(fadeOut);
            firstFrameImg.setVisibility(View.INVISIBLE);
            firstFrameImg=null;
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

        fadeOut = AnimationUtils.loadAnimation(VideoPlay.this, R.anim.fade_out);
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
            if (mMediaPlayer != null && (seekBar.getMax() > i)) {
                mMediaPlayer.seekTo(i);
                if (danmuView!=null)
                    danmuView.seekTo((long) i);
            }

            int leftDuration = (int) mMediaPlayer.getDuration() - (int) mMediaPlayer.getCurrentPosition();
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
        else
            removeFirstFrame();

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
                    new AlertDialog.Builder(VideoPlay.this).setMessage("当前文件路径\n"+path+"\n设置失败！").show();

                Log.e(TAG, "PLAY - START_PLAY:"+START_PLAY );
                //停止隐藏ui的handler
                hideControlUI.removeCallbacksAndMessages(null);

                //播放判断
                if (START_PLAY) {
                    mediaPause();
                }else {
                    if (currentPosition==0) {
                        mediaPlay();
                    }else {
                        mediaReplay();
                    }
                }

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
                finish();
                break;
            case R.id.vedio_view:
                startActivity(new Intent(VideoPlay.this,VideoViewPlay.class));
                break;
            case R.id.full_player:
                enterFullModel();
                break;
            case R.id.send_comment:
                sendSingleDanmu();
                break;
            default:
                Toast.makeText(this, "CLICK ID - "+view.getId(), Toast.LENGTH_SHORT).show();
                break;
        };
    }

    /*
    * 进入全屏模式
    * */
    private void enterFullModel(){
        Log.e(TAG, "enterFullModel1: "+mMediaPlayer.getCurrentPosition()
                +"/"+MediaHelper.formatDuring(mMediaPlayer.getCurrentPosition(),true));
        startActivityForResult(new Intent(VideoPlay.this,VedioPlayLandscape.class)
                .putExtra("PLAY_POSITION",mMediaPlayer.getCurrentPosition())
                .putExtra("PATH",path),0);
    }

    /*
    * 播放
    * */
    private void mediaPlay(){
        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_pause_normal));

        //
        removeFirstFrame();
        if (FRIST_PLAY)
            FRIST_PLAY=!FRIST_PLAY;

        //隐藏ui
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_PLAY_ACTION,Contants.HIDE_UI_DELAY_TIME);
        if (mMediaPlayer!=null)
            mMediaPlayer.start();
        upDatePlayingTimeInfo();

        mDanmuControl.resume();
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

        mDanmuControl.pause();
    }

    /*
    * 重新播放
    * */
    private void mediaReplay(){
        play.setImageDrawable(
                getResources().getDrawable(R.drawable.mp_pause_normal));

        removeFirstFrame();
        if (FRIST_PLAY)
            FRIST_PLAY=!FRIST_PLAY;

        //隐藏ui
        hideControlUI.sendEmptyMessageDelayed(
                HIDE_PLAY_ACTION,Contants.HIDE_UI_DELAY_TIME);

        mMediaPlayer.seekTo(0);
        if (danmuView!=null)
            danmuView.seekTo((long)0);
        vedioSeekbar.setProgress(0);

        if (mMediaPlayer!=null)
            mMediaPlayer.start();
        upDatePlayingTimeInfo();

        mDanmuControl.resume();
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
    }

    private void upDatePlayingTimeInfo(){
        Log.e(TAG, "update vedio info timer called!" );
        mTimer = new Timer();
        mTimer.schedule(new playingTimeUpdate(),0,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                currentPosition=data.getIntExtra("CONTINUE_POSITION",0);
                Log.e(TAG, "enterFullModel: "+currentPosition);

                CONTINUE_PLAY=true;
                releaseMediaPlayer();
                playVideo();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void continuePlay(int position){
        mMediaPlayer.seekTo(position);
        if (danmuView!=null)
            danmuView.seekTo((long) position);
        mediaPlay();

        //播放完成之后重新设置flag
        START_PLAY=true;
        CONTINUE_PLAY=false;
    }

    /*
    * timer 更新进度条以及时长倒计时
    * */
    class playingTimeUpdate extends TimerTask{

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
                vedioSeekbar.setProgress((int)mMediaPlayer.getCurrentPosition());
            currentPosition=mMediaPlayer.getCurrentPosition();
        }

        private void timeTextChange(){
            //倒计时
            int leftDuration = (int)mMediaPlayer.getDuration() - (int)mMediaPlayer.getCurrentPosition();
            if (vedioFullTime!=null&&leftDuration>=0)
                updateTimeText.sendEmptyMessage(leftDuration);
        }
    }




    //    TODO 弹幕相关
    private EditText editDanmu;
    private TextView sendDanmu;
    private DanmakuView danmuView;
    private DanmuControl mDanmuControl;

    private void initDanmuUI(){
        editDanmu = (EditText) findViewById(R.id.edit_comment);
        sendDanmu = (TextView) findViewById(R.id.send_comment);
        sendDanmu.setOnClickListener(this);

        danmuView = (DanmakuView) findViewById(R.id.danmakuView);
        mDanmuControl = new DanmuControl(this);
        mDanmuControl.setDanmakuView(danmuView);
        mDanmuControl.pause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadHistoryDanmu();
            }
        }).start();
    }

    private String getEditDanmu(){
        return editDanmu.getText().toString().trim();
    }

    private void sendSingleDanmu(){
        if (editDanmu.getText()==null||
                editDanmu.getText().toString().length()==0)
            return;

        mDanmuControl.addCommentDanmu(getEditDanmu());
        editDanmu.setText("");
    }

    private void loadHistoryDanmu(){
        try {
            List<Danmu> danmus = new ArrayList<>();
            Danmu danmu;

            String jsonString = JSONParser.getJsonFromAssets(VideoPlay.this, "danmu.json");
            if (jsonString==null)
                return;
            //Log.e(TAG, "loadHistoryDanmu: "+jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray listdanmu = jsonObject.getJSONArray("listdanmu");
            for (int i = 0; i < listdanmu.length(); i++) {
                JSONObject jsonObject1 = listdanmu.getJSONObject(i);
                String content = jsonObject1.getString("content");
                int time = jsonObject1.getInt("time");

                danmu=new Danmu(i,i,"Comment",R.mipmap.ic_launcher,content,time);
                danmus.add(danmu);
            }
            Collections.shuffle(danmus);
            mDanmuControl.addDanmuList(danmus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendDanmuList(){
        List<Danmu> danmus = new ArrayList<>();
        Danmu danmu;
        for (int i = 0; i < 20; i++) {
            danmu=new Danmu(i,i,"Comment",R.mipmap.ic_launcher,"第"+i+"条弹幕",0);
            danmus.add(danmu);
        }
        Collections.shuffle(danmus);
        mDanmuControl.addDanmuList(danmus);
    }
}
