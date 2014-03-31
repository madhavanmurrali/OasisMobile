

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.ArrayList,com.oasishome.server.Device;"%>
<%
	ArrayList<Device> activeDevices =  (ArrayList<Device>)request.getAttribute("ActiveList");
ArrayList<Device> inactiveDevices =  (ArrayList<Device>)request.getAttribute("InActiveList");
String username = (String)request.getAttribute("username");
%>
<div class="container">
	<div class="headTop header">
		<div class="logo" id="topcompanylogo">
			<img height="28" src="/images/oasiscanvas.png"> <span
				style="font-size: 24px; padding-left: 10px; font-family: cursive;">Oasis
				Smart Home</span>
		</div>
		<div class="tr" id="header-right">
			<a href="javascript:;"> hey <%=username%> ! </a> <a href="javascript:logOut()">Logout</a>
		</div>
	</div>


	<div class="navbar">
		<div style="">
			<ul id="main_tab">
				<li id="main_home" class="sel"><a title="Home"
					href="javascript:getUserHomeDevices()">Manage Devices</a></li>
				<li id="main_selfservice"><a title="Self service"
					href="javascript:getUserBillPredictions()">View Billing Predictions</a></li>
			</ul>
		</div>
	</div>
	
	<div id="subtab_home" class="headList" style="display: block;">
	<span id="form_gettingstarted" class="selArw"><a href="javascript:;">Devices being monitored</a></span><span><a>(drag and drop to control devices being monitored)</a></a></span><span id="form_gettingstarted" style="float: right;margin-right: 188px;" class="selArw"><a href="javascript:;">Inactive Devices</a></span></div>

	<div class="devicesPanel">
		<div id="homeDevs" class="home">
			<div id="activeDevices" class="active"
				style="float: left; height: 500px; width: 79%; margin: 0px; padding: 0em;">
				<%
					for(int i=0;i<activeDevices.size();i++){
						Device device = (Device)activeDevices.get(i);
						String devName = device.getName();
						Long id=device.getId();
						String left = device.getXpos();
						String top = device.getYpos();
						String posStr="";
						if(left!=null && top!=null && !left.equals("") && !top.equals("")){
								posStr=" left:"+left+";top:"+top;							
						}
						
						String img = device.getImageSrc()!=null ? device.getImageSrc():"images_generic";
						img= "../images/"+img+".jpg"; 
						
						String checked = device.isStatus()?"checked":"";
				%>
				<div id="draggable_<%=id%>" img="<%=img%>" class="draggableDev devices"
					style="position: absolute;<%=posStr%>">
					<div id="extras"><span id="c" style="float: right;" onclick="javascript:inactive(this)"><img style="cursor:pointer; height: 12px" src="../images/images_c.jpg"/></span>
					<span id="dp"><img src="<%=img%>" style="height: 50px;"/></span> 
					<span id="onOff"><input type="checkbox" data-size="mini" name="my-checkbox" class="onoff" <%=checked%>></span>
					</div>
					<span id="name"><%=devName%></span>
				</div>
				<%
					}
				%>
			</div>

			<div id="inactivedevices" class="inactive"
				style="float: right; height: 500px; width: 20%; margin: 0px; padding: 0em;overflow: auto;">
				<%
					for(int i=0;i<inactiveDevices.size();i++){
						Device device = (Device)inactiveDevices.get(i);
						String devName = device.getName();
						Long id=device.getId();
						String img = device.getImageSrc()!=null ? device.getImageSrc():"images_generic";
						img= "../images/"+img+".jpg"; 
				%>
				<div id="draggable_<%=id%>" img="<%=img%>" class="draggableDev devices devices1"
					style="position: relative;">
					<div id="extras"><span id="c" style="display:none; float: right;" onclick="javascript:inactive(this)"><img style="cursor:pointer; height: 12px" src="../images/images_c.jpg"/></span>
					<span id="dp" style="display:none;"><img src="<%=img%>" style="height: 50px;"/></span> 
					<span id="onOff" style="display:none;"><input type="checkbox" data-size="mini" name="my-checkbox" class="onoff" checked></span>
					</div>
					<span id="name"><%=devName%></span>
				</div>
				<%
					}
				%>
			</div>
		</div>

		<div id="dummy" class="draggableDev devices"
			style="display: none; position: absolute;">
			<div id="extras"><span id="c" style="float: right;" onclick="javascript:inactive(this)"><img style="cursor:pointer; height: 12px" src="../images/images_c.jpg"/></span>
					<span id="dp"><img src="../images/images_fan.jpg" style="height: 50px;"/></span> 
					<span id="onOff"><input type="checkbox" data-size="mini" name="my-checkbox" checked></span>
					</div>
					<span id="name"></span>
		</div>
	</div>
</div>
</div>
</div>
    <div class="messageBox" style="display: none;"></div>  
<script>
	function inactive(elm) {
		var removeElm = $(elm).parent().parent().attr("id");

		var cl = $("#dummy").clone();
		$(cl).attr("id", removeElm);
		$(cl).attr("img", $(elm).parent().parent().attr("img"));
		$(cl).find("span#dp").find("src").attr("img",$(elm).parent().parent().attr("img"));
		$(cl).find("span#name").html($(elm).parent().parent().find("span#name").html());
		$(cl).find("span#dp").hide();
		$(cl).find("span#onoff").hide();
		$(cl).find("span#c").hide();
		
		// update Db
		toggleDeviceMonitorState(removeElm,false,1,1);
		
		$(cl).appendTo("#inactivedevices").css({
			position : "relative"
		}).addClass("devices1").attr("id", removeElm).show();
		$(".devices1").draggable({
			containment : "#homeDevs",
			appendTo: 'body',
			helper: 'clone'
		});
		$('#activeDevices').find('#' + removeElm).remove();
	}

	$(document).ready(
			function() {

				$(".devices").draggable({
					containment : "parent",
					stop:function(event,ui){
						console.log($(this).attr("id"));
						updatePosition($(this).attr("id"),ui.offset);
					}
				});
				
				$(".devices1").draggable({
					containment : "#homeDevs",
					appendTo: 'body',
					helper: 'clone'
				});

				$("#activeDevices").droppable(
						{

							drop : function(event, ui) {
								var removeElm = $(ui.draggable).attr("id");
								if ($(ui.draggable).parent().hasClass(
										"inactive")) {

									var newleft = ui.offset.left
											+ $("#activeDevices").width();
									if (newleft + $("#dummy").width() > $(
											"#activeDevices").width())
										newleft -= 50;
									var cl = $("#dummy").clone();																		
									$(cl).attr("id", removeElm);
									$(cl).attr("img", $(ui.draggable).attr("img"));
									$(cl).find("span#dp").find("img").attr("src",$(ui.draggable).attr("img"));
									$(cl).appendTo("#activeDevices");
									$(cl).find("span#name").html($(ui.draggable).find("span#name").html());
									$(cl).find("span#dp").show();
									$(cl).find("span#onoff").find("input").addClass("onoff").show();
									$(cl).find("span#c").show();
									$(cl).css({
										"top" : ui.offset.top,
										"left" : ui.offset.left,
										position : "absolute"
									}).show();
									console.log(removeElm);
									$(".devices").draggable({
										containment : "parent"
									});
									$(".devices1").draggable({
										containment : "#homeDevs",
										appendTo: 'body',
										helper: 'clone'
									});
									$('#inactivedevices').find('#' + removeElm)
											.remove();
									initSwitches();
									
									// Update DB
									toggleDeviceMonitorState(removeElm,true,ui.offset.left,ui.offset.top);
								}
								
							},
						});
				initSwitches();
			});
	
	
</script>
