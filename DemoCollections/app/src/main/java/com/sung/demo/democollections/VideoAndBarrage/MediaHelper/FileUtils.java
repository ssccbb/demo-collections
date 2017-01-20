package com.sung.demo.democollections.VideoAndBarrage.mediaHelper;

import java.io.File;

/**
 * Created by sung on 2016/11/15.
 */

public class FileUtils {

    public static boolean isFileExists(String path){
        boolean exist=false;
        File file=new File(path);
        if (file.exists()&&!file.isDirectory())
            exist=true;
        return exist;
    }

    public static boolean isFileDirExists(String path){
        boolean exist=false;
        File file=new File(path);
        if (file.isDirectory()){
            if (file.exists())
                exist=true;
        }
        return exist;
    }

}
