function change_font(ele_id, font_color, font_size) {
	alert(font_color + "," + font_size);
	var ele = $("#" + ele_id);
	ele.css("color", font_color);
	ele.css("font-size", font_size + "pt");
}

function change_font_size(ele_id, font_size) {
	var ele = $("#" + ele_id);
	ele.css("font-size", font_size + "pt");
}

function change_font_color(ele_id, font_color) {
	var ele = $("#" + ele_id);
	ele.css("color", font_color);
}

function change_bg_color(ele_id, font_color) {
	var ele = $("#" + ele_id);
	ele.css("background-color", font_color);
}

function hide_by_id(id, speed) {
	$("#" + id).hide(speed);
}

function show_by_id(id, speed) {
	$("#" + id).show(speed);
}

function show_window(id) {
	var winNode = $("#" + id);
	// 方法一：利用js修改css的值，实现显示效果
	// winNode.css("display", "block");
	// 方法二：利用jquery的show方法，实现显示效果
	// winNode.show("slow");
	// 方法三：利用jquery的fadeIn方法实现淡入
	winNode.fadeIn("slow");
}

function hide_window(id) {
	var winNode = $("#" + id);
	// 方法一：修改css的值
	// winNode.css("display", "none");
	// 方法二：jquery的fadeOut方式
	winNode.fadeOut("slow");
	// 方法三：jquery的hide方法
	winNode.hide("slow");
}