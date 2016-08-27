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
        		html += "<tr><td>" + data[i].artist + " - " + data[i].title + "</td><td><a href='" + data[i].url + "' rel='nofollow'>试   听</a></td></tr>";
        	}
        	$(".data table").append(html);
        }
    }); 
}