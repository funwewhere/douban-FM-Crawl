package com.funwewhere.doubanfmcrawl.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class WebRequestUtil {
	
//	private static Log log = LogFactory.getLog(WebRequestUtil.class);
	
	private static CloseableHttpClient httpClient;
	
	private static String encode = "UTF-8";
	
	static{
		try {
			PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
	        //设置线程数最大100,如果超过100为请求个数
	        phccm.setMaxTotal(100);
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					//设置相信所有证书
					return true;
				}
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            httpClient = HttpClients.custom()
            		.setSSLSocketFactory(sslsf)
            		.setConnectionManager(phccm)
            		.build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
	}
	
	public static String requestByPost(String requsetUrl, Map<String, String> headers, Map<String, String> params) throws Exception{
		HttpPost post = new HttpPost(requsetUrl);
		
		//设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(15000)
                .setConnectTimeout(15000)
                .setSocketTimeout(15000)
                .build();
        post.setConfig(requestConfig);
        
		for(Entry<String, String> entry : headers.entrySet()){
			String value = entry.getValue();
			if (!StringUtils.isBlank(value)) {
				post.setHeader(entry.getKey(), value);
			}
		}
		
		List<NameValuePair> pairs = null;
		if (params != null && !params.isEmpty()) {
		    pairs = new ArrayList<NameValuePair>(params.size());
		    for (Map.Entry<String, String> entry : params.entrySet()) {
		        String value = entry.getValue();
		        if (!StringUtils.isBlank(value)) {
		            pairs.add(new BasicNameValuePair(entry.getKey(), value
		                    .toString()));
		        }
		    }
		}
		if (pairs != null && pairs.size() > 0) {
		    post.setEntity(new UrlEncodedFormEntity(pairs, encode));
		}
		HttpRequestThread requestThread = new HttpRequestThread(httpClient, post);
		requestThread.start();
		requestThread.join();
		return requestThread.call();
	}

	public static String requestByGet(String requsetUrl, Map<String, String> headers, Map<String, String> params) throws Exception{
		if (params != null && !params.isEmpty()) {
			StringBuilder builder = new StringBuilder(requsetUrl);
			builder.append("?");
		    for (Map.Entry<String, String> entry : params.entrySet()) {
		        String value = entry.getValue();
		        if (!StringUtils.isBlank(value)) {
		        	builder.append("&");
		        	builder.append(entry.getKey());
		        	builder.append("=");
		        	builder.append(value);
		        }
		    }
		    requsetUrl = builder.toString();
		}
	    
	    HttpGet get = new HttpGet(requsetUrl);
	    
	    //设置超时时间e
	    RequestConfig requestConfig = RequestConfig.custom()
	    		.setConnectionRequestTimeout(15000)
	    		.setConnectTimeout(15000)
	    		.setSocketTimeout(15000)
	    		.build();
	    get.setConfig(requestConfig);
	    
	    for(Entry<String, String> entry : headers.entrySet()){
	    	String value = entry.getValue();
	    	if (!StringUtils.isBlank(value)) {
	    		get.setHeader(entry.getKey(), value);
	    	}
	    }
        
		HttpRequestThread requestThread = new HttpRequestThread(httpClient, get);
		requestThread.start();
		requestThread.join();
		return requestThread.call();
	}
	
	public static InputStream getResponseStream(String fileUrl) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(fileUrl);
		CloseableHttpResponse response = httpClient.execute(get);
		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}
	
	public static boolean downloadFile(String fileUrl,String filePath) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = getResponseStream(fileUrl);
			File file = new File(filePath);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
			fos = new FileOutputStream(file);
			byte [] buffer=new byte[2048];
			int lengh = 0;
			while((lengh = is.read(buffer)) != -1){
				fos.write(buffer, 0, lengh);
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
		return true;
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}
	
}
