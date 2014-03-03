
/* Login Page */

function checkLoginAndRedirect(){
	/*$.post("/validateUser", {
	}, function(data) {
		var jsObj = data;
		if(jsObj!=null && jsObj.status){
			getUserHome();
		}
	},"json");*/
}

function logOut(){
	$.post("/logoutUser", {
	}, function(data) {
			window.location.reload();
	},"json");
}


function validateLogin() {
	var uname = $("#username").val();
	var pwd = $("#password").val();
	$("#username").removeClass("red");
	$("#password").removeClass("red");
	
	if (uname.trim() == "") {
		$("#username").focus();
		$("#username").addClass("red");
		return;			
	}

	if (pwd.trim() == "") {
		$("#password").focus();
		$("#password").addClass("red");
		return;	
	}

	$.post("/validateUser", {
		"name" : encodeURIComponent(uname),
		"pwd" : encodeURIComponent(pwd)
	}, function(data) {
		var jsObj = data;
		if(!jsObj.status){
			$("#password").siblings("p.red").html(jsObj.errorMessage);
			$("#password").siblings("p.red").show();
		}else{
			getUserHomeDevices(uname,pwd);
		}
	},"json");
	
	
}

function getUserHomeDevices(uname,pwd){
	$.post("/getDevices", {
		"uname" : encodeURIComponent(uname),
		"pwd" : encodeURIComponent(pwd)
	}, function(data) {
		$("#mainContent").html(data);
	});
}

function getUserHome(taskidfocus){
	$.post("/getDevices", {
		
	}, function(data) {
		$("#mainContent").html(data);
		if(taskidfocus){
			$('#task_'+taskidfocus).css("backgroundColor","#EFF8D9");
			$('#task_'+taskidfocus).animate({backgroundColor:"#99AB67" }, 'slow',function() {
				$('#task_'+taskidfocus).animate({backgroundColor : '#EFF8D9' },'5000');
			  });
			$('html, body').animate({ scrollTop: $('#task_'+taskidfocus).offset().top }, 'slow',function() {
				$('#task_'+taskidfocus).animate({backgroundColor : '#EFF8D9' },'5000');
			  });
			
		}
	});
}

function showRegister(){
	$("#loginDiv").slideUp('slow');
	$("#regDiv").slideDown('slow');
	$("#r_username").val("");
	$("#r_password").val("");
}

function showRegisterBack(){
	$("#loginDiv").slideDown('slow');
	$("#regDiv").slideUp('slow');
}

function registerUser(){
	var uname = $("#r_username").val();
	var pwd = $("#r_password").val();
	var mic = $("#r_microaddress").val();
	$("#r_username").removeClass("red");
	$("#r_password").removeClass("red");
	$("#r_microaddress").removeClass("red");
	$("#r_password").siblings("p.red").hide();
	if (uname.trim() == "") {
		$("#r_username").focus();
		$("#r_username").addClass("red");
		return;			
	}

	if (mic.trim() == "") {
		$("#r_microaddress").focus();
		$("#r_microaddress").addClass("red");
		return;	
	}
	
	if (pwd.trim() == "") {
		$("#r_password").focus();
		$("#r_password").addClass("red");
		return;	
	}

	$.post("/userOperations", {
		"name" : encodeURIComponent(uname),
		"pwd" : encodeURIComponent(pwd),
		"micAddress" : encodeURIComponent(mic),
		"action":"adduser"
	}, function(data) {
	var jsObj = data;
		if(!jsObj.status){
			$("#r_password").siblings("p.red").html(jsObj.errorMessage);
			$("#r_password").siblings("p.red").show();
		}else{
			getUserHome();
		}
	},"json");
	
}

/* Task Page */
function showAddTaskPopUP(){
	var curDate = new Date();
	$("#date_hrs").val(curDate.getHours());
	$("#date_min").val(curDate.getMinutes());
	$("#date_sec").val(curDate.getSeconds());
	$(".editTask").remove();
	$("#addTaskDiv").slideDown('slow');
}
function hideAddTaskPopUP(elm){
	$("#taskduedate").datepicker("destroy");
	$(".addtask").slideUp('slow');
}


function showHideDueDate(elmClicked){
	var elm = $(elmClicked).parent().parent();
	if($(elm).find("#duedatereq").is(':checked')){
		$(elm).find("#taskduedate").attr("disabled",false);
		$(elm).find("#duedatereq").siblings("input").attr("disabled",false);
	}else{
		$(elm).find("#taskduedate").attr("disabled",true);
		$(elm).find("#duedatereq").siblings("input").attr("disabled",true);
	}
}




/* task operations */ 

function addNewTask(elm){
	
		if($(elm).attr("edit")=="1"){
			editTaskReq(elm);
		}
	var name = $("#addTaskDiv").find("#taskname").val();
	var notes = $("#addTaskDiv").find("#tasknotes").val();
	var datereq = $("#addTaskDiv").find("#duedatereq").is(":Checked");
	var date = $("#addTaskDiv").find("#taskduedate").val();
	var pr = $("#addTaskDiv").find("#taskprir").val();
	$("#task_error").hide();
	
	$("#addTaskDiv").find("#taskname").removeClass("red");
	 $("#addTaskDiv").find("#taskduedate").removeClass("red");
		
	if (name.trim() == "") {
		 $("#addTaskDiv").find("#taskname").focus();
		 $("#addTaskDiv").find("#taskname").addClass("red");
		return;			
	}
	
	if(datereq && date.trim()==""){
		$("#addTaskDiv").find("#taskduedate").focus();
		 $("#addTaskDiv").find("#taskduedate").addClass("red");
		return;	
	}
	
	
	
	var timeComp = $("#date_hrs").val()+":"+$("#date_min").val()+":"+$("#date_sec").val();
	
	$.post("/taskOperations", {
		"name" : encodeURIComponent(name),
		"duedate" : encodeURIComponent(date),
		"noduedate" : encodeURIComponent(datereq),
		"priority" : encodeURIComponent(pr),
		"notes" : encodeURIComponent(notes),
		"timeComp" : encodeURIComponent(timeComp),
		"action":"addTask"
	}, function(data) {
	var jsObj = data;
		if(!jsObj.status){
			$("#task_error").html(jsObj.errorMessage);
			$("#task_error").show();
		}else{
			var taskid = jsObj.taskId;
			setTimeout(function() {
			getUserHome(taskid);		
			}, 2000);
			$("#addTaskDiv").slideUp(1500);
			}
	},"json");
	
}


