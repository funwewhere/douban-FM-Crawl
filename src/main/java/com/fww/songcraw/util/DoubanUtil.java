package com.fww.songcraw.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	static{
		headers = new HashMap<String, String>();
		headers.put("Host", "douban.fm");
//		headers.put("Connection", "keep-alive");
//		headers.put("Content-Length", "206");
//		headers.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
//		headers.put("Origin", "http://douban.fm");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
//		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Referer", "http://douban.fm/mine");
//		headers.put("Accept-Encoding", "gzip, deflate");
//		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		
		params = new HashMap<String, String>();
		params.put("kbps", "320");
		params.put("ck", "fCVy");
	}
	
	public static void getSongInfosAndSave(String getSongSids_cookie, String getSongInfos_cookie, String savePath){
		String[] songSids = getSongSids(getSongSids_cookie);
//		String[] songSids = {"174646|967826|186827|1030249|1002131|280187|107686"};
		songInfos  = getSongInfos(songSids, getSongInfos_cookie);
		
		saveSongInfos(songInfos, savePath);
	}
	
	private static String[] getSongSids(String cookie){
		headers.put("Cookie", cookie);
		
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
	
	private static List<SongInfo> getSongInfos(String[] songSids, String cookie){
		headers.put("Cookie", cookie);
		
		List<SongInfo> songs = new ArrayList<SongInfo>();
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
			
			getSongInfo(songs, sids);
			
		}
		

		for(SongInfo song : songs){
			String str = song.getArtist() + song.getTitle();
			for(SongInfo song2 : songs){
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
		
		return songs;
	}

	private static void getSongInfo(List<SongInfo> songs, String sids) {

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
				songs.add(songInfo);
			}
		} else {
			System.out.println("请求" + sids + "失败!!");
		}
	}
	
	private static boolean saveSongInfos(List<SongInfo> songInfos, String savePath){
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

	public static void downloadSongs(String savePath) {
		DownLoadThread.setSongs(songInfos);
		for(int i = 0; i < 3; ++i){
			DownLoadThread downLoadThread = new DownLoadThread(savePath);
			Thread thread = new Thread(downLoadThread, "thread" + i);
			thread.start();
		}
		System.out.println("成功下载了" + songInfos.size()+ "首歌");
	}

}
