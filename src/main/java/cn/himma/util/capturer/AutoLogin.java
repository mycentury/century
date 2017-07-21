/**
 * 
 */
package cn.himma.util.capturer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月17日
 * @ClassName AutoLogin
 */
public class AutoLogin {
	public static void main(String[] args) {
		StringBuffer buffer = new StringBuffer();
		try {
			/*
			 * Map<String, String> header = new HashMap<String, String>();
			 * Document post = Jsoup .connect(
			 * "http://citrix.yun.hubs1.net/Citrix/XenApp/auth/login.aspx")
			 * .data(header).post(); System.out.println(post);
			 */
			StringBuilder sb = new StringBuilder();
			sb.append("?").append("user").append("=").append("wenge.yan");
			sb.append("&").append("password").append("=")
					.append("WenGe.Yan@GUpRe@093");
			sb.append("&").append("domain").append("=").append("Hydrogen");
			sb.append("&").append("SESSION_TOKEN").append("=")
					.append("E9F61B0677D171808F8FFEBB1EF5D8E8");
			sb.append("&").append("LoginType").append("=").append("Explicit");
			URL url = new URL(
					"http://citrix.yun.hubs1.net/Citrix/XenApp/auth/login.aspx");
			// URL url = new URL(
			// "http://sctest.hubs1.net/sys/getDefaultFrame.action");
			URLConnection urlConnection = url.openConnection();
			((HttpURLConnection) urlConnection).setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(
					urlConnection.getOutputStream(), "UTF-8");
			osw.flush();
			osw.close();
			// 读取返回内容
			try {
				// 一定要有返回值，否则无法把请求发送给server端。
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream(), "UTF-8"));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(buffer.toString());
	}
}
