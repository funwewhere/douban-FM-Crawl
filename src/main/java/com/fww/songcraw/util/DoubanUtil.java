package com.fww.songcraw.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fww.songcraw.bean.SongInfo;

public class DoubanUtil {
	
	private static Map<String,String> headers;
	
	private static HashMap<String, String> params;
	
	private static int increase = 1000;

	private static String getSongInfo_url = "http://douban.fm/j/v2/redheart/songs";

	private static String getSongSids_url = "http://douban.fm/j/v2/redheart/basic";

	private static List<SongInfo> songInfos = null;
	
	private String savePath = null;

	static{
		headers = new HashMap<String, String>();
		headers.put("Host", "douban.fm");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");
		headers.put("Referer", "http://douban.fm/mine");
		headers.put("Connection", "keep-alive");
//		headers.put("Content-Length", "206");
		headers.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
		headers.put("Origin", "https://douban.fm");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
//		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		
		params = new HashMap<String, String>();
		params.put("kbps", "128");
	}
	
	public DoubanUtil(String cookieString, String savePath) throws Exception {
		this.savePath = savePath;
		
		//设置cookie
		headers.put("Cookie", cookieString);
		
		//获取cookie[ck]的值
		Pattern pattern = Pattern.compile("ck=(.+?);");
	    Matcher matcher = pattern.matcher(cookieString);
	    if (matcher.find()) {
	    	params.put("ck", matcher.group(1));
	    } else {
	    	throw new Exception("not find cookie ck");
	    }
	}
	
	public void getSongInfosAndSave(){
		String[] songSids = getSongSids();
//		String[] songSids = {"1992019","1454157","154954","1792548"};
		getSongInfos(songSids);
		saveSongInfos();
	}
	
	private String[] getSongSids(){
		WebUtil webUtil = new WebUtil();
		webUtil.requestByGet(getSongSids_url, headers, null);
		
		JSONObject json = JSONObject.fromObject(webUtil.getReponseBody());
		JSONArray jsonSongs = json.getJSONArray("songs");
		
		String[] songSids = new String[jsonSongs.size()];
		
		int i = 0;
		Iterator iterator = jsonSongs.iterator();
		while(iterator.hasNext()){
			JSONObject next = (JSONObject) iterator.next();
			String sid = next.getString("sid");
			songSids[i++] = sid;
		}
		
		System.out.println("一共" + i + "首歌");
		return songSids;
	}
	
	private void getSongInfos(String[] songSids){
		songInfos = new ArrayList<SongInfo>();
		for (int index = 0; index < songSids.length; index += increase) {
			String sids = "";
			for (int i = index; i < increase; ++i) {
				if(i >= songSids.length){
					break;
				}
				sids += songSids[i] + "|";
			}
			if (sids.endsWith("|")) {
				sids = sids.substring(0, sids.length() - 1);
			}
			getSongInfo(sids);
		}
		
		for(SongInfo song : songInfos){
			String str = song.getArtist() + song.getTitle();
			for(SongInfo song2 : songInfos){
				if(song == song2 || song2.isHaveRepeatName()){
					continue;
				}
				String str2 = song2.getArtist() + song2.getTitle();
				if(str2.equals(str)){
					song.setHaveRepeatName(true);
					song2.setHaveRepeatName(true);
				}
			}
		}
	}

	private void getSongInfo(String sids) {

		params.put("sids", sids);
		
		WebUtil webUtil = new WebUtil();
		if(webUtil.requestByPost(getSongInfo_url, headers, params)){
			JSONArray jsonArray = JSONArray.fromObject(webUtil.getReponseBody());
			for (int j = 0; j < jsonArray.size(); ++j) {
				JSONObject jsonObject = jsonArray.getJSONObject(j);
				SongInfo songInfo = new SongInfo();
				songInfo.setSid(jsonObject.getString("sid"));
				songInfo.setArtist(jsonObject.getString("artist"));
				songInfo.setUrl(jsonObject.getString("url"));
				songInfo.setTitle(jsonObject.getString("title"));
				songInfos.add(songInfo);
			}
		} else {
			System.out.println("请求" + sids + "失败!!");
		}
	}
	
	private boolean saveSongInfos(){
		File file = new File(savePath + "/douban.txt");
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.println("一共" + songInfos.size() + "首歌");
			for(SongInfo song : songInfos){
				pw.println("----------------------------------------------------------------------------");
				pw.println(song.getArtist() + " - " + song.getTitle());
				pw.println(song.getSid());
				pw.println(song.getUrl());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(pw !=null){
				pw.flush();
				pw.close();
			}
		}
	}

	public void downloadSongs(String savePath) {
		DownLoadThread.setSongs(songInfos);
		for(int i = 0; i < 3; ++i){
			DownLoadThread downLoadThread = new DownLoadThread(savePath);
			Thread thread = new Thread(downLoadThread, "thread" + i);
			thread.start();
		}
		System.out.println("成功下载了" + songInfos.size()+ "首歌");
	}

}
