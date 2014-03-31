<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>

<html>
<head>
<link href="../css/evoslider.css" type="text/css" rel="Stylesheet"/>
<link href="../css/default/default.css" type="text/css" rel="Stylesheet"/>

<link type="text/css" rel="stylesheet" href="../OasisStyles.css" />
<link type="text/css" rel="stylesheet" href="../css/bootstrap-switch.min.css" />
<script language="JavaScript" type="text/javascript"
	src="../jquery_min.js"></script>	

	<script src="../jquery_migrate.js"></script>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">	
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />        
<script src="../js/jquery.easing.1.3.js" type="text/javascript"></script>
<script src="../js/jquery.evoslider.lite-1.1.0.min.js" type="text/javascript"></script>
<script type="text/javascript" src="../OasisScripts.js"></script>	
<script language="JavaScript" type="text/javascript"
	src="../jquery_ui.js"></script>
<script language="JavaScript" type="text/javascript"
	src="../js/bootstrap-switch.min.js"></script>
	
	

</head>

<body>
	<div id="OasisPage">
	<div id="mainContent">
		<div class="heading"><img src="../images/oasiscanvas.png" style="height: 30px;"/> Oasis Smart Home </div>
		<div class="arrowLog"><img src="../images/login.png" style="height: 80px;"/></div>
		<div class="loginPanel" id="loginDiv">
		
					<p style="margin: 10px;font-family: cursive;"></p>
			<div>
				<span><input id="username" type="text"
					placeholder="user name"></span> <span><input id="password"
					type="password" placeholder="password"><p class="red"></p></span>
			</div>
			<div class="clsBtns" style="left: 50px">
				<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="validateLogin()">Login</span> <span
					style="display: inline-block;" class="btns" onclick="showRegister()">Register</span>
			</div>
		</div>
		<div class="loginPanel" id="regDiv" style="display: none;">
			<p style="margin: 10px;font-family: cursive;"></p>
			<div>
				<span><input id="r_username" type="text"
					placeholder="user name"></span> <span><input id="r_password"
					type="password" placeholder="password"><p class="red"></p> </span>
					<span><input id="r_microaddress" value="Aware_Home"
					type="text" disabled="disabled" placeholder="Microcontroller Address" style="font-size: 11"><p class="red"></p> </span>
			</div>
			<div class="clsBtns" style="left: 50px;;margin-top: 20px">
				<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="registerUser()">Go!</span>
					<span style="display: inline-block; margin-right: 7px;" class="btns"
					onclick="showRegisterBack()">Back</span>
			</div>
		</div>
		</div>
		<div id="about" style="top:13%;position: absolute;left: 16%;">
		<div class="evoslider default" id="aboutSlider"> <!-- start evo slider -->
                <dl>
                <dt>Manage Appliances</dt>
                <dd>
                <div class="evoText partialleft">
	                        <h3>Manage Appliances</h3>
	                        <p>Oasis Smart Home allows you to</p>
	                        
	                        <ul class="custom-list check">
					            <li>Andriod / Web control access to appliances</li>
					            <li>Efficient Home energy management</li>
					            <li>Increase user awareness on energy patterns</li>
					            <li>Efficient home energy management</li>					           	
				            </ul>
	                    </div>
	                    <div class="evoImage" style="width: 350px; padding: 30px;padding-left: 70px;"><img src="../images/green-home-icon.png" style="float: none;height: 200px;"></div>
                </dd>
                <dt>Geo Fencing</dt>
                <dd><div class="evoText partialleft">
	                        <h3>Mobile controlling of appliances</h3>
	                        <p>Oasis Smart Home allows you to</p>
	                        
	                        <ul class="custom-list check">
					            <li>Andriod access to home</li>
					            <li>Web access to home</li>
					            <li>Andriod access to home</li>
					            <li>Andriod access to home</li>					           	
				            </ul>
	                    </div>
	                    <div class="evoImage" style="width: 350px; padding: 30px;padding-left: 70px;"><img src="../images/green-home-icon.png" style="float: none;height: 200px;"></div>
                </dd></dd>
                <dt>Bill Predictions</dt>
                <dd><div class="evoText partialleft">
	                        <h3>Mobile controlling of appliances</h3>
	                        <p>Oasis Smart Home allows you to</p>
	                        
	                        <ul class="custom-list check">
					            <li>Andriod access to home</li>
					            <li>Web access to home</li>
					            <li>Andriod access to home</li>
					            <li>Andriod access to home</li>					           	
				            </ul>
	                    </div>
	                    <div class="evoImage" style="width: 350px; padding: 30px;padding-left: 70px;"><img src="../images/green-home-icon.png" style="float: none;height: 200px;"></div>
                </dd></dd>
                </dl>

        </div>
		</div>
		</div>
</body>
</html>

<script language="JavaScript" type="text/javascript">
$(document).ready(function(){
	checkLoginAndRedirect();

	$("#aboutSlider").evoSlider({
		mode: "accordion", // Sets slider mode ("accordion", "slider", or "scroller")
		width: 960, // The width of slider
		height: 280, // The height of slider
		slideSpace: 5, // The space between slides
		mouse: true, // Enables mousewheel scroll navigation
		keyboard: true, // Enables keyboard navigation (left and right arrows)
		speed: 500, // Slide transition speed in ms. (1s = 1000ms)
		easing: "swing", // Defines the easing effect mode
		loop: true, // Rotate slideshow
		autoplay: true, // Sets EvoSlider to play slideshow when initialized
		interval: 5000, // Slideshow interval time in ms
		pauseOnHover: true, // Pause slideshow if mouse over the slide
		pauseOnClick: true, // Stop slideshow if playing
		directionNav: true, // Shows directional navigation when initialized
		directionNavAutoHide: false, // Shows directional navigation on hover and hide it when mouseout
		controlNav: true, // Enables control navigation
		controlNavAutoHide: false // Shows control navigation on mouseover and hide it when mouseout
		}); 

});



</script>