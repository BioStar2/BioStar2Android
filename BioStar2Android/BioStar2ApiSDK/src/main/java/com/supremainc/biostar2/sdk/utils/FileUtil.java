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
package com.supremainc.biostar2.sdk.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.supremainc.biostar2.sdk.provider.ConfigDataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileUtil {
	private static FileUtil mSelf = null;
	private static String TAG = "";
	private FileUtil() {
		TAG = getClass().getSimpleName();
	}

	public static FileUtil getInstance() {
		if (mSelf == null) {
			mSelf = new FileUtil();
		}
		return mSelf;
	}

	public void writeLog(String filename, String data) {
		if (ConfigDataProvider.DEBUG_SDCARD) {
			if (data == null) {
				return;
			}
			if (filename == null) {
				filename = "";
			}
			File file = new File(Environment.getExternalStorageDirectory() + "/err");
			if (!file.isDirectory()) {
				file.mkdirs();
				file.setWritable(true);
				file.setReadable(true);
			}
			bufferToFile(Environment.getExternalStorageDirectory() + "/err/w_" + filename + System.currentTimeMillis() + ".txt", data.getBytes());
		}
	}

	public void writeLogByte(String filename, byte[] data) {
		if (ConfigDataProvider.DEBUG_SDCARD) {
			if (data == null) {
				return;
			}
			if (filename == null) {
				filename = "";
			}
			File file = new File(Environment.getExternalStorageDirectory() + "/err");
			if (!file.isDirectory()) {
				file.mkdirs();
				file.setWritable(true);
				file.setReadable(true);
			}
			bufferToFile(Environment.getExternalStorageDirectory() + "/err/w_" + filename + System.currentTimeMillis() + ".txt", data);
		}
	}

	public boolean bufferToFile(String path, byte[] buffer) {
		if (isNull(path))
			return false;
		return bufferToFile(new File(path), buffer);
	}

	public boolean bufferToFile(File file, byte[] buffer) {
		try {
			file.createNewFile();
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
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

	public String getUrlencodeUTF8(String data) {
		String result = data;
		try {
			result = URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void createFolder(String folderpath) {
		File file = new File(folderpath);
		if (!file.isDirectory()) {
			file.mkdirs();
			file.setWritable(true);
			file.setReadable(true);
		}
	}

	public void deleteFolder(String path) {
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

	public boolean fileExists(String path) {
		if (null == path)
			return false;
		File file = new File(path);
		return file.exists();
	}

	public void fileExistsDelete(String path) {
		if (null == path)
			return;
		File file = new File(path);
		if (file.exists())
			file.delete();
	}

	public boolean isNull(String name) {
		if (null == name)
			return true;
		String diff = name.replace(" ", "");
		if (null == diff || diff.equals(""))
			return true;
		return false;
	}

	public String fileRead(String path) {
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

	public void saveFileObj(String fullpath, Object data) {
		File file = new File(fullpath);
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			if (file.exists())
				file.delete();
			file.createNewFile();
			fileOutputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(data);
			objectOutputStream.close();
		} catch (IOException e) {
			Log.e(TAG, "saveFileObj:"+e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null)
					objectOutputStream.close();
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Object loadFileObj(String fullPath) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Object result = null;
		File file = new File(fullPath);

		try {
			if (!file.exists())
				return null;
			fileInputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(fileInputStream);
			result = objectInputStream.readObject();
		} catch (Exception e) {
			Log.e(TAG, "loadFileObj:"+e.getMessage());
		} finally {
			try {
				if (objectInputStream != null)
					objectInputStream.close();
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "loadFileObj"+e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	public Object loadFileObj(Context context, String string) {
		String folder = context.getFilesDir().toString() + "/";
		return loadFileObj(folder + string);
	}

	public void saveFileObj(Context context, String string, Object data) {
		String folder = context.getFilesDir().toString() + "/";
		saveFileObj(folder + string, data);
	}

	public void fileExistsDelete(Context context, String path) {
		if (null == path)
			return;
		String folder = context.getFilesDir().toString() + "/";
		fileExistsDelete(folder + path);
	}

}
