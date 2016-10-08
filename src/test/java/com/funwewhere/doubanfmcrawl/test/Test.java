package com.funwewhere.doubanfmcrawl.test;

import com.funwewhere.doubanfmcrawl.util.DoubanUtil;

public class Test {

	public static void main(String[] args) {
		String cookieString = "bid=0WYKaYbNUAE; _pk_id.100002.6447=763a44682f6511d0.1470315253.7.1473442251.1473423722.; dbcl2=\"81617007:tbCq1QaEuic\"; fmNlogin=\"y\"; ck=uuDM; _gat=1; _vwo_uuid_v2=6387AB86CF8219D2A0B58A8FC8ECCAC1|43da4a3fd24dc327b679326e14ba9d30; _ga=GA1.2.1240051864.1470315253";
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
