package com.sung.demo.democollections.VideoAndBarrage.mediaHelper;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.SurfaceView;

import io.vov.vitamio.widget.CenterLayout;

/**
 * Created by sung on 2016/11/15.
 */

public class MediaHelper {
    private static String TAG="VideoThumbnail";

    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(filePath);

            Bitmap bitmap = media.getFrameAtTime();
            if (bitmap != null)
                Log.e(TAG, "createVideoThumbnail - media.getFrameAtTime() success!");
            return bitmap;
        }catch (IllegalArgumentException ex){
            Log.e(TAG, "createVideoThumbnail: ", ex);
        }
        return null;
    }

    //计算缩放比例
    public static void setSurfaceViewMeasure(CenterLayout container, SurfaceView surfaceView,float videoWith,float videoHeight){
        if (container==null||surfaceView==null)
            return;

        Log.e(TAG, "setSurfaceViewMeasure: "+videoWith+"/"+videoHeight);
        int VIDEO_TYPE=0;

        if (videoWith>videoHeight) VIDEO_TYPE=0;//横条型
        if (videoWith<videoHeight) VIDEO_TYPE=1;//竖条型
        if (videoWith==videoHeight) VIDEO_TYPE=2;//方型

        int measuredWidth = container.getMeasuredWidth();
        int measuredHeight = container.getMeasuredHeight();
        float scale=videoWith/videoHeight;
        Log.e(TAG, "setSurfaceViewMeasure: scale = "+(scale) );
        Log.e(TAG, "setSurfaceViewMeasure: measuredWidth="+measuredWidth+" measuredHeight="+measuredHeight );

        float surfaceWith=0;
        float surfaceHeight=0;
        switch (VIDEO_TYPE){
            case 0://以宽为准
                //scale=measuredWidth/videoWith;
                surfaceWith = measuredWidth;
                surfaceHeight = surfaceWith/scale;
                break;
            case 1://以高为准
                //scale=measuredHeight/videoHeight;
                surfaceHeight = measuredHeight;
                surfaceWith = surfaceHeight*scale;
                break;
            case 2://以高为准
                //scale=measuredHeight/videoHeight;
                surfaceHeight = measuredHeight;
                surfaceWith = surfaceHeight*scale;
                break;
        }

        Log.e(TAG, "setSurfaceViewMeasure: videoWith="+surfaceWith+" videoHeight="+surfaceHeight);
        if (surfaceWith==0|surfaceHeight==0)
            return;

        surfaceView.setLayoutParams(new CenterLayout.LayoutParams((int)surfaceWith,(int)surfaceHeight,0,0));
    }

    //计算时间
    public static String formatDuring(long mss,boolean withOutHours) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String hour = "";
        if(hours < 10) {
            hour = "0" + hours;
        }else{
            hour = hours+"";
        }
        String minute = "";
        if(minutes < 10) {
            minute = "0" + minutes;
        }else{
            minute = hours+"";
        }
        String second = "";
        if(seconds < 10) {
            second = "0" + seconds;
        }else{
            second = seconds+"";
        }

        //不需要显示小时
        if (withOutHours){
            minutes=hours*60+minutes;

            if (minutes<10)
                minute = "0" + minutes;
            else
                minute = "" + minutes;

            return minute + ":" + second ;
        }
        return hour + ":" + minute + ":" + second ;
    }

    public static int duration2seconds(long mss){
        int seconds=(int)(mss/1000);
        return seconds;
    }
}
