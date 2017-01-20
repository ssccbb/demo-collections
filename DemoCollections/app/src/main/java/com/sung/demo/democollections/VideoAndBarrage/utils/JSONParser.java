package com.sung.demo.democollections.VideoAndBarrage.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sung on 2016/12/29.
 */

public class JSONParser {
    private static String TAG="JSONParser-Utils";

    public static String getJsonFromAssets(Activity context, String fileName){
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            byte[] buffer = new byte[2048];
            int readBytes = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while((readBytes = is.read(buffer)) > 0){
                stringBuilder.append(new String(buffer, 0, readBytes));
            }

            Log.e(TAG, "getJsonFromAssets: "+stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getJsonFromAssets: "+e.toString());
        }finally {
            try {
                if (is!=null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
