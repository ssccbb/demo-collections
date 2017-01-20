package com.sung.demo.democollections.IMChat.view;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.sung.demo.democollections.R;

import java.io.IOException;

import mabeijianxi.camera.MediaRecorderActivity;

/*
* 单人聊天界面
* */
public class SingleChatAct extends EaseBaseActivity {
    public static SingleChatAct activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUsername;
    private String VIDEO_SCREENSHOT,VEDIO_URL;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imchat_single_chat);
        init();
    }

    private void init(){
        activityInstance = this;
        //user or group id
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        chatFragment = new EaseChatFragment();
        //set arguments
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // enter to chat activity when click notification bar, here make sure only one chat activiy
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (data==null)
                    break;

                VEDIO_URL=data.getStringExtra(MediaRecorderActivity.VIDEO_URI);
                VIDEO_SCREENSHOT=data.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
                try {
                    if (mediaPlayer==null)
                        mediaPlayer=new MediaPlayer();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(VEDIO_URL);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            Log.e("", "onPrepared: "+mediaPlayer.getDuration());
                            chatFragment.sendVedioMessage(VEDIO_URL,VIDEO_SCREENSHOT, mediaPlayer.getDuration());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername(){
        return toChatUsername;
    }
}
