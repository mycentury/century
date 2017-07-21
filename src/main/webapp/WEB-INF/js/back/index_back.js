/**
 * 
 */
function list_files(progress_id, files_id) {
	var files = document.getElementById(files_id).files;
	var progress = $("#" + progress_id);
	progress.children().remove();
	for (var i = 0; i < files.length; i++) {
		var element = "<p class='progress_p'><span  id='file" + i
				+ "' class='file'>" + files[i].name
				+ "</span><span id='progress" + i
				+ "' class='progress'>0%</span></p>";
		if (i == 0) {
			progress.append(element);
		} else {
			$(element).appendTo(progress);
		}
	}
}

function update_progress(progress_id, percent) {
	$("#progress" + progress_id).css("background-size", percent + "% 100%")
			.css("-moz-background-size", percent + "% 100%");
	$("#progress" + progress_id).html(percent + "%");
}

var completed = false;

function start_upload(files_id) {
	var url = "file/upload.do";
	var fd = new FormData();
	var files = $("#" + files_id);
	fd.append("files", files);
	var success = function(msg) {
		alert(msg);
		alert(JSON.isPrototypeOf(msg));
		var result = JSON.parse(msg);
		var timer = setInterval(function() {
			get_progress(files_id);
			if (completed) {
				clearInterval(timer);
			}
		}, 1000);
	};
	var error = function(msg) {
		alert("error");
		alert(msg);
	};
	var beforeSend = function() {
	};
	var complete = function() {
	};
	uploadRequestByAjax(url, fd, beforeSend, success, complete, error);
}

function get_progress(files_id) {
	var url = "file/getProgress.do";
	var fd = new FormData();
	var files = $("#" + files_id);
	fd.append("files", files);
	var success = function(msg) {
		var result = JSON.parse(msg);
		var flag = true;
		for ( var key in result) {
			var value = result[key];
			alert(key + "," + value);
			update_progress(key, value);
			if (value != "100") {
				flag = false;
			}
		}
		completed = flag;
	};
	var error = function(msg) {
		alert(msg);
	};
	var beforeSend = function() {
	};
	var complete = function() {
	};
	uploadRequestByAjax(url, fd, beforeSend, success, complete, error);
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