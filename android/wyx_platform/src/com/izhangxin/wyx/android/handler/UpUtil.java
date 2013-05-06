package com.izhangxin.wyx.android.handler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.util.Log;

public class UpUtil {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // �???��?
	private static final String CHARSET = "utf-8"; // 设置�??

	/**
	 * android�????��?��??��?
	 * 
	 * @param file
	 *            ???�?????�?
	 * @param RequestURL
	 *            请�???ul
	 * @return �????????�?
	 */
	public static String uploadFile(File file, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边�???? ??????
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ???类�?

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // ???�??�?
			conn.setDoOutput(true); // ???�??�?
			conn.setUseCaches(false); // �??许使?��?�?
			conn.setRequestMethod("POST"); // 请�??��?
			conn.setRequestProperty("Charset", CHARSET); // 设置�??
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (file != null) {
				/**
				 * �??件�?为空�????��???并�?�??
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �?????注�?�?name??????为�??��?�??�?ey ???�?��key ???以�??��?�????��
				 * filename???件�????�?????�????�??:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ?��??????200=??? �??�??????��???????
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				// if(res==200)
				// {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
