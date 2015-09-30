<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MikroTik IP List generator</title>
</head>
<body>
	<p>
		This service uses DB-IP's data, please visit their site for more
		information. <a href="https://db-ip.com/">DB-IP.com</a> <br>
		<br> You can Enter 1 Country code like TW, or a list of Country
		code as TW,JP,US,GB<br> This Web will automatically generate
		MikroTik readable list.
	</p>
	<form method="get" action="MikroTik.do">
		<input type="text" id="countries" name="countries" title=""> <input
			type="submit" id="nuke" value="Generate List"><br> <input
			type="radio" name="listType" value="v4v6" checked="checked">Both<br>
		<input type="radio" name="listType" value="v4">IPv4<br> <input
			type="radio" name="listType" value="v6">IPv6
	</form>

	<p>
		RESTful API :<br> base URI + /api/ip/[both,v6,v4]/{country}<br>
		<br> both : returns IPv6 and IPv4 in one list<br> v6 :
		returns IPv6 list.<br> v4 : returns IPv4 list.<br> country :
		put the country codes into it, split by "," symbol.<br>
		<br> Example : <br> base URI + /api/ip/both/TW,JP<br>
		this will get TW and JP's IPv6 and IPv4 list.<br>

	</p>

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