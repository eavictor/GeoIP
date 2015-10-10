<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MikroTik IP List generator</title>

<!-- Latest compiled and minified JavaScript -->
<script src="http://code.jquery.com/jquery-latest.min.js"></script>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

</head>
<body>
	<header></header>
	<article>
		<div class="col-lg-3"></div>
		<div class="col-lg-6">
			<div class="alert alert-success alert-dismissible" role="alert" id="myAlert" style="margin-top: 20px">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close" id="closeMyAlert">
					<span aria-hidden="true">&times;</span>
				</button>
				<strong>Notice!</strong> We use DB-IP's data, please visit their site for more information. <a href="https://db-ip.com/">DB-IP.com</a>
			</div>
			<div style="margin-top:20px">
				<ul class="nav nav-tabs nav-justified" role="tablist" id="myTabs">
					<li role="presentation" class="active"><a href="#generator" role="tab" data-toggle="tab">IP List Generator</a></li>
					<li role="presentation"><a href="#api" role="tab" data-toggle="tab">RESTful API</a></li>
				</ul>
			</div>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane fade in active" id="generator">
					<div style="margin-top: 50px">
						<div style="margin:50px">
							<h1 style="text-align:center">IP List Generator for MikroTik RouterOS</h1>
						</div>
						<form class="form-horizontal" method="get" action="MikroTik.do">
							<div class="form-group">
								<label for="countries" class="col-sm-2 control-label">Country
									Code:</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="countries"
										placeholder="EX: TW,US,JP,KR">
								</div>
							</div>
							<div class="radio col-sm-offset-2">
								<label class="inline-radio"> <input type="radio"
									name="listType" value="v4v6" checked="checked">Both
								</label> <label class="inline-radio"> <input type="radio"
									name="listType" value="v4">IPv4
								</label> <label class="inline-radio"> <input type="radio"
									name="listType" value="v6">IPv6
								</label>
							</div>
							<div></div>
							<div style="margin-top: 10px">
								<label class="col-sm-offset-2 control-label"> <input
									type="submit" id="nuke" class="btn btn-danger" value="Generate">
								</label>
							</div>
						</form>
					</div>
				</div>
				<div role="tabpanel" class="tab-pane fade" id="api">
					<div style="margin-top: 50px">
						<h1 class="text-primary">RESTful Service</h1>
						<h3 class="bg-primary">https://demo.eavictor.com/GeoIP/api/mikrotik/<strong>[both,ipv4,ipv6]</strong>/<strong>{countries}</strong></h3>
						<h3>/api</h3>
						<p class="text-primary">
							This is the root path of the RESTful service on this site, all RESTful services are under this path.
						</p>
						<h3>/mikrotik</h3>
						<p class="text-primary">
							This is the API for MikroTik RouterOS, we can get RouterOS readable IP lists by adding this into path.<br/>
							Currently I only developed API for RouterOS, if you need other APIs, please contact me.<br/>
						</p>
						<p class="text-primary">
							method : GET<br/>
							return : 200<br/>
							content type : text/plain
						</p>
						<h3>/[both,ipv4,ipv6]</h3>
						<p class="text-danger">
							Do not contain square brackets!!<br/>
						 	square brackets : [ ]
						</p>
						<p class="text-primary">
							You can choose the type of list you want to get.<br/>
							both : generate both IPv6 and IPv4.<br/>
							ipv4 : generate IPv4 only.<br/>
							ipv6 : generate IPv6 only.<br/>
						</p>
						<h3>/{countries}</h3>
						<p class="text-danger">
							Do not contain curly brackets!!<br/>
							curly brackets : { }
						</p>
						<p class="text-primary">
							Accept :<br/>
							1. 2 digits country code based on ISO 3166-1<br/>
							2. both upper case and lower case.<br/>
							3. multiple countries input divide by comma<br/>
							comma : ,
						</p><br/>
						<h1 class="text-primary">Examples</h1>
						<h3>List Type</h3>
						<p class="text-primary">
							both : https://demo.eavictor.com/GeoIP/api/mikrotik/<strong>both</strong>/TW<br/>
							IPv6 : https://demo.eavictor.com/GeoIP/api/mikrotik/<strong>ipv6</strong>/TW<br/>
							IPv4 : https://demo.eavictor.com/GeoIP/api/mikrotik/<strong>ipv4</strong>/TW
						</p>
						<h3>Multiple Countries</h3>
						<p class="text-primary">
							Upper Case : https://demo.eavictor.com/GeoIP/api/mikrotik/both/<strong>TW,JP,US,KR</strong><br/>
							Lower Case : https://demo.eavictor.com/GeoIP/api/mikrotik/both/<strong>tw,jp,us,kr</strong><br/>
							Mixed Case : https://demo.eavictor.com/GeoIP/api/mikrotik/both/<strong>TW,jp,US,kr</strong>
						</p>
					</div>
				</div>
			</div>
		</div>
		<div class="col-lg-3"></div>
		<script>
			$("#closeMyAlert").click(function() {
				$("#myAlert").fadeTo(1, 500).slideUp(500, function() {
					$("#myAlert").alert('close');
				});
			})
		</script>
	</article>
	<footer></footer>
</body>
</html>