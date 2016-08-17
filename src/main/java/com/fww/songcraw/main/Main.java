package com.fww.songcraw.main;

import com.fww.songcraw.util.DoubanUtil;

public class Main {

	public static void main(String[] args) {
		String cookieString = "bid=0WYKaYbNUAE; _pk_id.100002.6447=763a44682f6511d0.1470315253.3.1471351254.1470476978.; dbcl2=\"81617007:F2+wiezlxSg\"; fmNlogin=\"y\"; ck=E1Kz; _vwo_uuid_v2=6387AB86CF8219D2A0B58A8FC8ECCAC1|43da4a3fd24dc327b679326e14ba9d30; _ga=GA1.2.1240051864.1470315253; _gat=1";
		String savePath = "E:/Download/doubanMusic";
		boolean flag = true;
		DoubanUtil douban;
		try {
			douban = new DoubanUtil(cookieString, savePath);
			douban.getSongInfosAndSave();
			if (flag) {
				douban.downloadSongs(savePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
