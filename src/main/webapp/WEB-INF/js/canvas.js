function rainbow(cvs_id, number, speed) {
	var cvs = document.getElementById(cvs_id);
	var ctx = cvs.getContext("2d");
	var colors = [ "white", "violet", "indigo", "blue", "green", "yellow",
			"orange", "red" ];
	// ctx.beginPath();
	// ctx.scale(0.5, 0.5);
	var x = y = r1 = 100 * number;
	var r0 = 78 * number;
	var sAngle = 1.20;
	var eAngle = 1.80;
	var gap = (r1 - r0) * 1.0 / 7;
	var grd = ctx.createRadialGradient(x, y, r0, x, y, r1);
	// 所有参数改为传入
	for (var i = 0; i < 8; i++) {
		excute_canvas_after_time(x, y, r0, i, colors, gap, sAngle, eAngle, grd,
				ctx, speed * 100);
	}
	// ctx.stroke();
}

function test_one(cvs_id) {
	var cvs = document.getElementById(cvs_id);
	var ctx = cvs.getContext("2d");
	var x = y = 200;
	var r = 50;
	var i = 0;
	setInterval(function() {
		ctx.clearRect(0, 0, cvs.width * 2, cvs.height * 2);
		ctx.arc(x - r * Math.sin(i), y - r * Math.cos(i), r, i * Math.PI / 180
				+ Math.PI * 0.5, i * Math.PI / 180 + Math.PI * 1.5, false);
		ctx.arc(x - r * Math.sin(i), y + r * Math.cos(i), r, i * Math.PI / 180
				+ Math.PI, i * Math.PI / 180 + Math.PI * 2, false);
		ctx.arc(x + r * Math.sin(i), y - r * Math.cos(i), r, i * Math.PI / 180
				+ Math.PI * 1.5, i * Math.PI / 180 + Math.PI * 2.5, false);
		ctx.arc(x + r * Math.sin(i), y + r * Math.cos(i), r, i * Math.PI / 180
				+ Math.PI * 2, i * Math.PI / 180 + Math.PI * 3, false);
		ctx.fill();
		i = (i + 10) % 360;
	}, 1000);
}

function get_window_size() {
	// 获取窗口宽度
	if (window.innerWidth) {
		cvs.width = window.innerWidth;
	} else if ((document.body) && (document.body.clientWidth)) {
		cvs.width = document.body.clientWidth;
	}
	// 获取窗口高度
	if (window.innerHeight) {
		cvs.height = document.body.clientWidth;
	} else if ((document.body) && (document.body.clientHeight)) {
		cvs.height = document.body.clientHeight;
	}

	// 通过深入 Document 内部对 body 进行检测，获取窗口大小
	if (document.documentElement && document.documentElement.clientHeight
			&& document.documentElement.clientWidth) {
		cvs.width = document.documentElement.clientHeight;
		cvs.height = document.documentElement.clientWidth;
	}
}

function excute_canvas_after_time(x, y, r0, i, colors, gap, sAngle, eAngle,
		grd, ctx, speed) {
	setTimeout(function() {
		grd.addColorStop(1.0 / 7 * i, colors[i]);
		ctx.arc(x, y, r0 + gap * i, Math.PI * (sAngle - 0.01 * i), Math.PI
				* (eAngle + 0.01 * i), false);
		ctx.fillStyle = grd;
		ctx.fill();
	}, speed * i);
}

var supported = "IE 9/Firefox 3.5/Opera 10.5/Chrome/3.0/Safari 3.0";

function playPause() {
	var myVideo = document.getElementById("video");
	if (myVideo.paused)
		myVideo.play();
	else
		myVideo.pause();
}

function makeBig() {
	var myVideo = document.getElementById("video");
	myVideo.width = 560;
}

function makeSmall() {
	var myVideo = document.getElementById("video");
	myVideo.width = 320;
}

function makeNormal() {
	var myVideo = document.getElementById("video");
	myVideo.width = 420;
}

function allow_drop(event) {
	event.preventDefault();
}

function drag(event) {
	event.dataTransfer.setData("attr", event.target.id);
}

function drop(event) {
	event.preventDefault();
	var data = event.dataTransfer.getData("attr");
	event.target.appendChild(document.getElementById(data));
}

function getLocation(ele_id) {
	var ele = document.getElementById(ele_id);

	var successCallback = function showPosition(position) {
		ele.innerHTML = "Latitude(维度): " + position.coords.latitude
				+ "<br />Longitude(经度): " + position.coords.longitude;
	};
	var errorCallback = function(error) {
		switch (error.code) {
		case error.PERMISSION_DENIED:
			x.innerHTML = "User denied the request for Geolocation."
			break;
		case error.POSITION_UNAVAILABLE:
			x.innerHTML = "Location information is unavailable."
			break;
		case error.TIMEOUT:
			x.innerHTML = "The request to get user location timed out."
			break;
		case error.UNKNOWN_ERROR:
			x.innerHTML = "An unknown error occurred."
			break;
		}
	};
	if (navigator.geolocation) {
		navigator.geolocation
				.getCurrentPosition(successCallback, errorCallback);
	} else {
		ele.innerHTML = "Geolocation is not supported by this browser.";
	}
}