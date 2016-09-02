package com.funwewhere.doubanfmcrawl.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.funwewhere.doubanfmcrawl.http.WebRequestUtil;

public class DownloadServlet extends HttpServlet{

	private static final long serialVersionUID = -2297763459184477458L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = URLDecoder.decode(req.getParameter("songName"), "utf-8");
		String fileUrl = URLDecoder.decode(req.getParameter("url"), "utf-8");
		String encodeName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&").replaceAll("%2C", "\\,");
        resp.setContentType("application/x-msdownload");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Content-disposition", "attachment; filename=" + encodeName + ".mp3");
        
        InputStream is = null;
		ServletOutputStream fos = null;
		try {
			System.out.println("[" + fileName +  "]  下载begin.....");
			CloseableHttpClient httpClient = WebRequestUtil.getHttpClient();
			HttpGet get = new HttpGet(fileUrl);
			CloseableHttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			fos = resp.getOutputStream();
			//创建文件输出流
			byte [] buffer=new byte[1024];//接收缓存
			int lengh = 0;
			while((lengh = is.read(buffer) ) > 0){//接收
				fos.write(buffer, 0, lengh);//写入文件
			}
			System.out.println("[" + fileName +  "]  下载成功!");
		} catch (Exception e) {
			e.printStackTrace();
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
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
