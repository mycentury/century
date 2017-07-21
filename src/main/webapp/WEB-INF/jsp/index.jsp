<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="cn.himma.util.constant.ArrayConstant"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>搬砖网</title>
	<%@include file="common/js&css.jsp" %>
	<%
		List<String> fileNames = (ArrayList<String>)request.getAttribute("files");
	%>
</head>
<body onload="get_emojies('qq', 'emoji_pics');">
	<canvas id="magnetism" width="100%" height="100%"></canvas>
	<script type="text/javascript" src="js/nest.js"></script>
	<%@include file="common/logo&menu.jsp" %>
	<div class="middle">
		<!--<div class="left_blank">left</div>-->
		<div class="center">
			<%@include file="common/location.jsp" %>
			<p>我是一名搬攻城师，我为自己带烟！如果你一定要叫我“搬砖的”，请把前面那个“死”字去掉！</p>
        	<!-- <p>
	        	To view this page ensure that Adobe Flash Player version 
				10.0.0 or greater is installed. 
			</p> -->
			<div id="edit_div" class="edit" contenteditable="true">
			</div>
			<div>
				<div style="width: 600px;height: 25px;">
					<img align="left" src="img/emoji.png" class="hidable_trigger" onmouseover="show_by_id('emojies');" onmouseleave="hide_by_id('emojies')" />
					<img align="left" src="img/font.png" class="hidable_trigger" onmouseover="show_by_id('font_options');" onmouseleave="hide_by_id('font_options')" />
					<input align="right" style="display: inline;" type="button" value="提交" onclick="alert('暂不支持！')" />
				</div>
				<div id="emojies" class="emojies" onmouseover="show_by_id('emojies');" onmouseleave="hide_by_id('emojies')">
					<div id="emoji_pics">
						<%--for(String emoji:emojies) {
							<img src="emoji/<%=emoji %>.png" class="emoji" onclick="javascript:edit_div.innerHTML+=this.outerHTML;" />
						<%} --%>
					</div>
					<div>
						<input value="经典" type="button" onclick="get_emojies('qq','emoji_pics');" />
						<input value="微信" type="button" onclick="get_emojies('weixin','emoji_pics');" /><!-- 
						<input value="国旗" type="button" onclick="get_emojies('flag','emoji_pics');" /> -->
					</div>
				</div>
				<div id="font_options" class="hidable" style="width: 600px;height: 120px;" onmouseover="show_by_id('font_options');" onmouseleave="hide_by_id('font_options');">
						颜色：
					<div style="display: inline-block;vertical-align:top;">
						<label id="color_selected" style="background-color: black;border: thin black solid;" onmouseover="show_by_id('color_select');" onmouseleave="hide_by_id('color_select');">&nbsp;&nbsp;</label>
						<ul id="color_select" class="hidable_select" onmouseover="show_by_id('color_select');" onmouseleave="hide_by_id('color_select');">
							<li style="background-color: black;" onclick="change_bg_color('color_selected','black');change_font_color('edit_div','black');">&nbsp;&nbsp;</li>
							<li style="background-color: red;" onclick="change_bg_color('color_selected','red');change_font_color('edit_div','red');">&nbsp;&nbsp;</li>
							<li style="background-color: green;" onclick="change_bg_color('color_selected','green');change_font_color('edit_div','green');">&nbsp;&nbsp;</li>
							<li style="background-color: blue;" onclick="change_bg_color('color_selected','blue');change_font_color('edit_div','blue');">&nbsp;&nbsp;</li>
							<li  onclick="show_window('window');">more</li>
						</ul>
					</div>
					大小： 
					<select id="size" onchange="change_font_size('edit_div',this.value);" >
						<option value="12" selected="selected">小</option>
						<option value="18">中</option>
						<option value="24">大</option>
						<option value="8">微</option>
						<option value="28">超</option>
					</select>
				</div>
				<div id="window" style="display: none;" onmouseover="show_by_id('font_options');" onmouseleave="hide_by_id('font_options','slow');">
					<%
						for(String colour:ArrayConstant.COLOURS){
						    colour = "#"+colour;
					%>
					<span style="cursor: pointer;background-color: <%=colour %>;" onclick="change_bg_color('color_selected','<%=colour %>');change_font_color('edit_div','<%=colour %>');hide_window('window');">&nbsp;&nbsp;</span>
					<%} %>
					<input value="关闭" type="button" onclick="hide_window('window');"/>
				</div>
			</div>
			
	   		<form id="form_one">
	   			<input id="files" name="files" multiple="multiple" class="file" type="file" onchange="list_files('progress_bar',this.id,'submit')" />
	            <input id="submit" class="button" type="button" value="开始上传" disabled="true" onclick="start_upload('form_one','files',this.id);" />
	   		</form>
	   		<div id="progress_bar">
	   		</div>
	   		<%
	   			if(fileNames!=null && !fileNames.isEmpty()){
			    String params = "";
			    for(int i=0;i<fileNames.size();i++){
			        if(i>0) {
			            params+="&";
			        }
			    	params+="files="+fileNames.get(i);
			    }
	   			    
	   		%>
	   		<a href="file/download.do?<%=params %>" >点击下载</a>
	   		<%} %>
		</div>
		<!-- <div class="right_blank">right</div> -->
	</div>
	<%@include file="common/bottom.jsp" %>
</body>
</html>