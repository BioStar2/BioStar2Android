/*
 * Copyright 2015 Suprema(biostar2@suprema.co.kr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supremainc.biostar2.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileUtil {
    private final static String TAG = "FileUtil";

    public static boolean assetsFileCopy(Context context, String root, String folder, String filename) {
        createFolder(root + folder);
        AssetManager assetManager = context.getAssets();

        try {
            File mkFile = new File(root + folder + "/" + filename);
            if (!mkFile.exists()) {
                InputStream inputStream = assetManager.open(filename);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                mkFile.createNewFile();
                FileOutputStream fileOutputStream = null;
                BufferedOutputStream bufferedOutputStream = null;
                fileOutputStream = new FileOutputStream(mkFile);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int read = -1;
                byte[] buffer = new byte[1024];
                while ((read = bufferedInputStream.read(buffer, 0, 1024)) != -1) {
                    bufferedOutputStream.write(buffer, 0, read);
                }
                bufferedOutputStream.flush();
                fileOutputStream.close();
                bufferedOutputStream.close();
                inputStream.close();
                bufferedInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, " " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean BufferToFile(File file, byte[] bytes) {
        try {
            file.createNewFile();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean BufferToFile(String path, byte[] buffer) {
        if (isNull(path))
            return false;
        File file = new File(path);
        return BufferToFile(file, buffer);
    }

    public static boolean bufferToFileAppend(String path, byte[] buffer) {
        if (isNull(path)) {
            return false;
        }
        File newFile = new File(path);
        try {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(newFile, true);
                fileOutputStream.write(buffer);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void createFolder(String folderpath) {
        File folder = new File(folderpath);
        if (!folder.isDirectory()) {
            folder.mkdirs();
            folder.setWritable(true);
            folder.setReadable(true);
        }
    }

    public static void deleteFolder(String path) {
        File file = new File(path);
        if (file.exists() == false)
            return;

        File[] childFileList = file.listFiles();
        if (childFileList == null)
            return;
        for (File childFile : childFileList) {
            if (childFile.isDirectory()) {
                deleteFolder(childFile.getAbsolutePath());
            } else {
                childFile.delete();
            }
        }
        file.delete();
    }

    public static String getAssetsString(String path, Context context) {
        String result = null;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open(path);
        } catch (IOException exception) {
            inputStream = null;
        }
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        int fileSize = 0;
        if (inputStream != null) {
            try {
                fileSize = inputStream.available();
            } catch (IOException p_oIOException) {
                fileSize = 0;
            }
        }

        byte[] bytes = new byte[fileSize];
        int len = 0;
        try {
            while ((len = inputStream.read(bytes)) > 0) {
                byteOutStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result = new String(byteOutStream.toByteArray(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteFile(String fullpath) {
        File file = new File(fullpath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean fileExists(String path) {
        if (null == path)
            return false;
        File checkFile = new File(path);
        return checkFile.exists();
    }

    public static void fileExistsDelete(String path) {
        if (null == path)
            return;
        File file = new File(path);
        if (file.exists())
            file.delete();
    }

    public static String fileRead(String path) {
        if (null == path)
            return null;
        FileInputStream fileInputStream = null;
        byte buffer[];
        File file = new File(path);
        try {
            fileInputStream = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            while (fileInputStream.read(buffer) > 0) {

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String result = new String(buffer);
        return result;
    }

    public static byte[] fileReadBuffer(String path) {
        if (null == path)
            return null;
        FileInputStream fileInputStream = null;
        byte buffer[];
        File file = new File(path);
        try {
            fileInputStream = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            while (fileInputStream.read(buffer) > 0) {

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return buffer;
    }

    public static String getFileContent(String path, Context context) {
        String result = null;
        File srcFile = new File(path);
        if (!srcFile.exists()) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(srcFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        int fileSize = 0;
        if (inputStream != null) {
            try {
                fileSize = inputStream.available();
            } catch (IOException p_oIOException) {
                fileSize = 0;
            }
        }

        byte[] bytes = new byte[fileSize];
        int len = 0;
        try {
            while ((len = inputStream.read(bytes)) > 0) {
                byteStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            result = new String(byteStream.toByteArray(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getUrlencodeUTF8(String data) {
        String result = data;
        try {
            result = URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNull(String name) {
        if (null == name)
            return true;
        String diff = name.replace(" ", "");
        if (null == diff || diff.equals(""))
            return true;
        return false;
    }

    public static Object loadFileObj(String fullpath) {
        FileInputStream fileInputStream = null;
        ObjectInputStream is = null;
        Object result = null;
        File newFile = new File(fullpath);

        try {
            if (!newFile.exists())
                return null;
            fileInputStream = new FileInputStream(newFile);
            is = new ObjectInputStream(fileInputStream);
            result = is.readObject();

        } catch (Exception e) {
            Log.e(TAG, "loadFileObj " + e.getMessage());
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "loadFileObj " + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Object loadFileObj(Context context, String string) {
        String folder = context.getFilesDir().toString() + "/";
        return loadFileObj(folder + string);
    }

    public static void saveFileObj(Context context, String string, Object data) {
        String folder = context.getFilesDir().toString() + "/";
        saveFileObj(folder + string, data);
    }

    public static void saveFileObj(String fullpath, Object data) {
        File file = new File(fullpath);
        FileOutputStream fileOuputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            fileOuputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOuputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "saveFileObj " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (fileOuputStream != null)
                    fileOuputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
