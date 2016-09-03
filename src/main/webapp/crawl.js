DoubanCrawl = {
	pageIndex:1,
	pageSize:1,
	pageCount:30,
	songData:[],
	getSongInfo:function(cookieString){
		var data = "cookieString="+encodeURIComponent(cookieString);
		$.ajax({
	        type: "POST",
	        url: "getSongInfo",
	        data: data,
	        dataType: "json",
	        success: function(data){
	        	console.log(data);
	        	DoubanCrawl.songData = data;
	        	DoubanCrawl.pageSize = Math.floor(data.length/DoubanCrawl.pageCount);
	        	if(data.length%DoubanCrawl.pageCount > 0){
	        		++DoubanCrawl.pageSize;
	        	}
				DoubanCrawl.changePageIndex(1);
	        }
	    }); 
	},
	changePageIndex:function(pageIndex){
		pageIndex = pageIndex <= 1 ? 1 : pageIndex;
		pageIndex = pageIndex >= DoubanCrawl.pageSize ? DoubanCrawl.pageSize : pageIndex;
		var html = "<thead><tr><th class='first w1' style='width:3.2em;'></th><th style='width:50em;'><div class='wp'>标题</div></th><th class='w2-1' style='width:10em'><div class='wp'>歌手</div></th><th class='w3'><div class='wp'>时长</div></th></tr></thead><tbody>";
    	var start = (pageIndex - 1)*DoubanCrawl.pageCount;
    	var end = DoubanCrawl.songData.length < start +DoubanCrawl.pageCount ? DoubanCrawl.songData.length : start +DoubanCrawl.pageCount;
    	for(var i = start; i < end; ++i){
    		var minute =  Math.floor(DoubanCrawl.songData[i].length/60);
    		var second = DoubanCrawl.songData[i].length%60;
    		minute = DoubanCrawl.fillPosition(2, minute);
    		second = DoubanCrawl.fillPosition(2, second);
    		var artist = DoubanCrawl.songData[i].artist;
    		html += "<tr class='" + (i % 2 == 0?'even':'') + "'><td><span class='sn'>" + (i+1) +"</span></td><td><div class='title text'><span title='"+ DoubanCrawl.songData[i].title +"'>" + DoubanCrawl.songData[i].title + "</span></div></td>" +
				"<td><div class='artist text'><span title='"+ DoubanCrawl.songData[i].artist +"'>" + DoubanCrawl.songData[i].artist + "</span></div></td>" +
				"<td><span class='songtime'>" + minute + ":" + second + "</span>" +
				"<div class='hshow'><a href='" + DoubanCrawl.songData[i].url + "' target='_Blank'>试   听</a>" +
				"<a href=\"#\" target=\"_blank\" onclick=\"DoubanCrawl.download('"+DoubanCrawl.songData[i].artist +" - "+DoubanCrawl.songData[i].title+"', '"+DoubanCrawl.songData[i].url+"')\">下载</a></div></td></tr>";
    	}
		html += "</tbody>";
    	$(".data table").html(html);
    	$('#nowPageIndex').text(pageIndex);
	},
	download:function(songName, url){
		songName=encodeURIComponent(songName);
		url=encodeURIComponent(url);
		window.open("download?songName=" + songName + "&url=" + url);
	},
	fillPosition:function(pn, num){
		num = num.toString();
		while(num.length < pn){
			num = "0" + num;
		}
		return num;
	}
}

