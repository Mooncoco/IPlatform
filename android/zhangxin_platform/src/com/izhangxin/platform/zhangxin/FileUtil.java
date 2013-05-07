package com.izhangxin.platform.zhangxin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;


import android.content.Context;

public class FileUtil {
	
	public static Context context;
	
	public static void setContext(Context context) {
		FileUtil.context = context;
	}
	
	/**
	 * 判断文件是否存在(/data/data/<应用程序>目录下文件)
	 * 注意要使用绝对路径
	 */
	public static boolean exitsFile(String fileName) {
		String path = context.getResources().getString(R.string.path);
		String packageName = context.getPackageName();
		File file = new File(path+"/"+packageName+"/"+fileName);
		if(!file.exists()){
			return false;
		}		
		return true;
	}
	
	/**
	 * 写入/data/data/<应用程序名>目录下文件:
	 */
	public static void writeFile(String fileName, String writestr) throws IOException {

		try {
			
			FileOutputStream fileOutputStream = context.openFileOutput(fileName,Context.MODE_PRIVATE);
			byte[] bytes = writestr.getBytes();
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取/data/data/<应用程序名>目录下的文件:
	 */
	public static String readFile(String fileName) throws IOException {
		
		String result = "";
		String lasttime = "";
		
		try {
			FileInputStream fileInputStream = context.openFileInput(fileName);
			int length = fileInputStream.available();
			byte[] buffer = new byte[length];
			fileInputStream.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String smsDetail[] = result.split("=");
		System.out.println(result);
		lasttime = smsDetail[1];		
		return lasttime;
	}

}
