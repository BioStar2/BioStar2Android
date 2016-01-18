package com.supremainc.biostar2.util;

import android.util.Log;

import com.supremainc.biostar2.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final int COMPRESSION_LEVEL = 8;
    private static final String TAG = "ZipUtil";

    public static void unzip(File zipFile, File targetDir, boolean fileNameToLowerCase) throws Exception {
        FileInputStream fileInputStream = null;
        ZipInputStream zipInputStream = null;
        ZipEntry zipEntry = null;

        try {
            fileInputStream = new FileInputStream(zipFile);
            zipInputStream = new ZipInputStream(fileInputStream);
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if (fileNameToLowerCase) {
                    fileName = fileName.toLowerCase();
                }
                File targetFile = new File(targetDir, fileName);
                if (zipEntry.isDirectory()) {
                    targetFile.mkdir();
                } else {
                    unzipEntry(zipInputStream, targetFile);
                }
            }
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    protected static File unzipEntry(ZipInputStream zipInputStream, File targetFile) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return targetFile;
    }

    public static void zip(String[] sourcePath, String output) throws Exception {
        File sourceFile;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(output);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            zipOutputStream = new ZipOutputStream(bufferedOutputStream);
            zipOutputStream.setLevel(COMPRESSION_LEVEL);

            for (int i = 0; i < sourcePath.length; i++) {
                if (null == sourcePath[i]) {
                    continue;
                }
                sourceFile = new File(sourcePath[i]);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "full path:" + sourcePath[i]);
                }
                zipEntry(sourceFile, sourcePath[i], zipOutputStream);
            }
            zipOutputStream.finish();
        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    private static void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zipOutputStream) throws Exception {
        if (sourceFile.isDirectory()) {
            if (sourceFile.getName().equalsIgnoreCase(".metadata")) {
                return;
            }
            File[] fileArray = sourceFile.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                zipEntry(fileArray[i], sourcePath, zipOutputStream);
            }
        } else {
            zipEntryFileOnly(sourceFile, sourcePath, zipOutputStream);
        }
    }

    private static void zipEntryFileOnly(File sourceFile, String sourcePath, ZipOutputStream zipOutputStream) throws Exception {
        if (!sourceFile.isFile()) {
            return;
        }
        BufferedInputStream bufferedInputStream = null;
        try {
            String zipEntryName = sourceFile.getName();
            bufferedInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zipEntry.setTime(sourceFile.lastModified());
            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[BUFFER_SIZE];
            int cnt = 0;
            while ((cnt = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                zipOutputStream.write(buffer, 0, cnt);
            }
            zipOutputStream.closeEntry();
        } finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
        }
    }
}