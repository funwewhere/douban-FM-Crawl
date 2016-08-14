package com.fww.songcraw.main;

import com.fww.songcraw.util.DoubanUtil;

public class Main {

	public static void main(String[] args) {
		String getSongSids_cookie = "bid=CVMT/Ddhh2A; _vwo_uuid_v2=5CD8C88D1D126A764EF3F6CD9F4E4295|96f851a52a7b4a7b45da351dc6dae8d4; _pk_id.100002.6447=9ddd97adc42ab749.1461847252.1.1461847293.1461847252.; dbcl2=\"81617007:tpl6x9/yXL4\"; fmNlogin=\"y\"; ck=\"fCVy\"; _ga=GA1.2.1459738561.1461847252; _gat=1";
		String getSongInfos_cookie = getSongSids_cookie;
		String savePath = "E:/Download/doubanMusic";
		DoubanUtil.getSongInfosAndSave(getSongSids_cookie, getSongInfos_cookie, savePath);
		
//		DoubanUtil.downloadSongs(savePath);
	}
	
}
