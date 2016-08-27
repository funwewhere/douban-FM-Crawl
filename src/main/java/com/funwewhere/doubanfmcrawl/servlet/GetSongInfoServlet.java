package com.funwewhere.doubanfmcrawl.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.funwewhere.doubanfmcrawl.bean.SongInfo;
import com.funwewhere.doubanfmcrawl.util.DoubanUtil;

import net.sf.json.JSONArray;

public class GetSongInfoServlet extends HttpServlet{

	private static final long serialVersionUID = -2297763459184477458L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cookieString = req.getParameter("cookieString");
		System.out.println(cookieString);
		DoubanUtil douban;
		try {
			douban = new DoubanUtil(cookieString);
			List<SongInfo> songInfos= douban.getSongInfosAndSave();
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(JSONArray.fromObject(songInfos).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
