DoubanCrawl = {
    getSongInfo:function(cookieString){
		var data = "cookieString="+encodeURIComponent(cookieString);
		$.ajax({
	        type: "POST",
	        url: "getSongInfo",
	        data: data,
	        dataType: "json",
	        success: function(data){
	        	console.log(data);
	        	var html = "";
	        	for(var i = 0; i < data.length; ++i){
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
	fillPosition:function(pn, num){
		num = num.toString();
		while(num.length < pn){
			num = "0" + num;
		}
		return num;
	}
}