// edit a task ..
function editTaskReq(elmbut){
	var elm = $(elmbut).parent("span").parent("div");
	var name = $(elm).find("#taskname").val();
	var notes = $(elm).find("#tasknotes").val();
	var datereq = $(elm).find("#duedatereq").is(":Checked");
	var date = $(elm).find("#taskduedate").val();
	var pr = $(elm).find("#taskprir").val();
	$(elm).find("#task_error").hide();
	
	$(elm).find("#taskname").removeClass("red");
	$(elm).find("#taskduedate").removeClass("red");
		
	if (name!=null && name.trim() == "") {
		$(elm).find("#taskname").focus();
		$(elm).find("#taskname").addClass("red");
		return;			
	}
	
	if(datereq && date.trim()==""){
		$(elm).find("#taskduedate").focus();
		$(elm).find("#taskduedate").addClass("red");
		return;	
	}
	
	
	var timeComp = $(elm).find("#date_hrs").val()+":"+$(elm).find("#date_min").val()+":"+$(elm).find("#date_sec").val();
	var id = $(elm).attr("taskid");
	$.post("/taskOperations", {
		"name" : encodeURIComponent(name),
		"id" : encodeURIComponent(id),
		"duedate" : encodeURIComponent(date),
		"noduedate" : encodeURIComponent(datereq),
		"priority" : encodeURIComponent(pr),
		"notes" : encodeURIComponent(notes),
		"timeComp" : encodeURIComponent(timeComp),
		"action":"editTask"
	}, function(data) {
	var jsObj = data;
		if(!jsObj.status){
			$(elm).find("#task_error").html(jsObj.errorMessage);
			$(elm).find("#task_error").show();
		}else{
			var taskid = jsObj.taskId;
			setTimeout(function() {
			getUserHome(taskid);		
			}, 2000);
			$(elm).slideUp(1500);
			setTimeout(function() {
				$(elm).remove();		
				}, 2000);
			}
	},"json");
	
}


function completeTask(elm){
	var taskid = $(elm).parent("div").attr("taskid");
	if(taskid){ 
	$.post("/taskOperations", {
		"taskId" : encodeURIComponent(taskid),
		"action":"completeTask"
	}, function(data) {
		setTimeout(function() {
			getUserHome(taskid);
				}, 2000);
		$(elm).parent(".row").hide(1500);
	},"json");
	}
}


function deleteTask(elm){
	if(confirm("Do you want to delete this task")){
	var taskid = $(elm).parent("span").parent("div").attr("taskid");
	if(taskid){ 
		$(elm).parent(".edit").parent(".row").hide('slow');
	$.post("/taskOperations", {
		"taskId" : encodeURIComponent(taskid),
		"action":"deleteTask"
	}, function(data) {	
	},"json");
	}
	}
}

function editTask(elm){
	$(".editTask").remove();
	$(".addtask").hide();
	var editTaskDiv = $("#addTaskDiv").clone();
	var parent = $(elm).parent("span").parent("div");
	$(editTaskDiv).addClass("editTask");
	$(parent).append($(editTaskDiv));
	$(editTaskDiv).find(".rowheader").html("Edit Task");
	$(editTaskDiv).find("#addedittask").html("Edit");
	$(editTaskDiv).find("#addedittask").attr("edit","1");
	
	// set all the task values	
	var taskObj = jQuery.parseJSON($(parent).attr("taskObj"));
	$(editTaskDiv).find("#taskname").val(taskObj.name);
	$(editTaskDiv).find("#tasknotes").val(taskObj.notes);
//	$(editTaskDiv).find("#taskduedate").val(taskObj.date);
	$(editTaskDiv).find("#date_sec").val(taskObj.sec);
	$(editTaskDiv).find("#date_min").val(taskObj.min);
	$(editTaskDiv).find("#date_hrs").val(taskObj.hr);
	$(editTaskDiv).find("#taskprir").val(taskObj.prior);
	$(editTaskDiv).find("#duedatereq").attr("checked",false);
	if(taskObj.nodue=="checked"){
	$(editTaskDiv).find("#duedatereq").attr("checked",taskObj.nodue);
	}
	
	$(editTaskDiv).attr("taskid",taskObj.taskid);
	$("#taskduedate").datepicker("destroy");
	$(editTaskDiv).find( "#taskduedate" ).datepicker({dateFormat: "dd-M-yy"} );
	showHideDueDate($(editTaskDiv).find("#duedatereq"));
	$(editTaskDiv).slideDown('slow');

}


function showHideQ(elm){
	$(elm).toggleClass("green");
	var ascdiv = $(elm).attr("asscDiv");

	if($(elm).hasClass("green")){
		$("#"+ascdiv).slideDown('fast');
	}else{
		$("#"+ascdiv).slideUp('fast');
		
	}
}
