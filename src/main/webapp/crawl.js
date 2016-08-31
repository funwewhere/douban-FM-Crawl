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
	        	var html = "";
	        	var start = 0;
	        	var end = data.length < DoubanCrawl.pageCount ? data.length : DoubanCrawl.pageCount;
	        	for(var i = start; i < end; ++i){
	        		var minute =  Math.floor(data[i].length/60);
	        		var second = data[i].length%60;
	        		minute = DoubanCrawl.fillPosition(2, minute);
	        		second = DoubanCrawl.fillPosition(2, second);
	        		var artist = data[i].artist;
	        		html += "<tr class='" + (i % 2 == 0?'even':'') + "'><td><span class='sn'>" + (i+1) +"</span></td><td>" + data[i].title + "</td><td><div class='artist text'><span title='"+ data[i].artist +"'>" + data[i].artist + "</span></div></td><td>" + minute + ":" + second + "</td><td><a href='" + data[i].url + "' target='_Blank'>试   听</a></td></tr>";
	        	}
	        	$(".data table").append(html);
	        }
	    }); 
	},
	changePageIndex:function(pageIndex){
		pageIndex = pageIndex <= 1 ? 1 : pageIndex;
		pageIndex = pageIndex >= DoubanCrawl.pageSize ? DoubanCrawl.pageSize : pageIndex;
		var html = "";
    	var start = (pageIndex - 1)*DoubanCrawl.pageCount;
    	var end = DoubanCrawl.songData.length < start +DoubanCrawl.pageCount ? DoubanCrawl.songData.length : start +DoubanCrawl.pageCount;
    	for(var i = start; i < end; ++i){
    		var minute =  Math.floor(DoubanCrawl.songData[i].length/60);
    		var second = DoubanCrawl.songData[i].length%60;
    		minute = DoubanCrawl.fillPosition(2, minute);
    		second = DoubanCrawl.fillPosition(2, second);
    		var artist = DoubanCrawl.songData[i].artist;
    		html += "<tr class='" + (i % 2 == 0?'even':'') + "'><td><span class='sn'>" + (i+1) +"</span></td><td>" + DoubanCrawl.songData[i].title + "</td><td><div class='artist text'><span title='"+ DoubanCrawl.songData[i].artist +"'>" + DoubanCrawl.songData[i].artist + "</span></div></td><td>" + minute + ":" + second + "</td><td><a href='" + DoubanCrawl.songData[i].url + "' target='_Blank'>试   听</a></td></tr>";
    	}
    	$(".data table").empty();
    	$(".data table").append(html);
    	$('#nowPageIndex').text(pageIndex);
	},
	fillPosition:function(pn, num){
		num = num.toString();
		while(num.length < pn){
			num = "0" + num;
		}
		return num;
	}
}

