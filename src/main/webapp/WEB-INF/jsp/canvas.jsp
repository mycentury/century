<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" language="JavaScript" src="js/canvas.js">
</script> 
<title>CANVAS TEST</title>
</head>
<body onload="test_one('test_one');">
	<canvas id="test_one" style="z-index: -1;" width="800px" height="600px"></canvas>
	<!-- <video id="video" width="320" height="240" controls="controls">
		<source src="/i/movie.ogg" type="video/ogg">
		<source src="/i/movie.mp4" type="video/mp4">
		Your browser does not support the video tag.
	</video> -->
	
	<div>
		<button onclick="playPause('video');">播放/暂停</button> 
		<button onclick="makeBig('video');">大</button>
		<button onclick="makeNormal('video');">中</button>
		<button onclick="makeSmall('video');">小</button>
	</div>
	<!-- <audio src="/i/music.mp3" controls="controls" autoplay="autoplay"></audio> -->
	
	<div id="div1" style="border: solid thin;width: 600px;height: 100px;" ondrop="drop(event);" ondragover="allow_drop(event);">
	</div>
	<img id="drag1" src="" draggable="true" ondragstart="drag(event);" width="336" height="69" />
	<svg xmlns="http://www.w3.org/2000/svg" version="1.1" height="190">
		<polygon points="100,10 40,180 190,60 10,60 160,180" style="fill:lime;stroke:purple;stroke-width:1;fill-rule:evenodd;" />
	</svg>
	<button onclick="getLocation('show_location');">试一下</button>
	<p id="show_location"></p>
</body>
</html>