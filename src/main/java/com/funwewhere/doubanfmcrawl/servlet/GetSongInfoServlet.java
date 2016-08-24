package com.funwewhere.doubanfmcrawl.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.funwewhere.doubanfmcrawl.util.DoubanUtil;

public class GetSongInfoServlet extends HttpServlet{

	private static final long serialVersionUID = -2297763459184477458L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cookieString = req.getParameter("cookieString");
		String savePath = req.getParameter("savePath");
		
		System.out.println(cookieString);
		System.out.println(savePath);
		
//		boolean flag = false;
//		DoubanUtil douban;
//		try {
//			douban = new DoubanUtil(cookieString, savePath);
//			douban.getSongInfosAndSave();
//			if (flag) {
//				douban.downloadSongs(savePath);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
