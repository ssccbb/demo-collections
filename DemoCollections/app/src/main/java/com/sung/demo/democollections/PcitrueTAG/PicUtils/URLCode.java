package com.sung.demo.democollections.PcitrueTAG.PicUtils;

import android.util.Log;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by sung on 16/3/28.
 *
 * 转码
 */
public class URLCode {
    public String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e("URLCODE","toURLEncoded error:" + paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("URLCODE","toURLEncoded error:"+paramString, localException);
        }

        return "";
    }

    public String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e("URLCODE","toURLDecoded error:" + paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("URLCODE","toURLDecoded error:" + paramString, localException);
        }

        return "";
    }
}
