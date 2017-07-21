/**
 * 
 */
function list_files(progress_id, files_id, submit_id) {
	var file_input = document.getElementById(files_id);
	var files = file_input.files;
	var progress = $("#" + progress_id);
	progress.children().remove();
	if (files.length == 0) {
		$("#" + submit_id).attr("disabled", true);
	} else {
		$("#" + submit_id).attr("disabled", false);
	}
	var size = 0;
	for (var i = 0; i < files.length; i++) {
		size += files[i].size;
	}
	var max_size = 1000 * 1024 * 1024;
	if (size > max_size) {
		file_input.outerHTML = file_input.outerHTML;
		file_input.value = "";
		return false;
	}
	for (var i = 0; i < files.length; i++) {
		var element = "<p class='progress_p'><span id='file" + i
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
	$("#" + progress_id).css("background-size", percent + "% 100%").css(
			"-moz-background-size", percent + "% 100%");
	$("#" + progress_id).html(percent + "%");
}

function start_upload(form_id, files_id, submit_id) {
	var timer = null;
	var submit = $("#" + submit_id)[0];
	var url = "file/upload.do";
	var fd = new FormData($("#" + form_id)[0]);
	var success = function(msg) {
		if (get_type(msg) == "[object String]") {
			msg = json_parse(msg);
		}
		if (msg.result == "fail") {
			alert(msg.error_msg + "请重新上传！");
			$(submit).attr("disabled", false);
			clearInterval(timer);
		}
		// 上传结束，不一定成功，success是针对ajax请求，不是业务
		// $(submit).attr("disabled", false);
	};
	var error = function(msg) {
		// 上传错误
		$(submit).attr("disabled", false);
		clearInterval(timer);
	};
	var beforeSend = function() {
	};
	var complete = function() {
	};
	// 开始上传
	uploadRequestByAjax(url, fd, beforeSend, success, complete, error);
	// 禁止重复提交
	$(submit).attr("disabled", true);

	// 开始检查进度
	var xml_http_request = createTraditionalAjax();
	timer = setInterval(function() {
		// 检查进度
		get_progress(xml_http_request, files_id, submit, timer);
	}, 100);
}

function get_progress(xhr, files_id, submit, timer) {
	var url = "file/getProgress.do";
	var files = document.getElementById(files_id).files;
	var fileNames = "";
	var fileSizes = "";
	for (var i = 0; i < files.length; i++) {
		if (i > 0) {
			fileNames += "&";
		}
		fileNames += "fileNames=" + files[i].name;
		fileSizes += "&fileSizes=" + files[i].size;
	}
	var data = fileNames + fileSizes;
	var success = function(msg) {
		if (get_type(msg) == "[object String]") {
			// alert(typeof msg == "string");
			msg = json_parse(msg);
		}
		var flag = true;
		for ( var key in msg) {
			var value = msg[key];
			update_progress(key, value);
			if (key.startWith("progress") && value != "100") {
				flag = false;
			}
		}
		if (flag) {
			clearInterval(timer);
		}
	};
	var error = function(msg) {
		alert(msg);
		clearInterval(timer);
	};
	requestByTraditionalAjax(xhr, "POST", url, true, data, success, error);
}

function get_emojies(page, emojies_id) {
	var url = "emoji/getEmojiesByPage.do";
	var data = {
		page : page
	};
	var emojies = $("#" + emojies_id);
	emojies.children().remove();
	var success = function(msg) {
		if (get_type(msg) == "[object String]") {
			msg = json_parse(msg);
		}
		for ( var i in msg) {
			var element = "<img src=\"emoji/"
					+ page
					+ "/"
					+ msg[i]
					+ "\" class=\"emoji\" onclick=\"javascript:edit_div.innerHTML+=this.outerHTML;\" />";
			if (i == 0) {
				emojies.append(element);
			} else {
				$(element).appendTo(emojies);
			}
		}
	};
	var error = function(msg) {
	};
	var beforeSend = function() {
	};
	var complete = function() {
	};
	// 开始上传
	normalRequestByAjax(url, data, beforeSend, success, complete, error);
}