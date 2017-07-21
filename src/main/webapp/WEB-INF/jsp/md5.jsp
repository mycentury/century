<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%@include file="common/js&css.jsp" %>
<title>Insert title here</title>
<script type="text/javascript">
	function testMd5(text,ele_id){
		var url="md5/md5test.do";
		var other=function(msg){
			alert(msg);
		};
		$.ajax({
			url : encodeURI(url), // 请求的url地址
			/* dataType : "json", // 返回格式为json,可由SpringMVC定义 */
			async : true,// 请求是否异步，默认为异步，这也是ajax重要特性
			cache : false,
			data : {
				source:text
			}, // 参数值
			type : "POST", // 请求方式
			success : function(msg){
				document.getElementById(ele_id).innerHTML=msg;
			}
		});
	}
</script>
</head>
<body>
	<form action="md5/md5test.do" onsubmit="return false;">
		<input id="source" type="text" onkeypress="javascript:testMd5(this.value,'md5');" />
		<label id="md5"></label>
	</form>
</body>
</html>