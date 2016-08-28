package com.funwewhere.doubanfmcrawl.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.funwewhere.doubanfmcrawl.bean.SongInfo;
import com.funwewhere.doubanfmcrawl.http.WebRequestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DoubanUtil {
	
	private static Map<String,String> headers;
	
	private static HashMap<String, String> params;
	
	private static int increase = 50;

	private static String getSongInfo_url = "https://douban.fm/j/v2/redheart/songs";

	private static String getSongSids_url = "https://douban.fm/j/v2/redheart/basic";

	private static List<SongInfo> songInfos = null;
	
	private static JSONArray responseJson = new JSONArray();
	
	static{
		headers = new HashMap<>();
		headers.put("Host", "douban.fm");
		headers.put("Connection", "keep-alive");
		headers.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Referer", "https://douban.fm/");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
//		headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
//		headers.put("Content-Length", "212");
//		headers.put("Origin", "https://douban.fm");
		
		params = new HashMap<String, String>();
		params.put("kbps", "320");
	}
	
	public DoubanUtil(String cookieString) throws Exception {
		//设置cookie
		headers.put("Cookie", cookieString);
		
		//获取cookie[ck]的值
		Pattern pattern = Pattern.compile("ck=(.+?);");
	    Matcher matcher = pattern.matcher(cookieString);
	    if (matcher.find()) {
	    	String group = matcher.group(1);
	    	System.out.println(group);
	    	params.put("ck", group);
	    } else {
	    	throw new Exception("not find cookie ck");
	    }
	}
	
	public JSONArray getSongInfosAndSave() throws Exception{
		String[] songSids = getSongSids();
//		String[] songSids = "2601719|1992019|1454157|2091696|1387152|1957265|475519|1464354|1382824|1644945|379650|154954|1792548|1451365|2235199|18497|321204|549583|1615839|2088643".split("\\|");
		getSongInfos(songSids);
		return responseJson;
	}
	
	private String[] getSongSids() throws Exception{
		String reponseBody = WebRequestUtil.requestByGet(getSongSids_url, headers, null);
		JSONObject json = JSONObject.fromObject(reponseBody);
		JSONArray jsonSongs = json.getJSONArray("songs");
		
		String[] songSids = new String[jsonSongs.size()];
		
		int i = 0;
		Iterator<?> iterator = jsonSongs.iterator();
		while(iterator.hasNext()){
			JSONObject next = (JSONObject) iterator.next();
			String sid = next.getString("sid");
			songSids[i++] = sid;
		}
		System.out.println("一共" + i + "首歌");
		return songSids;
	}
	
	private void getSongInfos(String[] songSids) throws Exception{
		songInfos = new ArrayList<SongInfo>();
		int sidLength = songSids.length;
		int length = 0;
		responseJson.clear();
		for (int index = 0; index < sidLength; index += increase) {
			String sids = "";
			length = index + increase;
			if(length > sidLength){
				length = sidLength;
			}
			for (int i = index; i < length; ++i) {
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

	private void getSongInfo(String sids) throws Exception {

		params.put("sids", sids);
		String reponseBody = WebRequestUtil.requestByPost(getSongInfo_url, headers, params);
		JSONArray jsonArray = JSONArray.fromObject(reponseBody);
//		for (int j = 0; j < jsonArray.size(); ++j) {
//			JSONObject jsonObject = jsonArray.getJSONObject(j);
//			SongInfo songInfo = new SongInfo();
//			songInfo.setSid(jsonObject.getString("sid"));
//			songInfo.setArtist(jsonObject.getString("artist"));
//			songInfo.setUrl(jsonObject.getString("url"));
//			songInfo.setTitle(jsonObject.getString("title"));
//			songInfos.add(songInfo);
//		}
		responseJson.addAll(jsonArray);
	}
	
	private boolean saveSongInfos(String savePath){
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
