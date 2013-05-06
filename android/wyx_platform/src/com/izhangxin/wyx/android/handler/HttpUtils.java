package com.izhangxin.wyx.android.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("unchecked")
public class HttpUtils {
	private static final String URL_PARAM_CONNECT_FLAG = "&";

	private static final int SIZE = 1024 * 1024;

	/**
	 * GET METHOD
	 * 
	 * @param strUrl
	 *            String
	 * @param map
	 *            Map
	 * @throws IOException
	 * @return List
	 */

	public static List URLGet(String strUrl, Map map) throws IOException {
		String strtTotalURL = "";
		List result = new ArrayList();
		if (strUrl.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
		System.out.println("========strtTotalURL========" + strtTotalURL);
		URL url = new URL(strtTotalURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		con.setFollowRedirects(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"), SIZE);
		while (true) {
			String line = in.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}
		in.close();
		return (result);
	}

	/**
	 * POST METHOD
	 * 
	 * @param strUrl
	 *            String
	 * @param content
	 *            Map
	 * @throws IOException
	 * @return List
	 */
	public static List URLPost(String strUrl, Map map) throws IOException {

		String content = "";
		content = getUrl(map);
		String totalURL = null;
		if (strUrl.indexOf("?") == -1) {
			totalURL = strUrl + "?" + content;
		} else {
			totalURL = strUrl + "&" + content;
		}

		System.out.println("===httpurl====="+totalURL);
		System.out.println(strUrl);
		URL url = new URL(strUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
		bout.write(content);
		bout.flush();
		bout.close();
		BufferedReader bin = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"), SIZE);
		List result = new ArrayList();
		while (true) {
			String line = bin.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}
		return (result);
	}

	/**
	 * @param map
	 *            Map
	 * @return String
	 */
	public static String getUrl(Map map) {
		if (null == map || map.keySet().size() == 0) {
			return ("");
		}
		StringBuffer url = new StringBuffer();
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = String.valueOf(i.next());
			if (map.containsKey(key)) {
				Object val = map.get(key);
				String str = val != null ? val.toString() : "";
				try {
					str = URLEncoder.encode(str, "GBK");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = "";
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}
	
	
	public static void main(String[] args){
//		String s ="http://www.17xunqin.com/xqnews/bdoPay.php?sel_orderno=20100709000000010236503940280132&result=2&pid=2365039&paytime=1278640280&gameid=1&sign=E975A97459BFB6C1C7F4D6A464353931&serverid=1&price=10.0&bankcode=&paytype=0&account=wanghubb&app_id=123455&channel=1012?";
//		System.out.println("http://www.17xunqin.com/xqnews/bdoPay.php?".length());
//		System.out.println("ssss".indexOf("?"));
//		System.out.println(s.indexOf("?"));
//		System.out.println(s.lastIndexOf("?"));
//		http://payment.hiigame.com/new/gateway/upt/face   face=http://61.172.241.234:8888/group1/M00/9C/CB/Pazx6lCboeYAAAAAAABECWLViHk835.png&pid=1201134337291667
//		Map<String,String> params = new Hashtable<String, String>();
//		params.put("face", "http://61.172.241.234:8888/group1/M00/9C/CB/Pazx6lCboeYAAAAAAABECWLViHk835.png");
//		params.put("pid", "1201134337291667");
//		String str_url = "http://payment.hiigame.com/new/gateway/upt/face";
//		try {
//			//ArrayList list = (ArrayList)HttpUtils.URLPost(str_url, params);
//			System.out.println(getUrl(params));
//			//System.out.println("::::::::::::::"+list.get(0));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//System.out.println("hello");
	}
}
