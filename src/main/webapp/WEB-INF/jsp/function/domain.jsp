<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>搬砖网</title>
	<%@include file="../common/js&css.jsp" %>
<%
	String result = (String)request.getAttribute("queryResult");
%>
</head>
<body>
<%@include file="../common/logo&menu.jsp" %>
	<div class="middle">
		<!--<div class="left_blank">left</div>-->
		<div class="center">
			<%@include file="../common/location.jsp" %>
			<form action="/queryDomain" method="post">
				<input id="domain" type="text" name="domain" />
				<input id="query" type="button" name="query" title="查询" value="查询" onclick="submit();" />
				<%= result==null?"" : result %>
				<input id="testAjax" type="button" name="testAjax" title="TEST" value="TEST" onclick="test();" />
			</form>
		</div>
		<!-- <div class="right_blank">right</div> -->
	</div>
	<%@include file="../common/bottom.jsp" %>
</body>
</html>