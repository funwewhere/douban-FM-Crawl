package com.funwewhere.doubanfmcrawl.bean;

public class SongInfo {
	private String artist;
	
	private String url;
	
	private String title;
	
	private boolean isDownloaded = false;
	
	private String sid;
	
	private boolean haveRepeatName = false;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public boolean isHaveRepeatName() {
		return haveRepeatName;
	}

	public void setHaveRepeatName(boolean haveRepeatName) {
		this.haveRepeatName = haveRepeatName;
	}
	
}
