package com.oasis.android;

import java.util.logging.Logger;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * serves as a session management tool
 * 
 */
public class SessionManager {

	// members
	private Integer userId;
	private Boolean userLoggedIn;
	private String currentListName;
	private int currentListID;
	private int currentTaskID;
	public static final String PREF_FILE_NAME = "GTodoPrefFile";
	private static Logger logger = Logger.getLogger("GTodo");

	// getters and setters
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Boolean getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(Boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}
	

	public String getCurrentListName() {
		return currentListName;
	}

	public void setCurrentListName(String currentListName) {
		this.currentListName = currentListName;
	}
	
	

	public int getCurrentListID() {
		return currentListID;
	}

	public void setCurrentListID(int currentListID) {
		this.currentListID = currentListID;
	}

	public int getCurrentTaskID() {
		return currentTaskID;
	}

	public void setCurrentTaskID(Context context, int currentTaskID) {
		this.currentTaskID = currentTaskID;
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.putInt("CurrentTaskID", currentTaskID);
		editSh.commit();
	}

	public SessionManager() {
		userId = -1;
		userLoggedIn = false;
	}

	public void setLoginDetails(GTodoUser userObj, Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.putInt("LoggedInUserId", userObj.getUserId());
		editSh.commit();
		logger.info(" Credentials" + userObj.getUserId());
	}

	public void getUserLoginDetails(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		userId = preferences.getInt("LoggedInUserId", -1);
		if (userId != null && userId != -1) {
			userLoggedIn = true;
		}
		logger.info(" Credentials got" + userId);
	}

	// clear the session - logout
	public void logoutUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.remove("LoggedInUserId");
		editSh.commit();
		logger.info(" Log Out Complete");
	}

	public void setCurrentListDetails(List list, Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.putInt("CurrentListID", list.getId());
		editSh.putString("CurrentListName", list.getName());
		editSh.commit();
	}
	
	public int getCurrentListDetails(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		currentListName = preferences.getString("CurrentListName",
				"Default List");
		currentListID = preferences.getInt("CurrentListID", -1);
		return currentListID;
	}

	public void backToList(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.remove("CurrentListName");
		editSh.remove("CurrentListID");
		editSh.commit();
	}

	public void setCurrentTaskDetails(Task task, Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.putInt("CurrentTaskID", task.getTaskId());
		editSh.commit();
	}

	public int getCurrentTaskDetails(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		currentTaskID = preferences.getInt("CurrentTaskID", -1);
		return currentTaskID;
	}

	public void backToTasks(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editSh = preferences.edit();
		editSh.remove("CurrentTaskID");
		editSh.commit();
	}

}
