function getSongInfo(){
	var data = "cookieString="+encodeURIComponent($("input[name=cookieString]", ".form").val());
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
        		minute = fillPosition(2, minute);
        		second = fillPosition(2, second);
        		var artist = data[i].artist;
        		html += "<tr><td><span class='sn'>" + (i+1) +"</span></td><td>" + data[i].title + "</td><td><div class='artist text'><span title='"+ data[i].artist +"'>" + data[i].artist + "</span></div></td><td>" + minute + ":" + second + "</td><td><a href='" + data[i].url + "' rel='nofollow'>试   听</a></td></tr>";
        	}
        	$(".data table").append(html);
        }
    }); 
}

function fillPosition(pn, num){
	num = num.toString();
	while(num.length < pn){
		num = "0" + num;
	}
	return num;
}