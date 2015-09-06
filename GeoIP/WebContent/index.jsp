<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MikroTik IP List generator</title>
</head>
<body>
	<p>
		You can Enter 1 Country code like TW, or a list of Country code as
		TW,JP,US,GB<br> This Web will automatically generate MikroTik
		readable list.
	</p>
	<input type="text" id="countries">
	<input type="button" id="nuke" value="Generate List">
	
	<div id="show"></div>
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
	<script>
		$("#nuke").click(processRequest);

		$("#countries").keypress(function(e) {
			if (e.which == 13) {
				processRequest();
			}
		})
		function processRequest() {
			var temp = $("#countries").val();
			//傳送temp()到servlet，取回IP列表。
		}
	</script>
</body>
</html>