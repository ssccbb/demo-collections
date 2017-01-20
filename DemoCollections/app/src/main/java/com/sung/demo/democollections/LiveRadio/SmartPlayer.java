/*
 * SmartPlayer.java
 * SmartPlayer
 * 
 * Github: https://github.com/daniulive/SmarterStreaming
 * 
 * Created by DaniuLive on 2015/09/26.
 * Copyright © 2014~2016 DaniuLive. All rights reserved.
 */

package com.sung.demo.democollections.LiveRadio;

import java.nio.ByteBuffer;

import android.app.Activity;  
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
  
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daniulive.smartplayer.SmartPlayerJni;
import com.eventhandle.SmartEventCallback;
import com.videoengine.*;

public class SmartPlayer extends Activity {
	    
    private SurfaceView sSurfaceView = null;   
	
	private long playerHandle = 0;
	
	private static final int PORTRAIT = 1;		//竖屏
	private static final int LANDSCAPE = 2;		//横屏
	private static final String TAG = "SmartPlayer";
	
	private SmartPlayerJni libPlayer = null;
	
	private int currentOrigentation = PORTRAIT;
	
	private boolean isPlaybackViewStarted = false;
	
	private String playbackUrl = null;
	
	private boolean isMute = false;
	
	private boolean isHardwareDecoder = false;
	
	Button btnPopInputText;
	Button btnPopInputUrl;
	Button btnMute;
    Button btnStartStopPlayback;
	Button btnHardwareDecoder;
    TextView txtCopyright;
    TextView txtQQQun;
    
    LinearLayout lLayout = null;
    FrameLayout fFrameLayout = null;
    
    private Context myContext; 
    
	static {  
		System.loadLibrary("SmartPlayer");
	}
  
    @Override protected void onCreate(Bundle icicle) {  
        super.onCreate(icicle);  
        
      Log.i(TAG, "Run into OnCreate++");
      
      libPlayer = new SmartPlayerJni();
         
      myContext = this.getApplicationContext();

	  boolean bViewCreated = CreateView();
	    
	   if(bViewCreated){
		   inflateLayout(LinearLayout.VERTICAL);
	   }
    }
    
    /* For smartplayer demo app, the url is based on: baseURL + inputID
     * For example: 
     * baseURL: rtmp://daniulive.com:1935/hls/stream
     * inputID: 123456 
     * playbackUrl: rtmp://daniulive.com:1935/hls/stream123456
     * */
    private void GenerateURL(String id){
    	if(id == null)
    		return;
    	
    	if(id.equals("hks"))
    	{
    		playbackUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    		return;
    	}
    	
    	btnStartStopPlayback.setEnabled(true);
    	String baseURL = "rtmp://daniulive.com:1935/hls/stream";

    	playbackUrl = baseURL + id;
    }
    
    private void SaveInputUrl(String url)
    {
    	playbackUrl = "";
    	
    	if ( url == null )
    		return;
    	
    	// rtmp:/
    	if ( url.length() < 8 )
    	{
    		Log.e(TAG, "Input full url error:" + url);
    		return;
    	}
    	
    	if ( !url.startsWith("rtmp://") && !url.startsWith("rtsp://"))
    	{
    	    Log.e(TAG, "Input full url error:" + url);
    		return;
    	}
    		
    	btnStartStopPlayback.setEnabled(true);
    	playbackUrl = url;
    	
    	 Log.i(TAG, "Input full url:" + url);
    }
    
