

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.ArrayList,com.oasishome.server.Device;"%>
<%
	/*ArrayList<Device> activeDevices =  (ArrayList<Device>)request.getAttribute("ActiveList");
ArrayList<Device> inactiveDevices =  (ArrayList<Device>)request.getAttribute("InActiveList");*/
String username = (String)request.getAttribute("username");
String jqtable = (String)request.getAttribute("jqChartTable");
String jqtops = (String)request.getAttribute("jqChartTops");


%>

<link type="text/css" rel="stylesheet" href="../css/jquery.dataTables.css" />
<script language="JavaScript" type="text/javascript"
	src="../js/jquery.dataTables.min.js"></script>

<link type="text/css" rel="stylesheet" href="../css/jquery.jqChart.css" />
<script language="JavaScript" type="text/javascript"
	src="../js/jquery.jqChart.min.js"></script>
	
	
<link type="text/css" rel="stylesheet" href="../css/jquery.jqRangeSlider.css" />
<script language="JavaScript" type="text/javascript"
	src="../js/jquery.jqRangeSlider.min.js"></script>
	
	
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
				<li id="main_home"><a title="Home"
					href="javascript:getUserHomeDevices()">Manage Devices</a></li>
				<li id="main_selfservice" class="sel"><a title="Self service"
					href="javascript:getUserBillPredictions()">View Billing Predictions</a></li>
			</ul>
		</div>
	</div>
	
	<div id="subtab_home" class="headList" style="display: block;">
	<span id="form_gettingstarted" class="selArw"><a href="javascript:;">Bill Predictions for monitored devices</a></span><span id="form_gettingstarted" style="float: right;margin-right: 188px;" class="selArw"><a href="javascript:;">Billing Trends</a></span></div>

	<div class="devicesPanel">
		<div id="homeDevs" class="home">
			<div id="activeDevices" class="active"
				style="float: left; height: 460px; width: 60%; margin: 0px; padding: 10px;border-color: #BBBBBB;border-style: dashed;border-width: 2px;">
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="table1"></table>
			</div>

			<div id="inactivedevices" class="inactive"
				style="float: right; height: 500px; width: 35%; margin: 0px; padding: 0em;overflow: auto;">
				
				 <div style="padding-left: 10px;font-weight: bold;color: #AAAAAA;margin-top:40px;color: #8989889;">Energy Consumptions</div>
				<div id="jqChart1" style="height: 160px;margin: 20px;">
        </div>
        <div style="padding-left: 10px;padding-top:5px; font-weight: bold;color: #8989889;">Past Usage</div>
				 <div id="jqChart"  style="height: 160px;margin: 20px;"></div>
				
			</div>
		</div>

		
	</div>
</div>

<script>
$('#table1').dataTable( {
        "aaData": 
            /* Reduced data set */
           <%=jqtable%>
        ,
        "aoColumns": [
            { "sTitle": "Device" },
            { "sTitle": "Usage(hrs/day)" },
            { "sTitle": "Spending($)" }           
        ]
    } );   

</script>


<script lang="javascript" type="text/javascript">
        $(document).ready(function () {
        	  var background = {
                      type: 'linearGradient',
                      x0: 0,
                      y0: 0,
                      x1: 0,
                      y1: 1,
                      colorStops: [{ offset: 0, color: '#eeeeee' },
                                   { offset: 1, color: 'white' }]
                  };
            $('#jqChart').jqChart({
                
                animation: { duration: 1 },
                border: { strokeStyle: '#CCCCCC' },
                background: background,
                shadows: {
                    enabled: true
                },
                series: [
                    {
                        type: 'column',
                        title: 'spending',
                        fillStyle: '#418CF0',
                        data: [['Mar', 260], ['Feb', 270], ['Jan', 320],
                               ['Dec', 400], ['Nov', 390]]
                    }
                ]
            });
        });
    </script>
    
    <script lang="javascript" type="text/javascript">
        $(document).ready(function () {

            var background = {
                type: 'linearGradient',
                x0: 0,
                y0: 0,
                x1: 0,
                y1: 1,
                colorStops: [{ offset: 0, color: '#eeeeee' },
                             { offset: 1, color: 'white' }]
            };

            $('#jqChart1').jqChart({
               
                legend: { title: 'devices' },
                border: { strokeStyle: '#CCCCCC' },
                background: background,
                animation: { duration: 1 },
                shadows: {
                    enabled: true
                },
                series: [
                    {
                        type: 'pie',
                        fillStyles: ['#418CF0', '#FCB441', '#E0400A', '#056492'],
                        labels: {
                            stringFormat: '%d%%',
                            valueType: 'percentage',
                            font: '10px sans-serif',
                            fillStyle: 'white'
                        },
                        explodedRadius: 10,
                        explodedSlices: [5],
                        data: <%=jqtops%>
                    }
                ]
            });

            $('#jqChart').bind('tooltipFormat', function (e, data) {
                var percentage = data.series.getPercentage(data.value);
                percentage = data.chart.stringFormat(percentage, '%.2f%%');

                return '<b>' + data.dataItem[0] + '</b><br />' +
                       data.value + ' (' + percentage + ')';
            });
        });
    </script>
    


