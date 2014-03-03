package com.oasis.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import android.R.bool;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * This file has all the database related create insert update delete logics 
 *
 */

/**
 * @author lokesh
 *
 */
public class DBOperationsHandler {

	private static Logger logger = Logger.getLogger("GTodo");

	// Register for an account ..

	/**
	 * addNewUser
	 * Input : user details contained in the GtodoUser object.
	 * Output : Status of the database transaction and new user details.
	 * Description : Adds a new user , creates default list and returns the new user id.
	 */
	public HashMap addNewUser(Context context, GTodoUser user) {
		HashMap status = new HashMap();
		String uname = user.getUserName();
		String pwd = user.getPassword();
		String secQ = user.getSecurityQuestion();
		String secA = user.getSecurityAnswer();
		
		// User details validations 
		if (uname == null || ("").equals(uname.trim()) || pwd == null
				|| ("").equals(pwd) || secQ == null || ("").equals(secQ)
				|| secA == null || ("").equals(secA)) {
			status.put("error", true);
			status.put("errorMessage", "Values missing.");
			return status;
		}
		if (checkIfUserNAmeExists(context, uname)) {
			status.put("error", true);
			status.put("errorMessage", "Username already taken.");
			return status;
		}
		
		// Add the user to db
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBUtil.USER_USERNAME, uname.trim());
		values.put(DBUtil.USER_PWD, pwd);
		values.put(DBUtil.USER_SECQTN, secQ);
		values.put(DBUtil.USER_SECANS, secA);

		// Inserting Row
		Long userId = db.insert(DBUtil.TABLE_USER, null, values);
		logger.info("new User id" + userId);
		user.setUserId(userId.intValue());

		// Insert the default lists as well.
		ContentValues deflistvalues = new ContentValues();
		deflistvalues.put(DBUtil.LIST_NAME, DBUtil.defaultListName);
		deflistvalues.put(DBUtil.LIST_USERID, user.getUserId());
		db.insert(DBUtil.TABLE_LIST, null, deflistvalues);