    /* Popup InputID dialog */
    private void PopDialog(){
    	final EditText inputID = new EditText(this);
    	inputID.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("如 rtmp://daniulive.com:1935/hls/stream123456,\n请输入123456").setView(inputID).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String strID = inputID.getText().toString();
                    	GenerateURL(strID);
                    }
                });
        builder.show();
    }
    
    
    private void PopFullUrlDialog(){
    	final EditText inputUrlTxt = new EditText(this);
    	inputUrlTxt.setFocusable(true);
    	inputUrlTxt.setText("rtmp://daniulive.com:1935/hls/stream");

        AlertDialog.Builder builderUrl = new AlertDialog.Builder(this);
        builderUrl.setTitle("如 rtmp://daniulive.com:1935/hls/stream123456").setView(inputUrlTxt).setNegativeButton(
                "取消", null);
        builderUrl.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String fullUrl = inputUrlTxt.getText().toString();
                        SaveInputUrl(fullUrl);
                    }
                });
        builderUrl.show();
    }
    
    /* Generate basic layout */
    private void inflateLayout(int orientation) {
    	if (null == lLayout)
            lLayout = new LinearLayout(this);

	    addContentView(lLayout,  new LayoutParams(LayoutParams.WRAP_CONTENT,
	      LayoutParams.WRAP_CONTENT));
    	
        lLayout.setOrientation(orientation);
   
        fFrameLayout = new FrameLayout(this);
        
        LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
        fFrameLayout.setLayoutParams(lp);
        Log.i(TAG, "++inflateLayout..");
               
        sSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));       
        
        fFrameLayout.addView(sSurfaceView, 0);

        RelativeLayout outLinearLayout = new RelativeLayout(this);
        outLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        
        LinearLayout lLinearLayout = new LinearLayout(this);
        lLinearLayout.setOrientation(LinearLayout.VERTICAL);
        lLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
 
                
        LinearLayout copyRightLinearLayout = new LinearLayout(this);
        copyRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rl.topMargin = getWindowManager().getDefaultDisplay().getHeight()-270;
        copyRightLinearLayout.setLayoutParams(rl);
 
        txtCopyright=new TextView(this);
        txtCopyright.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        txtCopyright.setText("Copyright 2014~2016 www.daniulive.com v1.0.16.0326");
        copyRightLinearLayout.addView(txtCopyright, 0);
        		
        txtQQQun=new TextView(this);
        txtQQQun.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        txtQQQun.setText("QQ群:294891451,  499687479");
        copyRightLinearLayout.addView(txtQQQun, 1);
        
        /* PopInput button */
        btnPopInputText = new Button(this);
        btnPopInputText.setText("输入urlID");
        btnPopInputText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnPopInputText, 0);
        
        btnPopInputUrl = new Button(this);
        btnPopInputUrl.setText("输入完整url");
        btnPopInputUrl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnPopInputUrl, 1);
        
        /*mute button */
        isMute = false;
        btnMute = new Button(this);
        btnMute.setText("静音 ");
        btnMute.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnMute, 2);
        
        /*hardware decoder button */
        isHardwareDecoder = false;
        btnHardwareDecoder = new Button(this);
        btnHardwareDecoder.setText("当前软解码");
        btnHardwareDecoder.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnHardwareDecoder, 3);
        
        /* Start playback stream button */
        btnStartStopPlayback = new Button(this);
        btnStartStopPlayback.setText("开始播放 ");
        btnStartStopPlayback.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lLinearLayout.addView(btnStartStopPlayback, 4);
 
       
        outLinearLayout.addView(lLinearLayout, 0);
        outLinearLayout.addView(copyRightLinearLayout, 1);
        fFrameLayout.addView(outLinearLayout, 1);

        lLayout.addView(fFrameLayout, 0);
  
        if(isPlaybackViewStarted)
        {
        	btnPopInputText.setEnabled(false);
        	btnPopInputUrl.setEnabled(false);
        	btnHardwareDecoder.setEnabled(false);
        	btnStartStopPlayback.setText("停止播放 ");
        }
        else
        {
        	btnPopInputText.setEnabled(true);
        	btnPopInputUrl.setEnabled(true);
        	btnHardwareDecoder.setEnabled(true);
        	btnStartStopPlayback.setText("开始播放 ");
        }
        
        /* PopInput button listener */
        btnPopInputText.setOnClickListener(new Button.OnClickListener() {  
        	  
            //  @Override  
              public void onClick(View v) {  
            	  Log.i(TAG, "Run into input playback ID++");
	              
            	  PopDialog();
            	  
            	  Log.i(TAG, "Run out from input playback ID--");
              	}
              });  
        
        
        btnPopInputUrl.setOnClickListener(new Button.OnClickListener() { 
        	 public void onClick(View v) { 
        		 PopFullUrlDialog();
        	 }
        });
        
        btnMute.setOnClickListener(new Button.OnClickListener() 
        { 
       	 public void onClick(View v) { 
    		 isMute = !isMute;
    		 
    		 if ( isMute )
    		 {
    			 btnMute.setText("取消静音");
    		 }
    		 else
    		 {
    			 btnMute.setText("静音");
    		 }
    		 
    		 if ( playerHandle != 0 )
    		 {
    			 libPlayer.SmartPlayerSetMute(playerHandle, isMute?1:0);
    		 }
    	 }
       });

        btnHardwareDecoder.setOnClickListener(new Button.OnClickListener() 
        { 
       	 public void onClick(View v) { 
    		 isHardwareDecoder = !isHardwareDecoder;
    		 
    		 if ( isHardwareDecoder )
    		 {
    			 btnHardwareDecoder.setText("当前硬解码");
    		 }
    		 else
    		 {
    			 btnHardwareDecoder.setText("当前软解码");
    		 }
    		 
    	 }
       });
        
        btnStartStopPlayback.setOnClickListener(new Button.OnClickListener() {  
        	  
            //  @Override  
              public void onClick(View v) {  
	              
            	  if(isPlaybackViewStarted)
            	  {
                	  Log.i(TAG, "Stop playback stream++");
            		  btnStartStopPlayback.setText("开始播放 ");
            		  btnPopInputText.setEnabled(true);
            		  btnPopInputUrl.setEnabled(true);
            		  btnHardwareDecoder.setEnabled(true);
            		  libPlayer.SmartPlayerClose(playerHandle);	
            		  playerHandle = 0;
            		  isPlaybackViewStarted = false;
                      Log.i(TAG, "Stop playback stream--");
            	  }
            	  else
            	  {
            		  Log.i(TAG, "Start playback stream++");
            		  
            		  playerHandle = libPlayer.SmartPlayerInit(myContext);
            	      
            	      if(playerHandle == 0)
            	      {
            	    	  Log.e(TAG, "surfaceHandle with nil..");
            	    	  return;
            	      }
            		  
					  libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());
					  
            	      libPlayer.SmartPlayerSetSurface(playerHandle, sSurfaceView); 	//if set the second param with null, it means it will playback audio only..
            		  	
            	      // libPlayer.SmartPlayerSetSurface(playerHandle, null); 
            	      
            	      // External Render test
            	      //libPlayer.SmartPlayerSetExternalRender(playerHandle, new RGBAExternalRender());
            	      //libPlayer.SmartPlayerSetExternalRender(playerHandle, new I420ExternalRender());
 	              	 
            	      libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
            	      
            	      libPlayer.SmartPlayerSetBuffer(playerHandle, 200);
            	      
            	      if ( isMute )
            	      {
            	    	  libPlayer.SmartPlayerSetMute(playerHandle, isMute?1:0);
            	      }
            	      
            	      if( isHardwareDecoder )
            	      {
            	    	  Log.i(TAG, "check isHardwareDecoder: " + isHardwareDecoder);
            	    	  
                	      int hwChecking = libPlayer.SetSmartPlayerVideoHWDecoder(playerHandle, isHardwareDecoder?1:0);
  						
    					  Log.i(TAG, "[daniulive] hwChecking: " + hwChecking);
            	      }

            	      //It only used when playback RTSP stream..
            	     // libPlayer.SmartPlayerSetRTSPTcpMode(playerHandle, 1);
     
					  //playbackUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
            	                  	      
            	      //playbackUrl = "rtsp://rtsp-v3-spbtv.msk.spbtv.com/spbtv_v3_1/214_110.sdp";

					  //playbackUrl = "rtmp://10.2.68.91:1935/hls/stream8";

	              	  if(playbackUrl == null){
	              		 Log.e(TAG, "playback URL with NULL..."); 
	              		 return;
	              	  }
	              	  
	              	  int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
	              	  	              	  
	                  if(iPlaybackRet != 0)
	                  {
	                	 Log.e(TAG, "StartPlayback strem failed.."); 
	                	 return;
	                  }
	
	        		  btnStartStopPlayback.setText("停止播放 ");
	                  btnPopInputText.setEnabled(false);
	                  btnPopInputUrl.setEnabled(false);
	                  btnHardwareDecoder.setEnabled(false);
	              	  isPlaybackViewStarted = true;
	              	  Log.i(TAG, "Start playback stream--");
	        	  }
	          	}
              });
	}
	
    
    public static final String bytesToHexString(byte[] buffer)
    {   
        StringBuffer sb = new StringBuffer(buffer.length);   
        String temp;
        
        for (int i = 0; i < buffer.length; ++i)
        {   
        	temp = Integer.toHexString(0xff&buffer[i]);   
          if (temp.length() < 2)   
            sb.append(0);
          
          sb.append(temp);   
        }   
        
        return sb.toString();   
    }  
    
    class RGBAExternalRender implements NTExternalRender 
    {
    	//public static final int NT_FRAME_FORMAT_RGBA = 1;
    	//public static final int NT_FRAME_FORMAT_ABGR = 2;
    	//public static final int NT_FRAME_FORMAT_I420 = 3;
    	
    	private int width_ = 0;
    	private int height_ = 0;
    	private int row_bytes_ = 0;
    	private ByteBuffer rgba_buffer_ = null;

    	@Override
    	public int getNTFrameFormat()
    	{
    		Log.i(TAG, "RGBAExternalRender::getNTFrameFormat return "+ NT_FRAME_FORMAT_RGBA);
    		return NT_FRAME_FORMAT_RGBA;
    	}

    	@Override
    	public void onNTFrameSizeChanged(int width, int height)
    	{
    		width_ = width;
    		height_ = height;
    		
    		row_bytes_ = width_ * 4;
    		
    		Log.i(TAG, "RGBAExternalRender::onNTFrameSizeChanged width_:" + width_ + " height_:" + height_);
    		
    		rgba_buffer_ = ByteBuffer.allocateDirect(row_bytes_*height_);
    	}

    	@Override
    	public ByteBuffer getNTPlaneByteBuffer(int index)
    	{
    		if ( index == 0 )
    		{
    			return rgba_buffer_;
    		}
    		else
    		{
    			Log.e(TAG, "RGBAExternalRender::getNTPlaneByteBuffer index error:" + index);
    			return null;
    		}
    	}

    	@Override
    	public int getNTPlanePerRowBytes(int index)
    	{
    		if ( index == 0 )
    		{
    			return row_bytes_;
    		}
    		else
    		{
    			Log.e(TAG, "RGBAExternalRender::getNTPlanePerRowBytes index error:" + index);
    			return 0;
    		}
    	}

    	public void onNTRenderFrame()
    	{
    		 if( rgba_buffer_ == null )
    	            return;
    		 
    		 rgba_buffer_.rewind();
    		 
    		 // copy buffer
    		 
    		 // test
    		// byte[] test_buffer = new byte[16];
    		// rgba_buffer_.get(test_buffer);
    		 
    		 //Log.i(TAG, "RGBAExternalRender:onNTRenderFrame rgba:" + bytesToHexString(test_buffer));
    	}
    }
    
    class I420ExternalRender implements NTExternalRender
    {
    	//public static final int NT_FRAME_FORMAT_RGBA = 1;
    	//public static final int NT_FRAME_FORMAT_ABGR = 2;
    	//public static final int NT_FRAME_FORMAT_I420 = 3;
    	
    	private int width_ = 0;
    	private int height_ = 0;
    	
    	private int y_row_bytes_ = 0;
    	private int u_row_bytes_ = 0;
    	private int v_row_bytes_ = 0;
    	
    	private ByteBuffer y_buffer_ = null;
    	private ByteBuffer u_buffer_ = null;
    	private ByteBuffer v_buffer_ = null; 
    	
    	@Override
    	public int getNTFrameFormat()
    	{
    		Log.i(TAG, "I420ExternalRender::getNTFrameFormat return "+ NT_FRAME_FORMAT_I420);
    		return NT_FRAME_FORMAT_I420;
    	}

    	@Override
    	public void onNTFrameSizeChanged(int width, int height)
    	{
    		width_ = width;
    		height_ = height;
    		
    		y_row_bytes_ = (width_ + 15) & (~15);
    		u_row_bytes_ = ((width_+1)/2 + 15) & (~15);
    		v_row_bytes_ = ((width_+1)/2 + 15) & (~15);
    		
    		y_buffer_ = ByteBuffer.allocateDirect(y_row_bytes_*height_);
    		u_buffer_ = ByteBuffer.allocateDirect(u_row_bytes_*(height_+1)/2);
    		v_buffer_ = ByteBuffer.allocateDirect(v_row_bytes_*(height_+1)/2);
    		
    		Log.i(TAG, "I420ExternalRender::onNTFrameSizeChanged width_=" 
    		    + width_ + " height_=" + height_ 
    		    + " y_row_bytes_=" +  y_row_bytes_
    		    + " u_row_bytes_=" + u_row_bytes_
    		    + " v_row_bytes_=" + v_row_bytes_);
    	}

    	@Override
    	public ByteBuffer getNTPlaneByteBuffer(int index)
    	{
    		if ( index == 0 )
    		{
    			return y_buffer_;
    		}
    		else if ( index == 1 )
    		{
    			return u_buffer_;
    		}
    		else if ( index == 2 )
    		{
    			return v_buffer_;
    		}
    		else
    		{
    			Log.e(TAG, "I420ExternalRender::getNTPlaneByteBuffer index error:" + index);
    			return null;
    		}
    	}

    	@Override
    	public int getNTPlanePerRowBytes(int index)
    	{
    		if ( index == 0 )
    		{
    			return y_row_bytes_;
    		}
    		else if ( index == 1)
    		{
    			return u_row_bytes_;
    		}
    		else if (index == 2 )
    		{
    			return v_row_bytes_;
    		}
    		else
    		{
    			Log.e(TAG, "I420ExternalRender::getNTPlanePerRowBytes index error:" + index);
    			return 0;
    		}
    	}

    	public void onNTRenderFrame()
    	{
    		if ( y_buffer_ == null )
    			return;
    		
    		if ( u_buffer_ == null )
    			return;
    		
    		if ( v_buffer_ == null )
    			return;
    		
      
    		y_buffer_.rewind();
    		
    		u_buffer_.rewind();
    		
    		v_buffer_.rewind();
    		
    		 // copy buffer
    		
    		// test
    		// byte[] test_buffer = new byte[16];
    		// y_buffer_.get(test_buffer);
    		 
    		// Log.i(TAG, "I420ExternalRender::onNTRenderFrame y data:" + bytesToHexString(test_buffer));
    		 
    		// u_buffer_.get(test_buffer);
    		// Log.i(TAG, "I420ExternalRender::onNTRenderFrame u data:" + bytesToHexString(test_buffer));
    		 
    		// v_buffer_.get(test_buffer);
    		// Log.i(TAG, "I420ExternalRender::onNTRenderFrame v data:" + bytesToHexString(test_buffer));
    	}
    }
    
    
    class EventHande implements SmartEventCallback
    {
    	 @Override
    	 public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5){
             switch (code) {
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STARTED:
                     Log.i(TAG, "开始。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTING:
                     Log.i(TAG, "连接中。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTION_FAILED:
                     Log.i(TAG, "连接失败。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTED:
                     Log.i(TAG, "连接成功。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_DISCONNECTED:
                     Log.i(TAG, "连接断开。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STOP:
                     Log.i(TAG, "关闭。。");
                     break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_RESOLUTION_INFO:
                	 Log.i(TAG, "分辨率信息: width: " + param1 + ", height: " + param2);
                	 break;
                 case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_NO_MEDIADATA_RECEIVED:
                	 Log.i(TAG, "收不到媒体数据，可能是url错误。。");
             }
    	 }
    }
    
    /* Create rendering */
    private boolean CreateView() {
    	
        if(sSurfaceView == null)
        {
        	 /*
             *  useOpenGLES2:
             *  If with true: Check if system supports openGLES, if supported, it will choose openGLES.
             *  If with false: it will set with default surfaceView;	
             */
        	sSurfaceView = NTRenderer.CreateRenderer(this, true);
        }
        
        if(sSurfaceView == null)
        {
        	Log.i(TAG, "Create render failed..");
        	return false;
        }

        return true;
	}
    
	@Override  
    public void onConfigurationChanged(Configuration newConfig) {  
            super.onConfigurationChanged(newConfig);  
            
            Log.i(TAG, "Run into onConfigurationChanged++");
            
            if (null != fFrameLayout)
            {
            	fFrameLayout.removeAllViews();
            	fFrameLayout = null;
            }
            
            if (null != lLayout)
            {
                lLayout.removeAllViews();
                lLayout = null;
            }
            
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
            {   
            	Log.i(TAG, "onConfigurationChanged, with LANDSCAPE。。");

            	inflateLayout(LinearLayout.HORIZONTAL);
                 
            	currentOrigentation = LANDSCAPE;
            } 
            else
            {  
            	Log.i(TAG, "onConfigurationChanged, with PORTRAIT。。"); 
            	
            	inflateLayout(LinearLayout.VERTICAL);
            	
            	currentOrigentation = PORTRAIT;
            }  
            
            if(!isPlaybackViewStarted)
            	return;
            
            libPlayer.SmartPlayerSetOrientation(playerHandle, currentOrigentation);

            Log.i(TAG, "Run out of onConfigurationChanged--");
    }
    
	@Override
    protected  void onDestroy()
	{
		Log.i(TAG, "Run into activity destory++");   	
    	
		if(playerHandle!=0)
		{
			libPlayer.SmartPlayerClose(playerHandle);	
			playerHandle = 0;
		}
		super.onDestroy();
    	finish();
    }
}