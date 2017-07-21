function uploadRequestByAjax(url, data, beforeSend, success, complete, error) {
	$.ajax({
		url : url, // 请求的url地址
		// dataType : "json", // 返回格式为json,可由SpringMVC定义
		async : true,// 请求是否异步，默认为异步，这也是ajax重要特性
		cache : false,
		data : data, // 参数值
		type : "POST", // 请求方式
		contentType : false,
		processData : false,// 必须
		beforeSend : beforeSend,
		success : success,
		complete : complete,
		error : error
	});
}

function normalRequestByAjax(url, data, beforeSend, success, complete, error) {
	$.ajax({
		url : url, // 请求的url地址
		// dataType : "json", // 返回格式为json,可由SpringMVC定义
		async : true,// 请求是否异步，默认为异步，这也是ajax重要特性
		cache : false,
		data : data, // 参数值
		type : "POST", // 请求方式
		// contentType : false,
		// processData : false,// 必须
		beforeSend : beforeSend,
		success : success,
		complete : complete,
		error : error
	});
}

function getCurrentTime() {
	var now = new Date();
	var time = now.getFullYear() + "年" + now.getMonth() + "月" + now.getDate()
			+ "日 " + now.getHours() + ":" + now.getMinutes() + ":"
			+ now.getSeconds() + "," + now.getMilliseconds() + " "
			+ now.getDay();
	return time;
}

function createTraditionalAjax() {
	var xhr;
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		xhr = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xhr;
}

function requestByTraditionalAjax(xhr, method, url, no_sync, data, success,
		error) {
	xhr.onreadystatechange = function(msg) {
		if (xhr.readyState == 4 && xhr.status == 200) {
			success(xhr.responseText);
		}
		if (xhr.readyState == 4 && xhr.status != 200) {
			error(xhr.responseText);
		}
	}
	xhr.open(method, url, no_sync);
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.send(data);
}

String.prototype.startWith = function(str) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	if (this.substr(0, str.length) == str)
		return true;
	else
		return false;
	return true;
}
String.prototype.endWith = function(str) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	if (this.substring(this.length - str.length) == str)
		return true;
	else
		return false;
	return true;
}

function get_type(obj) {
	return Object.prototype.toString.call(obj);
}

function generateRandomString(len) {
	var array = [ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
			'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' ];
	var result = "";
	for (var i = 0; i < len; i++) {
		result += array[Math.round(Math.random() * 62)];
	}
	return result;
}
