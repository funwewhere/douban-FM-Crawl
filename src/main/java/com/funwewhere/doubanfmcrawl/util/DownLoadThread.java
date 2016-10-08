package com.funwewhere.doubanfmcrawl.util;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.funwewhere.doubanfmcrawl.bean.SongInfo;
import com.funwewhere.doubanfmcrawl.http.WebRequestUtil;

public class DownLoadThread implements Runnable {
	
//	private static List<SongInfo> songs = null;
	
	private BlockingQueue<SongInfo> songQueue;
	
	private String savePath;
	
	private ConcurrentHashMap<String, Integer> songMap;
	
	public DownLoadThread(List<SongInfo> songInfos, String savePath) {
		this.savePath = savePath;
		this.songQueue = new ArrayBlockingQueue<>(songInfos.size(), false, songInfos);
		this.songMap = new ConcurrentHashMap<>(songInfos.size());
	}

	@Override
	public void run() {
		SongInfo song;
		int i = 0;
		while ((song = songQueue.poll())!=null) {
			++i;
			String downloadUrl = song.getUrl();
			String fileName = song.getArtist() + " - " + song.getTitle();
			synchronized(songMap) {
				if(songMap.containsKey(fileName)){
					Integer integer = songMap.get(fileName);
					songMap.put(fileName, integer+1);
					fileName = fileName  + "(" + integer + ")";
				} else {
					songMap.put(fileName, 1);
				}
			}
			fileName = fileName.replaceAll("[/|\\\\|:|*|\"|?|<|>|\\|]", " ");
			System.out.println(Thread.currentThread().getName() + "[" + fileName +  "]  下载开始.....");
			String filePath = savePath + fileName + ".mp3";
			if(!WebRequestUtil.downloadFile(downloadUrl, filePath)) {
				System.out.println("[" + fileName + "]  下载失败!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			} else {
				System.out.println("[" + fileName +  "]  下载成功!");
			}
		}
		System.out.println(Thread.currentThread().getName() + "下载了" + i + "首歌！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！");
	}
	
//	@Override
//	public void run() {
//		int i = 0;
//		while(true){
//			boolean isfind = false;
//			for(SongInfo song : songs){
//				if(song.isDownloaded()){
//					continue;
//				}
//				synchronized (song) {
//					if(!song.isDownloaded()){
//						++i;
//						isfind = true;
//						song.setDownloaded(true);
//						String downloadUrl = song.getUrl();
//						String fileName = song.getArtist() + " - " + song.getTitle();
//						if(song.isHaveRepeatName()){
//							fileName += " " + song.getSid();
//						}
//						System.out.println("[" + fileName +  "]  下载开始.....");
//						String filePath = savePath + fileName + ".mp3";
//						if(!WebRequestUtil.downloadFile(downloadUrl, filePath)) {
//							System.out.println("[" + fileName + "]  下载失败!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//						} else {
//							System.out.println("[" + fileName +  "]  下载成功!");
//						}
//					}
//				}
//			}
//			if(!isfind){
//				break;
//			}
//		}
//		System.out.println("下载了" + i + "首歌！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！");
//	}
//
//	public static void setSongs(List<SongInfo> songInfos) {
//		songs = songInfos;
//	}

}
