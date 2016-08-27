package com.funwewhere.doubanfmcrawl.http;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpRequestThread extends Thread implements Callable<String>{

    private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final HttpRequestBase httpMethod;
    private String result = null;
    
    public HttpRequestThread(CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws UnsupportedEncodingException {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpMethod = httpMethod;
    }

    @Override
    public void run() {
        try {
            CloseableHttpResponse response = httpClient.execute(httpMethod, context);
            try {
                // get the response body as an array of bytes
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
//                    System.out.println("：：：执行结果：：：" + result);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public String call() throws Exception {
        return result;
    }
}
