package com.oasis.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * represents the Task of the application
 *
 */
public class Task {
	// properties of a task
	private int taskId = -1;
	private String listName = "";
	private String name = "";
	private long alert = 0;
	private long dueDate = 0;
	private int isCompleted = 0;
	private int isEnabled = 1;
	private String notes = "";
	private int priority = 1;
	private int listId = 0;

	// for display alone
	private String dueDateStr = "";
	private String alertStr = "";
	private boolean isCompletedStatus = false;

	// static constants used in code
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_HIGH = 2;
	public static final int PRIORITY_LOW = 1;
	public static final int TASK_COMPLETE=1;
	public static final int TASK_PENDING=0;

	private boolean missed = false;

	public Task() {

	}

	public Task(String name, long date, int priority, int taskId, long alert,
			int isCompleted, int isEnabled, String notes, int listid) {
		this.name = name;
		this.dueDate = date;
		this.priority = priority;
		this.taskId = taskId;
		this.alert = alert;
		this.isCompleted = isCompleted;
		this.isEnabled = isEnabled;
		this.notes = notes;
		this.listId = listid;
	}

	// getters and setters
	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAlert() {
		return alert;
	}

	public void setAlert(long alert) {
		this.alert = alert;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public int getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(int isCompleted) {
		this.isCompleted = isCompleted;
	}

	public int getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public void setMissed(boolean missed) {
		this.missed = missed;
	}

	public boolean isMissed(){
		return this.missed;
	}
	
	public void toggleMissed(){
		if (this.missed){
			this.missed = false;
		} else {
			this.missed = true;
		}
	}
	
	public void toggleCompleted(){
		if (this.isCompleted == 1){
			this.isCompleted = 0;
		} else {
			this.isCompleted = 1;
		}
	}


	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	// convert the timestamp into a display format
	public String getDueDateStr() {
		dueDateStr = "";
		if(dueDate==-1){
			return "";
		}
		Calendar cld = Calendar.getInstance();
		cld.setTimeInMillis(dueDate);
		SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		dueDateStr = dformat.format(cld.getTime());
		return dueDateStr;
	}
	
	public void setDueDateStr(String dueDateStr) {
		this.dueDateStr = dueDateStr;
	}
	
	public boolean isCompletedStatus() {
		isCompletedStatus = isCompleted == 1 ? true : false;
		return isCompletedStatus;
	}
	
	public void setCompletedStatus(boolean isCompletedStatus) {
		this.isCompletedStatus = isCompletedStatus;
	}
	
	public String getAlertDateStr() {
		alertStr = "";
		Date dt = new Date(alert);
		SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		alertStr = dformat.format(dt);
		return alertStr;
	}
}
