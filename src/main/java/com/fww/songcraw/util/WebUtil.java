package com.fww.songcraw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class WebUtil {
	
//	private static Logger log = Logger.getLogger(WebUtil.class);
	//链接源代码
	private String reponseBody = "";
	//返回头信息
	private Header[] reponseHeaders = null;
	//连接超时时间
	private static int connectTimeOut = 10000;
	//连接读取时间
	private static int readTimeOut = 10000;
	//默认最大访问次数
	private static int maxConnectTimes = 3;
	//网页默认编码方式
	private static String charsetName = "iso-8859-1";
	//将HttpClient委托给MultiThreadedHttpConnectionManager，支持多线程
	private static MultiThreadedHttpConnectionManager httpConnectManager = new MultiThreadedHttpConnectionManager();
	
	private static HttpClient httpClient = new HttpClient(httpConnectManager);
	
	static {
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeOut);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeOut);
		//设置请求的编码格式
		httpClient.getParams().setContentCharset("utf-8");
	}
	
	public boolean requestByPost(String requsetUrl, Map<String, String> headers, Map<String, String> params){
		PostMethod method = createPostMethod(requsetUrl, headers, params);
		return executeMethod(method);
	}

	public boolean requestByGet(String requsetUrl, Map<String, String> headers, Map<String, String> params){
		GetMethod method = createGetMethod(requsetUrl, headers, params);
		return executeMethod(method);
	}
	
	public boolean downloadFile(String downloadUrl, String fileName, String savePath){
		GetMethod method = createGetMethod(downloadUrl, null, null);
		int index = downloadUrl.lastIndexOf(".");
		fileName = fileName.replaceAll("\\\\|/|:|\\*|\\?|\\\"|<|>|\\|", " ");
		String filePath = savePath + "/" + fileName + downloadUrl.substring(index);
		return executeDownload(method, filePath); 
	}

	private boolean executeDownload(GetMethod method, String newfilePath) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			File file = new File(newfilePath);
			if(file.exists()){
				return true;
			}
			if(httpClient.executeMethod(method) == HttpStatus.SC_OK){
				System.out.println("[" + newfilePath.substring(newfilePath.lastIndexOf("/")) + "]  开始下载.......");
				is = method.getResponseBodyAsStream();
				fos = new FileOutputStream(newfilePath);
				//创建文件输出流
				byte [] buffer=new byte[1024];//接收缓存
				int lengh = 0;
				while((lengh = is.read(buffer) ) > 0){//接收
					fos.write(buffer, 0, lengh);//写入文件
				}
				System.out.println("[" + newfilePath.substring(newfilePath.lastIndexOf("/")) + "]  下载成功!");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(fos != null){
					fos.flush();
					fos.close();
				}
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private PostMethod createPostMethod(String requsetUrl,
			Map<String, String> headers, Map<String, String> params) {
		PostMethod method = new PostMethod(requsetUrl);
		
		if(headers != null){
			Set<Entry<String, String>> headersSet = headers.entrySet();
			Iterator<Entry<String, String>> headersIterator = headersSet.iterator();
			while(headersIterator.hasNext()){
				Entry<String, String> next = headersIterator.next();
				method.setRequestHeader(next.getKey(), next.getValue());
			}
		}
		
		if(params != null){
			Set<Entry<String, String>> paramsSet = params.entrySet();
			Iterator<Entry<String, String>> paramsIterator = paramsSet.iterator();
			NameValuePair[] bodys = new NameValuePair[params.size()];
			int i = 0;
			while(paramsIterator.hasNext()){
				Entry<String, String> next = paramsIterator.next();
				bodys[i++] = new NameValuePair(next.getKey(), next.getValue());
			}
			method.setRequestBody(bodys);
		}
		
		return method;
	}
	
	private GetMethod createGetMethod(String requsetUrl,
			Map<String, String> headers, Map<String, String> params) {
		if(params != null){
			boolean first = true;
			Set<Entry<String, String>> paramsSet = params.entrySet();
			Iterator<Entry<String, String>> paramsIterator = paramsSet.iterator();
			while(paramsIterator.hasNext()){
				Entry<String, String> next = paramsIterator.next();
				if(first){
					requsetUrl += "?";
					first = false;
				} else {
					requsetUrl += "&";
				}
				requsetUrl += next.getKey() + "=" + next.getValue();
			}
		}
		
		GetMethod method = new GetMethod(requsetUrl);
		
		if(headers != null){
			Set<Entry<String, String>> headersSet = headers.entrySet();
			Iterator<Entry<String, String>> headersIterator = headersSet.iterator();
			while(headersIterator.hasNext()){
				Entry<String, String> next = headersIterator.next();
				method.setRequestHeader(next.getKey(), next.getValue());
			}
		}
		return method;
	}

	private boolean executeMethod(HttpMethod method) {
		try {
			if(httpClient.executeMethod(method) == HttpStatus.SC_OK){
				setReponseHeaders(method.getResponseHeaders());
				setReponseBody(method.getResponseBodyAsString());
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public String getReponseBody() {
		return reponseBody;
	}

	public void setReponseBody(String reponseBody) {
		this.reponseBody = reponseBody;
	}

	public Header[] getReponseHeaders() {
		return reponseHeaders;
	}

	public void setReponseHeaders(Header[] reponseHeaders) {
		this.reponseHeaders = reponseHeaders;
	}

	public static int getMaxConnectTimes() {
		return maxConnectTimes;
	}

	public static void setMaxConnectTimes(int maxConnectTimes) {
		WebUtil.maxConnectTimes = maxConnectTimes;
	}

	public static String getCharsetName() {
		return charsetName;
	}

	public static void setCharsetName(String charsetName) {
		WebUtil.charsetName = charsetName;
	}
}
