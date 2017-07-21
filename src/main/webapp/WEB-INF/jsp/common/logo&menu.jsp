<%@page import="cn.himma.util.service.CacheUtil"%>
<%@page import="cn.himma.entity.MenuEntity"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	List<MenuEntity> parents = CacheUtil.getCacheMenuList();
	String username = (String)session.getAttribute("username");
%>
	<div class="top">
		<div class="logo">
			<img alt="logo加载失败" title="www.banzhuangong.cn" src="img/logo.jpg" height="100%" />
			<img alt="logo加载失败" title="www.banzhuangong.cn" src="img/welcome.jpg" height="100%" />
		</div> 
		<div class="info">
		<%if(username==null){ %>
			<a href="">登录</a>
			<a href="">注册</a>
		<%}else{ %>
			欢迎你，
			<a href=""><%=username %></a>
		<%} %>
		</div>
		<div class="menu">
		<%if(parents!=null&&!parents.isEmpty()){ %>
			<ul class="menu_ul">
			<%for(MenuEntity parent: parents){ %>
				<li class="<%=request.getRequestURI().startsWith(parent.getCode())?"menu_li_choose":"menu_li" %>" onmouseover="show_by_id('menu_<%=parent.getCode() %>');" onmouseleave="hide_by_id('menu_<%=parent.getCode() %>');" >
					<a href="menu.do?menu=<%=parent.getCode() %>" ><%=parent.getName() %></a>
					<ul id="menu_<%=parent.getCode() %>" style="display: none;text-align: left;background-color: #9999ff;" onmouseover="show_by_id('menu_<%=parent.getCode() %>');" onmouseleave="hide_by_id('menu_<%=parent.getCode() %>');">
				<%if(parent.getChildren()!=null&&!parent.getChildren().isEmpty()){ %>
					<%for(MenuEntity child: parent.getChildren()){ %>
						<li class="<%=request.getRequestURI().startsWith("/"+child.getCode())?"child_menu_li_choose":"child_menu_li" %>"><a href="menu.do?menu=<%=child.getCode() %>" ><%=child.getName() %></a></li>
					<%} %>
				<%} %>
					</ul>
				</li>
			<%} %>
			</ul>
		<%} %>
		</div>
	</div>