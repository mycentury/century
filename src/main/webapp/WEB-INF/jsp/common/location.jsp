<%@page import="cn.himma.util.service.CacheUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<p style="text-align: left;">
	当前位置:
	<%=CacheUtil.getLocation(request) %></a>
</p>
<h1>个性签名</h1>