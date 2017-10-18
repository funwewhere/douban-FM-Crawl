package com.funwewhere.doubanfmcrawl.test;

import com.funwewhere.doubanfmcrawl.util.DoubanUtil;
import org.apache.http.annotation.ThreadSafe;

import java.util.Collections;

@ThreadSafe
public class Test {

	public static void main(String[] args) {
		String cookieString = "bid=zjczQHGElqk; _vwo_uuid_v2=90E2DA74EEC4E49DA1471DC24A5C13AB|3de83ef7891ecd857134cf987aeb38eb; flag=\"ok\"; dbcl2=\"81617007:uqKZMXpGGE4\"; ck=HwMw; show_pro_init_tip=Y; _gat=1; _ga=GA1.2.565992626.1484551042; _gid=GA1.2.225944833.1508249794";
		String savePath = "E:\\Download\\doubanMusic\\";
		DoubanUtil douban;
		try {
			douban = new DoubanUtil(cookieString);
			douban.getSongInfosAndSave();
			douban.downloadSongs(savePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
