/**
 * 
 */
function test() {
	// var url = encodeURI("/QueryServlet");
	var url = "/QueryServlet";
	var data = {
		"domain" : $("#domain").value()
	};
	beforeSend = function() {
		alert();
	};
	success = function(msg) {
		alert(msg);
	};
	complete = function() {
	};
	error = function() {
	};
	uploadRequestByAjax(url, data, beforeSend, success, complete, error);
}