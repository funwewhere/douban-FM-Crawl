function getSongInfo(){
	$.ajax({
        type: "POST",
        url: "getSongInfo",
        data: "cookieString="+$("input[name=cookieString]", ".form").val()+"&savePath="+$("input[name=savePath]", ".form").val(),
        dataType: "json",
        success: function(data){
        	console.log("Ya Da!!");
        }
    });
}