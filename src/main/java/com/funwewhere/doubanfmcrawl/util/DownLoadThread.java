package com.funwewhere.doubanfmcrawl.util;

import java.util.List;

import com.funwewhere.doubanfmcrawl.bean.SongInfo;

public class DownLoadThread implements Runnable {
	
	private static List<SongInfo> songs = null;
	
	private String savePath = null;
	
	public DownLoadThread(String savePath) {
		this.savePath = savePath;
	}
	
	@Override
	public void run() {
//		WebUtil webUtil = new WebUtil();
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
//						if(!webUtil.downloadFile(downloadUrl, fileName, savePath)) {
//							System.out.println("[" + fileName + "]  下载失败!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//						}
//					}
//				}
//			}
//			if(!isfind){
//				break;
//			}
//		}
//		System.out.println("下载了" + i + "首歌！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！6！");
	}

	public static void setSongs(List<SongInfo> songInfos) {
		songs = songInfos;
	}

}
