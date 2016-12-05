package com.sung.demo.democollections.PcitrueTAG.PicUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sung on 2016/11/14.
 *
 * 读取图片
 */

public class Readbitmap {

    /**
     * 根据图片的url路径获得Bitmap对象
     * new Thread
     * @param url
     * @return
     */
    public static Bitmap returnBitmap(String url) {
        //Log.e(TAG,"url---"+url);
        URL fileUrl = null;
        Bitmap bitmap = null;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize=5;

        try {
            URLCode urlCode=new URLCode();
            String s = urlCode.toURLDecoded(url);
            fileUrl = new URL(s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap returnLocalBitmap(String uri){
        Bitmap bitmap=null;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize=5;

        if (uri != null) {
            bitmap = BitmapFactory.decodeFile(uri,opt);
        }

        return bitmap;
    }

    /** 保存bitmap方法 */
    public static void saveBitmap(Bitmap bm,String path,String filename) {
        //path="/sdcard/yrksCache";
        //filename="adpic.jpg";
        if (bm==null)
            return;
        File file = new File(path);
        if (!file.exists())
            file.mkdir();

        file = new File((path+filename).trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));

        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = path + "/" + mName + "_cropped" + sName;
        Log.e("", "saveBitmap "+newFilePath);
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            if (fos!=null)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Log.e("", "saveBitmap "+e.toString());
        }

    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }

    public static Bitmap readBitMapFromDrwable(Drawable drawable){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = FormatTools.getInstance().Drawable2InputStream(drawable);
        return BitmapFactory.decodeStream(is,null,opt);
    }
}
