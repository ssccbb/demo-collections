package com.sung.demo.democollections.PcitrueTAG.PicUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.sung.demo.democollections.R;

import java.util.Calendar;

/**
 * Created by sung on 2016/11/14.
 *
 * 添加水印  文字/图片水印
 */

public class AddTag2Pic {
    public static int TEXT_TAG_COLOR= R.color.text_disable;

    public static void setTextTagColor(int textTagColor) {
        TEXT_TAG_COLOR = textTagColor;
    }

    public static Bitmap createWaterMaskImage(Context gContext, Bitmap src, Bitmap watermark)
    {
        String tag = "createBitmap";
        Log.e(tag, "create a new bitmap");
        if (src == null)
        {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, 20, 20, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }

    public static Bitmap drawTextToBitmap(Context gContext, Bitmap gSrc, String gText) {
        if (gSrc==null)
            return null;
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        // 记录src的宽高
        int width = gSrc.getWidth();
        int height = gSrc.getHeight();
        gSrc = scaleWithWH(gSrc, width*scale, height*scale);

        android.graphics.Bitmap.Config bitmapConfig =
                gSrc.getConfig();

        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        gSrc = gSrc.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(gSrc);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(gContext.getResources().getColor(TEXT_TAG_COLOR));
        paint.setTextSize((int) (10 * scale));
        paint.setDither(true); //获取跟清晰的图像采样
        paint.setFilterBitmap(true);//过滤一些
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);

        //右下角文字水印
        int x = width-(int)(paint.measureText(gText)/scale)-5;
        int y = height-5;

        canvas.drawText(gText, x * scale, y * scale, paint);
        return gSrc;
    }

    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    public static String getCurrentTime(){
        Calendar calendar=Calendar.getInstance();
        //年
        String time= calendar.get(Calendar.YEAR)+"";

        //月
        if (calendar.get(Calendar.MONTH)<10)
            time=time+"-0"+calendar.get(Calendar.MONTH);
        else
            time=time+"-"+calendar.get(Calendar.MONTH);

        //日
        if (calendar.get(Calendar.DAY_OF_MONTH)<10)
            time=time+"-0"+calendar.get(Calendar.DAY_OF_MONTH);
        else
            time=time+"-"+calendar.get(Calendar.DAY_OF_MONTH);

        //时
        if (calendar.get(Calendar.HOUR_OF_DAY)<10)
            time=time+"\t0"+calendar.get(Calendar.HOUR_OF_DAY);
        else
            time=time+"\t"+calendar.get(Calendar.HOUR_OF_DAY);

        //分
        if (calendar.get(Calendar.MINUTE)<10)
            time=time+":0"+calendar.get(Calendar.MINUTE);
        else
            time=time+":"+calendar.get(Calendar.MINUTE);

        //秒
        if (calendar.get(Calendar.SECOND)<10)
            time=time+":0"+calendar.get(Calendar.SECOND);
        else
            time=time+":"+calendar.get(Calendar.SECOND);

        return time;
    }

}