		// Closing database connection
		db.close();
		logger.info("added a new User");
		status.put("error", false);
		return status;

	}
	
	
	// Editing for EditUser page
		/**
		 * editAccountForUser
		 * Input : user object with the details that need to be updated.
		 * Output :  nil
		 * Description : updates the user details in db
		 */
		public void editAccountForUser (Context context, GTodoUser user) 
		{
			// set all the values that are to be updated
			ContentValues values = new ContentValues();
			values.put(DBUtil.USER_USERNAME, user.getUserName());
			values.put(DBUtil.USER_PWD, user.getPassword());
			values.put(DBUtil.USER_SECQTN, user.getSecurityQuestion());
			values.put(DBUtil.USER_SECANS, user.getSecurityAnswer());
			DBUtil dbUtil = new DBUtil(context);
			
			// do the udpate
			SQLiteDatabase db = dbUtil.getWritableDatabase();
			db.update(DBUtil.TABLE_USER, values, DBUtil.USER_USERID + "=?",
					new String[] { String.valueOf(user.getUserId()) });
			
			//close the connection
			db.close();
		}

	// Editing for forgot password page
	/**
	 * editPasswordForUser
	 * Input : User and the new password
	 * Output : nil
	 * Description : updates the password for the specified user.
	 */
	public void editPasswordForUser(Context context, GTodoUser user,
			String newPwd) {
		ContentValues values = new ContentValues();
		values.put(DBUtil.USER_PWD, newPwd);
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		db.update(DBUtil.TABLE_USER, values, DBUtil.USER_USERID + "=?",
				new String[] { String.valueOf(user.getUserId()) });
		db.close();
	}

	// UserDetails got by id
	/**
	 * getUserDetails
	 * Input : the unique id for the user.
	 * Output : all the user details as a GtodoUser object.
	 * Description : fetch all the user details and return id.
	 */
	public GTodoUser getUserDetails(Integer userID, Context context) {
		// set the db criteria based on userid
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		GTodoUser user = new GTodoUser();
		Cursor cursor = db
				.query(DBUtil.TABLE_USER,
						new String[] { DBUtil.USER_USERNAME, DBUtil.USER_PWD,
								DBUtil.USER_SECQTN, DBUtil.USER_SECANS },
						DBUtil.LIST_USERID + "=?",
						new String[] { String.valueOf(userID) }, null, null,
						null, null);
		cursor.moveToFirst();
		
		// check for valid user and set details into the GtodoUser object
		if (!cursor.isAfterLast()) {
			user.setUserName(cursor.getString(0));
			user.setPassword(cursor.getString(1));
			user.setSecurityQuestion(cursor.getString(2));
			user.setSecurityAnswer(cursor.getString(3));
		}
		
		// close the connection
		cursor.close();
		db.close();
		return user;
	}

	// for deleting an account
	/**
	 * deleteUser
	 * Input : user id for the user who is to be deleted
	 * Output : nil
	 * Description : deletes the mentioned user
	 */
	public void deleteUser(Context context, int userID) {
		ArrayList<List> lists2 = new ArrayList<List>();
		lists2 = getUserLists(userID, context);
		
		// form the criteria based on the user id
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		String[] str = { String.valueOf(userID) };
		db.delete(DBUtil.TABLE_USER, DBUtil.USER_USERID + "=?", str);
		// delete the user and close the connection
		for (int i = 0; i < lists2.size(); i++) {
			deleteList(context, lists2.get(i).getId());
		}
		db.close();
	}

	// Authentication Query
	/**
	 * verifyUserCredentials
	 * Input : user object with the entered details.
	 * Output : returns whether the credentials are valid.
	 * Description : check whether the user credentials are valid.
	 */
	public static Boolean verifyUserCredentials(Context context, GTodoUser user) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		// form the criteria based on username and password
		Cursor cursor = db.query(
				DBUtil.TABLE_USER,
				new String[] { DBUtil.USER_USERNAME, DBUtil.USER_PWD,
						DBUtil.USER_USERID },
				DBUtil.USER_USERNAME + "=?" + "and " + DBUtil.USER_PWD + "=?",
				new String[] { String.valueOf(user.getUserName()),
						String.valueOf(user.getPassword()) }, null, null, null,
				null);
		
		// validate and return status
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			user.setUserId(cursor.getInt(2));
			cursor.close();
			db.close();
			return true;
		}
		//close the connection
		cursor.close();
		db.close();
		return false;
	}

	// Lists of the current user.
	/**
	 * getUserLists
	 * Input : userId
	 * Output : Array of Lists which belong to the mentioned user.
	 * Description : fetches all the categories - Lists of the user and returns the same.
	 */
	public ArrayList<List> getUserLists(Integer userID, Context context) {
		//DBUtil dbUtil = new DBUtil(context);
		//SQLiteDatabase db = dbUtil.getWritableDatabase();
		
		ArrayList<List> lists = new ArrayList<List>();
		List listItem1 = new List("Light Bath");
		lists.add(listItem1);
		
		List listItem2 = new List("Light Kitchen");
		lists.add(listItem2);
		
		List listItem3 = new List("Light Balcony");
		lists.add(listItem3);
		
		List listItem4 = new List("Light Room1");
		lists.add(listItem4);
		
		List listItem5 = new List("Light Bedroom2");
		lists.add(listItem5);
		
		
		// form the query based on the userid
		/*Cursor cursor = db
				.query(DBUtil.TABLE_LIST, new String[] { DBUtil.LIST_NAME,
						DBUtil.LIST_ID, DBUtil.LIST_SHOWCOMPLETED,
						DBUtil.LIST_USERID }, DBUtil.LIST_USERID + "=?",
						new String[] { String.valueOf(userID) }, null, null,
						null, null);
		cursor.moveToFirst();
		
		// for the array of lists
		while (!cursor.isAfterLast()) {
			String listName = cursor.getString(0);
			int listID = cursor.getInt(1);
			int showCompleted = cursor.getInt(2);
			int num = this.getTasksAmountByID(listID, context);

			List listItem = new List(userID, listID, listName, num,
					showCompleted);
			lists.add(listItem);
			cursor.moveToNext();
		}
		
		// close the db connection and return the lists
		cursor.close();
		db.close();*/
		return lists;
	}

	// Number of tasks included in the list
	/**
	 * getTasksAmountByID
	 * Input : list id
	 * Output : number of tasks in the list
	 * Description : returns the number of tasks present in this list.
	 */
	public int getTasksAmountByID(int listID, Context context) {
		DBUtil dbUtil = new DBUtil(context);
		// form the query based on the listid
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		Cursor cursor = db
				.query(DBUtil.TABLE_TASK, new String[] { DBUtil.TASK_NAME, },
						DBUtil.TASK_LISTID + "=?",
						new String[] { String.valueOf(listID) }, null, null,
						null, null);

		// get the number of tasks
		int number = cursor.getCount();
		// close the db connection
		cursor.close();
		db.close();
		// return the number of tasks in the list
		return number;
	}

	// Username got by id
	/**
	 * getUserNameById
	 * Input : userId
	 * Output : username of the user - by the UserID.
	 * Description : get the username of the user given the userID.
	 */
	public String getUserNameById(Integer userID, Context context) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		// form the query based on the userid
		String userName = "";
		Cursor cursor = db.query(DBUtil.TABLE_USER,
				new String[] { DBUtil.USER_USERNAME, }, DBUtil.LIST_USERID
						+ "=?", new String[] { String.valueOf(userID) }, null,
				null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			userName = cursor.getString(0);
		}
		// close the connection and return the username
		cursor.close();
		db.close();
		return userName;
	}

	// Lists users .. ..
	/**
	 * getUsers
	 * Input : nil
	 * Output : array - all the users using the GTodo application.
	 * Description : returns the complete list of users using the app.
	 */
	public ArrayList<GTodoUser> getUsers(Context context) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getReadableDatabase();
		// form the query on the user table
		ArrayList<GTodoUser> users = new ArrayList<GTodoUser>();
		Cursor cursor = db.query(DBUtil.TABLE_USER, new String[] {
				DBUtil.USER_USERNAME, DBUtil.USER_USERID, DBUtil.USER_SECQTN,
				DBUtil.USER_SECANS }, null, null, null, null, null, null);
		cursor.moveToFirst();
		// construct the array of user objects 
		while (!cursor.isAfterLast()) {
			GTodoUser user = new GTodoUser();
			user.setUserName(cursor.getString(0));
			user.setUserId(cursor.getInt(1));
			user.setSecurityQuestion(cursor.getString(2));
			user.setSecurityAnswer(cursor.getString(3));
			users.add(user);
			cursor.moveToNext();
		}
		//close the connection
		cursor.close();
		db.close();
		return users;
	}

	// Lists users.
	/**
	 * checkIfUserNAmeExists
	 * Input : username
	 * Output : true/false 
	 * Description : check whether the specified username is already taken.
	 */
	public Boolean checkIfUserNAmeExists(Context context, String userName) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getReadableDatabase();
		// form the criteria for the qury
		Cursor cursor = db.query(DBUtil.TABLE_USER, new String[] {
				DBUtil.USER_USERNAME, DBUtil.USER_USERID },
				DBUtil.USER_USERNAME + "=?",
				new String[] { String.valueOf(userName) }, null, null, null,
				null);
		//validate and return status
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			db.close();
			return true;
		}
		//close the connections
		cursor.close();
		db.close();
		return false;
	}

	// Delete one list and all tasks belonged to this list
	
	/**
	 * deleteList
	 * Input : listid
	 * Output : nil
	 * Description : delete a list given its ListID
	 */
	public void deleteList(Context context, int listID) {
		DBUtil dbUtil = new DBUtil(context);
		// form the delete query and delete
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		String[] str = { String.valueOf(listID) };
		db.delete(DBUtil.TABLE_LIST, DBUtil.LIST_ID + "=?", str);
		db.delete(DBUtil.TABLE_TASK, DBUtil.TASK_LISTID + "=?", str);
		//close the connection
		db.close();
	}

	// Insert a list to the db.
	
	/**
	 * addList
	 * Input : List object with the list details
	 * Output : new list id
	 * Description : create a new list for a user
	 */
	public Integer addList(Context context, List list) {
		ContentValues values = new ContentValues();
		// set the values for insert query
		values.put(DBUtil.LIST_NAME, list.getName());
		values.put(DBUtil.LIST_SHOWCOMPLETED, 1);
		values.put(DBUtil.LIST_USERID, list.getUser_id());

		// perform the insert
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		Long listId = db.insert(DBUtil.TABLE_LIST, null, values);
		Integer newListID = listId.intValue();
		//close the connection
		db.close();
		return newListID;
	}

	// Update a list's name to the db
	/**
	 * updateList
	 * Input : Listid
	 * Output : nil
	 * Description : rename the list given its id
	 */
	public void updateList(Context context, int listId, String listName) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		// form the update query
		ContentValues values = new ContentValues();
		values.put(DBUtil.LIST_NAME, listName);
		db.update(DBUtil.TABLE_LIST, values, DBUtil.LIST_ID + "=?",
				new String[] { String.valueOf(listId) });
		//close the connection
		db.close();
	}

	// List ID got by user id and list name
	/**
	 * getListIdByName
	 * Input : userid,listname
	 * Output : listid
	 * Description : returns the listId of the given list - temporary method used for testing purpose
	 */
	public int getListIdByName(Context context, Integer userID, String listName) {
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		int listID = 0;
		// for the select query and set the details
		Cursor cursor = db.query(DBUtil.TABLE_LIST,
				new String[] { DBUtil.LIST_ID, }, DBUtil.LIST_USERID
						+ "=? and " + DBUtil.LIST_NAME + "=?", new String[] {
						String.valueOf(userID), listName }, null, null, null,
				null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			listID = cursor.getInt(0);
		}
		//close the connection
		cursor.close();
		db.close();
		return listID;
	}

	// Editing a task
	/**
	 * editTaskForUser
	 * Input : task obj, listid
	 * Output : nil
	 * Description : edit a task given task details.
	 */
	public void editTaskForUser(Context context, Task taskItem, Integer ListId) {
		ContentValues values = new ContentValues();
		// set the update parameters
		values.put(DBUtil.TASK_ALERT, taskItem.getAlert());
		values.put(DBUtil.TASK_DUEDATE, taskItem.getDueDate());
		values.put(DBUtil.TASK_ISCOMP, 0);
		values.put(DBUtil.TASK_ISENABLED, 1);
		values.put(DBUtil.TASK_NOTES, taskItem.getNotes());
		values.put(DBUtil.TASK_LISTID, ListId);
		values.put(DBUtil.TASK_PRIORITY, taskItem.getPriority());
		values.put(DBUtil.TASK_NAME, taskItem.getName());
		// invoke the update query
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		db.update(DBUtil.TABLE_TASK, values, DBUtil.TASK_ID + "=?",
				new String[] { String.valueOf(taskItem.getTaskId()) });
		//close the connection
		db.close();
	}

	// Insert a Task into to the database.
	
	/**
	 * addTaskForUser
	 * Input : task,list
	 * Output : new taskid
	 * Description : add a task into a list for given user
	 */
	public Integer addTaskForUser(Context context, Task taskItem, Integer ListId) {
		ContentValues values = new ContentValues();
		// set all the insert params
		values.put(DBUtil.TASK_ALERT, taskItem.getAlert());
		values.put(DBUtil.TASK_DUEDATE, taskItem.getDueDate());
		values.put(DBUtil.TASK_ISCOMP, 0);
		values.put(DBUtil.TASK_ISENABLED, 1);
		values.put(DBUtil.TASK_NOTES, taskItem.getNotes());
		values.put(DBUtil.TASK_LISTID, ListId);
		values.put(DBUtil.TASK_PRIORITY, taskItem.getPriority());
		values.put(DBUtil.TASK_NAME, taskItem.getName());

		// create a new task row
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		Long taskId = db.insert(DBUtil.TABLE_TASK, null, values);
		//close the connection
		db.close();
		// return new tasks id
		Integer newTaskId = taskId.intValue();
		logger.info("added a new task -->" + newTaskId);
		return newTaskId;
	}

	// get details of the task....
	/**
	 * getTaskById
	 * Input : taskid
	 * Output : task Obj
	 * Description : returns the task details given the taskid
	 */
	public Task getTaskById(Context context, int taskid) {
		Task taskItem = new Task();
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		String selectQryParams[];
		// form the select query
		selectQryParams = new String[] { String.valueOf(taskid) };
		Cursor cursor = db.query(DBUtil.TABLE_TASK, new String[] {
				DBUtil.TASK_NAME, DBUtil.TASK_ID, DBUtil.TASK_ALERT,
				DBUtil.TASK_ISCOMP, DBUtil.TASK_ISENABLED, DBUtil.TASK_LISTID,
				DBUtil.TASK_NOTES, DBUtil.TASK_PRIORITY, DBUtil.TASK_DUEDATE },
				DBUtil.TASK_ID + "=?", selectQryParams, null, null,
				DBUtil.TASK_DUEDATE, null); // check the sort order ..
		//set the details into a task obj
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String taskname = cursor.getString(0);
			Long taskalert = cursor.getLong(2);
			int taskcomplete = cursor.getInt(3);
			int taskenabled = cursor.getInt(4);
			String tasknotes = cursor.getString(6);
			int taskpriority = cursor.getInt(7);
			Long taskduedate = cursor.getLong(8);
			Integer listid = cursor.getInt(5);
			taskItem = new Task(taskname, taskduedate, taskpriority, taskid,
					taskalert, taskcomplete, taskenabled, tasknotes, listid);
			logger.info(taskItem.getName());
		}
		//close the connection
		cursor.close();
		db.close();
		return taskItem;
	}

	// fetch the complete list of tasks ....
	
	/**
	 * getTasksForCurrentUser
	 * Input : listid,sortBy option
	 * Output : list of tasks categorized into specific units
	 * Description : fetches all the missed, upcoming and completed tasks for the user in this list.
	 */
	public HashMap<String, ArrayList<Task>> getTasksForCurrentUser(
			Context context, Integer listId, int sortBy) {
		
		// init variables
		ArrayList<Task> taskItemsAllCompleted = new ArrayList<Task>();
		ArrayList<Task> taskItemsUnCompleted = new ArrayList<Task>();
		ArrayList<Task> taskItemsUnCompletedMissed = new ArrayList<Task>();
		ArrayList<Task> taskItemsAllCompletedNoDue = new ArrayList<Task>();
		ArrayList<Task> taskItemsUnCompletedNoDue = new ArrayList<Task>();
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();

		// form the select query based on the sort params
		String sortByetc = "";
		Boolean sortByp = false;
		String selectQryParams[];
		selectQryParams = new String[] { String.valueOf(listId) };

		if (sortBy == List.SORTBY_PRIORITY) {
			sortByetc = dbUtil.TASK_PRIORITY + " DESC" + ", ";
			sortByp = true;
		}

		Cursor cursor = db.query(DBUtil.TABLE_TASK, new String[] {
				DBUtil.TASK_NAME, DBUtil.TASK_ID, DBUtil.TASK_ALERT,
				DBUtil.TASK_ISCOMP, DBUtil.TASK_ISENABLED, DBUtil.TASK_LISTID,
				DBUtil.TASK_NOTES, DBUtil.TASK_PRIORITY, DBUtil.TASK_DUEDATE },
				DBUtil.TASK_LISTID + "=?", selectQryParams, null, null,
				sortByetc + DBUtil.TASK_DUEDATE, null); // check the sort order
														// ..
		cursor.moveToFirst();
		int curpriority = -1;
		
		//fetch and assign the task to the specific unit
		while (!cursor.isAfterLast()) {
			String taskname = cursor.getString(0);
			int taskid = cursor.getInt(1);
			Long taskalert = cursor.getLong(2);
			int taskcomplete = cursor.getInt(3);
			int taskenabled = cursor.getInt(4);
			String tasknotes = cursor.getString(6);
			int taskpriority = cursor.getInt(7);
			Long taskduedate = (long) cursor.getLong(8);
			Task taskItem = new Task(taskname, taskduedate, taskpriority,
					taskid, taskalert, taskcomplete, taskenabled, tasknotes,
					listId);

			if (sortByp && curpriority != taskpriority) {
				taskItemsAllCompleted.addAll(taskItemsAllCompletedNoDue);
				taskItemsUnCompleted.addAll(taskItemsUnCompletedNoDue);
				taskItemsAllCompletedNoDue.clear();
				taskItemsUnCompletedNoDue.clear();
				curpriority = taskpriority;
			}
			if (taskcomplete == Task.TASK_COMPLETE) {
				if (taskduedate != -1)
					taskItemsAllCompleted.add(taskItem);
				else
					taskItemsAllCompletedNoDue.add(taskItem);
			} else {
			// check for missed dates and have a marker
				Calendar cld = Calendar.getInstance();
				Date today = cld.getTime();
				Long currMillis = today.getTime();
				if (taskduedate != -1 && currMillis > taskduedate) {
					taskItem.toggleMissed();//
					taskItemsUnCompletedMissed.add(taskItem);
				} else {
					if (taskduedate != -1)
						taskItemsUnCompleted.add(taskItem);
					else
						taskItemsUnCompletedNoDue.add(taskItem);
				}
			}
			cursor.moveToNext();
		}
		
		//close the connection
		cursor.close();
		db.close();
		
		// set the units into the result map and return
		HashMap<String, ArrayList<Task>> tasksInTheList = new HashMap<String, ArrayList<Task>>();
		taskItemsAllCompleted.addAll(taskItemsAllCompletedNoDue);
		taskItemsUnCompleted.addAll(taskItemsUnCompletedNoDue);
		tasksInTheList.put("AllCompleted", taskItemsAllCompleted);
		tasksInTheList.put("UnCompleted", taskItemsUnCompleted);
		tasksInTheList.put("UnCompletedMissedTasks", taskItemsUnCompletedMissed);
		return tasksInTheList;
	}

	// Mark a task as complete or incomplete ....
	// send completeStatus = 1 - complete or 0 - incomplete
	/**
	 * markTaskAsComplete
	 * Input : taskid, completion status
	 * Output : nil
	 * Description : sets the given task complete
	 */
	public void markTaskAsComplete(Context context, Integer taskId,
			int completeStatus) {
		// form the update query
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBUtil.TASK_ISCOMP, completeStatus);
		db.update(DBUtil.TABLE_TASK, values, DBUtil.TASK_ID + "=?",
				new String[] { String.valueOf(taskId) });
		//close the connection
		db.close();
	}

	// Clear completed tasks of a list..
	/**
	 * clearCompletedTasks
	 * Input : listid
	 * Output : nil
	 * Description : delete all the completed tasks for this list
	 */
	public void clearCompletedTasks(Context context, Integer listId) {
		DBUtil dbUtil = new DBUtil(context);
		// form the delete query
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		db.delete(DBUtil.TABLE_TASK, DBUtil.TASK_LISTID + "=? AND "
				+ DBUtil.TASK_ISCOMP + "=?",
				new String[] { String.valueOf(listId), String.valueOf(1) });
		//close the connection
		db.close();
	}

	// delete a task ..
	/**
	 * deleteTask
	 * Input : taskid
	 * Output : nil
	 * Description : delete the specified task
	 */
	public void deleteTask(Context context, Integer taskId) {
		DBUtil dbUtil = new DBUtil(context);
		// form the delete query
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		db.delete(DBUtil.TABLE_TASK, DBUtil.TASK_ID + "=?",
				new String[] { String.valueOf(taskId) });
		//close the connection
		db.close();
	}
}
