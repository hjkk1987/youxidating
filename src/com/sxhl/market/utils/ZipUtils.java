package com.sxhl.market.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.widget.Toast;

import com.sxhl.market.R;

public class ZipUtils {

    public boolean zipTerminal = false;

    public ZipUtils() {
    }

    public void terminal() {
        zipTerminal = true;
    }

    public boolean isTerminal() {
        return zipTerminal;
    }

    /**
     * 功能：解压 zip 文件，只能解压 zip 文件
     */
    public void unZipApk(String zipfile, String destDir, String apkDestPath,
            OnZipListener listener) {
        // destDir = destDir.endsWith("\\") ? destDir : destDir + "\\";
        byte b[] = new byte[4*1024];
        int length;
        byte[] byteTmp1=new byte[10];
        byte[] byteTmp2=new byte[10];
        InputStream inputStream=null;
        OutputStream outputStream=null;

        try {
            zipTerminal = false;
            if (listener != null) {
                listener.onStart();
            }
            if(zipTerminal){
                if(listener!=null){
                    listener.onFinish();
                }
                return;
            }
            
            ZipFile zipFile;
            
            long uncompressFileSize = 0;
            long currentUncompressSize = 0;
            long currentEntrySize;
            int progress = 0;
            int lastProgress = 0;
            boolean isUnApk = false;
            String entryName;
            long loadedLen=0;
//            RandomAccessFile rfile;

            zipFile = new ZipFile(new File(zipfile));

            @SuppressWarnings("rawtypes")
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry = null;
            while (enumeration.hasMoreElements() && !zipTerminal) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                uncompressFileSize += zipEntry.getSize();
            }
//            DebugTool.info("ZipUtils","uncompressSize:" + uncompressFileSize);
            
            long leftSize=DeviceTool.getSdAvailableSpace();
            if(uncompressFileSize>0 && leftSize>=0 && uncompressFileSize>leftSize){
                if(listener!=null){
                    listener.onError(null,R.string.manage_zip_sd_space_error);
                }
                return;
            }

            enumeration = zipFile.entries();
            while (enumeration.hasMoreElements() && !zipTerminal) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                // String name = destDir + new
                // String(zipEntry.getName().getBytes("gbk"), "utf-8");
                // File loadFile = new File(name);
                File loadFile = null;
                entryName = zipEntry.getName();
                if (!isUnApk && entryName.toLowerCase().endsWith(".apk")
                        && !entryName.contains("/")) {
                    loadFile = new File(apkDestPath);
                    isUnApk = true;
                } else {
                    loadFile = new File(destDir + zipEntry.getName());
                }
                 System.out.println(zipEntry.getName()+" size:"+zipEntry.getSize());
                if (zipEntry.isDirectory()) {
                    if(!loadFile.exists()) {
                        loadFile.mkdirs();
                    }
                } else {
                    
                    currentEntrySize = zipEntry.getSize();
                    loadedLen=loadFile.length();
                    if(loadFile.exists() && loadedLen==currentEntrySize && loadedLen>20){
                        //已经解压的文件不再解压
                        inputStream = zipFile.getInputStream(zipEntry);
                        inputStream.read(byteTmp1, 0, byteTmp1.length);
                        try {
                            inputStream.close(); 
                            inputStream=null;
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        inputStream = new FileInputStream(loadFile);
                        inputStream.read(byteTmp2, 0, byteTmp2.length);
                        try {
                           inputStream.close(); 
                           inputStream=null;
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        if(Arrays.equals(byteTmp1, byteTmp2)){
                            currentUncompressSize += currentEntrySize;
                            if (listener != null) {
                                progress = (int) (currentUncompressSize * 100 / uncompressFileSize);
                                if (progress != lastProgress) {
                                    lastProgress = progress;
                                    listener.onProcess(progress);
                                }
                            }
                            continue;
                        }
                    }
                    
                    if (!loadFile.getParentFile().exists()) {
                        loadFile.getParentFile().mkdirs();
                    }
                    inputStream = zipFile.getInputStream(zipEntry);
                    outputStream = new FileOutputStream(loadFile);
//                    rfile=new RandomAccessFile(loadFile, "rw");
//                    if(loadedLen>0){
//                        inputStream.skip(loadedLen);
//                        rfile.skipBytes((int)loadedLen);
//                    }
                    while ((length = inputStream.read(b)) > 0 && (!zipTerminal)) {
                        outputStream.write(b, 0, length);
//                        rfile.write(b, 0, length);
                        
                        currentUncompressSize += length;
                        if (listener != null) {
                            progress = (int) (currentUncompressSize * 100 / uncompressFileSize);
                            if (progress != lastProgress) {
                                lastProgress = progress;
                                listener.onProcess(progress);
                            }
                        }
                    }
                    try {
                        inputStream.close();
                        outputStream.close();
//                        rfile.close();
                        inputStream=null;
                        outputStream=null;
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
//                if (zipTerminal){
//                    break;
//                }
//                currentUncompressSize += currentEntrySize;
//                if (listener != null) {
//                    progress = (int) (currentUncompressSize * 100 / uncompressFileSize);
//                    if (progress != lastProgress) {
//                        lastProgress = progress;
//                        listener.onProcess(progress);
//                    }
//                }
            }
            if (listener != null) {
                listener.onFinish();
            }
        } catch (ZipException e){
            e.printStackTrace();
            if(listener!=null){
                listener.onError(e,R.string.manage_zip_file_format_error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e,R.string.manage_zip_error);
            }
        } finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                    inputStream=null;
                }
                if(outputStream!=null){
                    outputStream.close();
                    outputStream=null;
                }
            } catch (Exception e2) {
                // TODO: handle exception
            }
        }
    }

    public static interface OnZipListener {
        public void onProcess(int progress);

        public void onStart();

        public void onFinish();

        public void onError(Exception e,int errId);
    }
}
