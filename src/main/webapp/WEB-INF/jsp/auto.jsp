<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
</script>
</head>
<body onload="javascript:loginForm.submit();">
	<form id="loginForm" method="post" action="http://citrix.yun.hubs1.net/Citrix/XenApp/auth/login.aspx">
		用户名:<input type="text" name="user" id="user" value="wenge.yan" />
		密码:<input type="password" name="password" id="password" value="WenGe.Yan@GUpRe@093" />
		域:<input type="text" name="domain" id="domain" value="Hydrogen">
		<input type="hidden" name="SESSION_TOKEN" value="E9F61B0677D171808F8FFEBB1EF5D8E8">
		<input type="hidden" name="LoginType" value="Explicit">
		<input value="TEST" type="button" onclick="javascript:loginForm.submit();" />
	</form>
</body>
</html>