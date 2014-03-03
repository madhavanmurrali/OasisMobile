package com.oasishome.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.html.HTMLUListElement;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.oasishome.server.User;

public class DBUtil {

	private static final Logger log = Logger.getLogger(DBUtil.class.getName());
	
	// User Managemnent code ..
	public boolean selectUser(String name) {
		Filter userNameFilter = new FilterPredicate(
				DBUtilConstants.COLUMN_NAME, FilterOperator.EQUAL, name);
		Query q = new Query(DBUtilConstants.TABLE_USERS)
				.setFilter(userNameFilter);
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		int cnt = 0;
		for (@SuppressWarnings("unused") Entity res : pq.asIterable()) {
			cnt++;
		}
		if (cnt != 0)
			return true;
		return false;
	}

	/**
	 * Validate User
	 * Input : user details contained in the User object.
	 * Output : Status of the database transaction and login details.
	 * Description : Checks the login information.
	 */

	public Long validateUserLogin(String name, String pwd) {
		Filter userNameFilter = new FilterPredicate(
				DBUtilConstants.COLUMN_NAME, FilterOperator.EQUAL, name);
		Filter pwdFilter = new FilterPredicate(DBUtilConstants.COLUMN_PASSWORD,
				FilterOperator.EQUAL, pwd);
		Query q = new Query(DBUtilConstants.TABLE_USERS)
				.setFilter(CompositeFilterOperator.and(userNameFilter,
						pwdFilter));
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		int cnt = 0;
		Long id = null;
		for (Entity res : pq.asIterable()) {
			id = (Long) res.getKey().getId();
			cnt++;
		}
		if (cnt != 0)
			return id;
		return id;
	}

	/**
	 * getUserByID
	 * Input : userid
	 * Output : User Object representing his information
	 * Description : gets the user by id.
	 */

	
	public User getUserById(long id) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Entity userent;
		User newUser = new User();

		try {
			userent = datastore.get(KeyFactory.createKey(
					DBUtilConstants.TABLE_USERS, id));
			newUser.setId(userent.getKey().getId());
			newUser.setName((String) userent
					.getProperty(DBUtilConstants.COLUMN_NAME));

		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "There is an exception, if not expected, uncomment line below to debug");
			// e.printStackTrace();
			return null;
		}
		return newUser;
	}

	public boolean updateUsers(User userObj) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity user = new Entity(DBUtilConstants.TABLE_USERS, userObj.getId());
		user.setProperty(DBUtilConstants.COLUMN_NAME, userObj.getName());
		user.setProperty(DBUtilConstants.COLUMN_PASSWORD, userObj.getPwd());
		datastore.put(user);
		userObj.setId(user.getKey().getId());
		return true;
	}

	/**
	 * addNewUser
	 * Input : user details contained in the User object.
	 * Output : Status of the database transaction and new user details.
	 * Description : Adds a new user , creates default list and returns the new user id.
	 */

	public User createUser(User userObj) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity user = new Entity(DBUtilConstants.TABLE_USERS);
		user.setProperty(DBUtilConstants.COLUMN_NAME, userObj.getName());
		user.setProperty(DBUtilConstants.COLUMN_PASSWORD, userObj.getPwd());
		user.setProperty(DBUtilConstants.COLUMN_MICROADDRESS, userObj.getMicAddress());
		datastore.put(user);
		userObj.setId(user.getKey().getId());
		return userObj;
	}

	/**
	 * deleteUser
	 * Input : user details contained in the User object.
	 * Output : Status of the database transaction.
	 * Description : delete the user provided.
	 */

	public void deleteUser(User user) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.delete(KeyFactory.createKey(DBUtilConstants.TABLE_USERS,
				user.getId()));
	}

	/*
	 * public List<User> getAllUsers() { List<User> allusers = new
	 * ArrayList<Users>();
	 * 
	 * Cursor cursor = database.query(DBUtilConstants.TABLE_USERS, allColumns,
	 * null, null, null, null, null);
	 * 
	 * cursor.moveToFirst(); while (!cursor.isAfterLast()) { Users user =
	 * cursorToUsers(cursor); allusers.add(user); cursor.moveToNext(); } // Make
	 * sure to close the cursor cursor.close(); return allusers; }
	 */

	/*
	 * private Users cursorToUsers(Cursor cursor) { Users user = new Users();
	 * user.setId(cursor.getLong(0)); user.setName(cursor.getString(1));
	 * user.setPwd(cursor.getString(2)); return user; }
	 */

	// Task Management reg code ...

	/**
	 * getItemByID
	 * Input : item ID.
	 * Output : item details in the ToDOItem Object.
	 * Description : gets the Item details from the database.
	 */

	public Device getItemByItemId(long id) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Entity taskent;
		Device item = new Device();
		// form the query
		try {
			taskent = datastore.get(KeyFactory.createKey(
					DBUtilConstants.TABLE_DEVICES, id));
			item = cursorToToDoItem(taskent);

		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return item;
	}

	/**
	 * createItem
	 * Input : item details contained in the ToDOItem object.
	 * Output : Status of the database transaction and new item details.
	 * Description : Adds a new item and returns the new item id.
	 */

	public long createItem(Device item) {
		Calendar cld = Calendar.getInstance();
		Long timenow = cld.getTimeInMillis();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity task = new Entity(DBUtilConstants.TABLE_DEVICES);
		task.setProperty(DBUtilConstants.DEVICE_USERID, item.getUserId());
		task.setProperty(DBUtilConstants.DEVICE_NAME, item.getName());
		task.setProperty(DBUtilConstants.DEVICE_NICKNAME, item.getNickname());
		task.setProperty(DBUtilConstants.DEVICE_PRIORITY,
				item.getPriority());
		task.setProperty(DBUtilConstants.DEVICE_ACTIVE, item.isActive());
		task.setProperty(DBUtilConstants.DEVICE_STATE, item.isStatus());
		task.setProperty(DBUtilConstants.DEVICE_MICROCONTROLLERADDRESS, item.getMicAddr());
		task.setProperty(DBUtilConstants.DEVICE_LAST_MODIFIED,timenow);
		task.setProperty(DBUtilConstants.DEVICE_ACTIVE, item.isActive());
		task.setProperty(DBUtilConstants.DEVICE_PRIORITY, 0);
		task.setProperty(DBUtilConstants.DEVICE_DELETED, false);
		datastore.put(task);
		long taskid = (task.getKey().getId());
		return taskid;
	}
	
	/**
	 * createItem
	 * Input : item details contained in the ToDOItem object.
	 * Output : Status of the database transaction and new item details.
	 * Description : Adds a new item and returns the new item id.
	 */

	public void createStatEntry(Device item) {
		Calendar cld = Calendar.getInstance();
		Long timenow = cld.getTimeInMillis();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity task = new Entity(DBUtilConstants.TABLE_DEVICEHISTORY);
		task.setProperty(DBUtilConstants.DEVICE_HISTORY_DEVICE_ID, item.getId());
		task.setProperty(DBUtilConstants.DEVICE_HISTORY_STATUS, item.isStatus());
		task.setProperty(DBUtilConstants.DEVICE_HISTORY_TIME, timenow);		
		datastore.put(task);		
	}


	public Long getDeviceByMICANDNAME(String micAddress,String devName) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity taskent;
		Device item = new Device();Long id = null;
		Filter userNameFilter = new FilterPredicate(
				DBUtilConstants.DEVICE_MICROCONTROLLERADDRESS, FilterOperator.EQUAL, micAddress);
		Filter pwdFilter = new FilterPredicate(DBUtilConstants.DEVICE_NAME,
				FilterOperator.EQUAL, devName);
		Query q = new Query(DBUtilConstants.TABLE_DEVICES)
				.setFilter(CompositeFilterOperator.and(userNameFilter,
						pwdFilter));			
		PreparedQuery pq = datastore.prepare(q);
		int cnt = 0;
		
		for (Entity res : pq.asIterable()) {
			id = (Long) res.getKey().getId();
			cnt++;
		}
		if (cnt != 0)
			return id;
		return id;
	}
	
	
	/**
	 * deleteItem
	 * Input : item details contained in the TodoItem object.
	 * Output : Status of the database transaction.
	 * Description : delete the given item.
	 */
/*
	public void deleteItem(Long taskid, Long userid) {
			ToDoItem item = getItemByItemId(taskid);
		if (item.getUserId() != null && item.getUserId().equals(userid)) {
			item.setIsdeleted(true);
			updateItem(item);
		}
		return;
	}

	/**
	 * checkItem
	 * Input : item details contained in the TodoItem object.
	 * Output : Status of the database transaction.
	 * Description : mark an item as complete.
	 */
/*
	public void completeItem(Long taskid, Long userid) {
		ToDoItem item = getItemByItemId(taskid);
		if (item.getUserId() != null && item.getUserId().equals(userid)) {
			item.setChecked(true);
			updateItem(item);
		}
		return;
	}
*/
	/**
	 * updateItem
	 * Input : item details contained in the todoItem object.
	 * Output : Status of the database transaction.
	 * Description : update item.
	 */

	public Device updateItem(Device item) {
		Calendar cld = Calendar.getInstance();
		Long timenow = cld.getTimeInMillis();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity task = new Entity(DBUtilConstants.TABLE_DEVICES, item.getId());
		task.setProperty(DBUtilConstants.DEVICE_USERID, item.getUserId());
		task.setProperty(DBUtilConstants.DEVICE_NAME, item.getName());
		task.setProperty(DBUtilConstants.DEVICE_NICKNAME, item.getNickname());
		task.setProperty(DBUtilConstants.DEVICE_PRIORITY,
				item.getPriority());
		task.setProperty(DBUtilConstants.DEVICE_ACTIVE, item.isActive());
		task.setProperty(DBUtilConstants.DEVICE_STATE, item.isStatus());
		task.setProperty(DBUtilConstants.DEVICE_MICROCONTROLLERADDRESS, item.getMicAddr());
		task.setProperty(DBUtilConstants.DEVICE_LAST_MODIFIED,timenow);
		task.setProperty(DBUtilConstants.DEVICE_ACTIVE, item.isActive());
		task.setProperty(DBUtilConstants.DEVICE_PRIORITY, item.getPriority());
		task.setProperty(DBUtilConstants.DEVICE_DELETED, item.isIsdeleted());
		datastore.put(task);
		return item;
	}

	/**
	 * getListOfItemsForUser
	 * Input : user details contained in the GtodoUser object.
	 * Output : Status of the database transaction and list of tasks details.
	 * Description : get the items for the user.
	 */

	public HashMap<String, ArrayList<Device>> getDevicesListing(Long userId,String micAddress) {
		System.out.println("****************SUCCESS");
		try{
			
		ArrayList<Device> inactiveList = new ArrayList<Device>();
		ArrayList<Device> activeList = new ArrayList<Device>();
		Filter userNameFilter = new FilterPredicate(
				DBUtilConstants.DEVICE_USERID, FilterOperator.EQUAL,
				userId);
		Filter pwdFilter = new FilterPredicate(
				DBUtilConstants.DEVICE_DELETED, FilterOperator.EQUAL,
				false);
		Filter micFilter = new FilterPredicate(
				DBUtilConstants.DEVICE_MICROCONTROLLERADDRESS, FilterOperator.EQUAL,
				micAddress);
		Query q = new Query(DBUtilConstants.TABLE_DEVICES)
				.setFilter(CompositeFilterOperator.and(userNameFilter,
						pwdFilter/*,micFilter*/));
		q.addSort(DBUtilConstants.DEVICE_LAST_MODIFIED, SortDirection.ASCENDING);
		System.out.print("SUCCESS");
		// form the query and save the tasks into 3 sets
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		System.out.print("SUCCESS"+pq.countEntities());
		for (Entity res : pq.asIterable()) {
			Device item = cursorToToDoItem(res);
			if (item.isActive()) {
				activeList.add(item);
			} else {
				inactiveList.add(item);
			}

		}
		// form the result and return
		HashMap<String, ArrayList<Device>> tasksInTheList = new HashMap<String, ArrayList<Device>>();
		tasksInTheList.put("ActiveList", activeList);
		tasksInTheList.put("InActiveList", inactiveList);
		System.out.print(activeList);

		return tasksInTheList;
		}catch(Exception exp){
			exp.printStackTrace();
			System.out.print("SUCCESS"+exp.toString());
			return null;
		}
		
	}
	/*
	public ArrayList<ToDoItem> getAllItemsByIdSync(long userId) {
		
		ArrayList<ToDoItem> fullList = new ArrayList<ToDoItem>();
		Query q = new Query(DBUtilConstants.TABLE_TODOLIST);
		Filter userNameFilter = new FilterPredicate(
				DBUtilConstants.COLUMN_TASK_USERID, FilterOperator.EQUAL, userId);
		q.setFilter(userNameFilter);
		q.addSort(DBUtilConstants.COLUMN_TASK_MODIFIED, SortDirection.ASCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//	try{
		PreparedQuery pq = datastore.prepare(q);
		for (Entity res : pq.asIterable()) {
			ToDoItem item = cursorToToDoItemSync(res);
			fullList.add(item);
		}
		/*}catch(Exception exp){
			
		}*/
	/*	return fullList;
	}
*/

	/*
	 * public List<ToDoItem> getInCompleteListByUId(long userId) {
	 * List<ToDoItem> list = new ArrayList<ToDoItem>(); Filter userNameFilter =
	 * new FilterPredicate( DBUtilConstants.COLUMN_TASK_USERID,
	 * FilterOperator.EQUAL, userId); Query q = new
	 * Query(DBUtilConstants.TABLE_USERS) .setFilter((userNameFilter));
	 * DatastoreService datastore = DatastoreServiceFactory
	 * .getDatastoreService(); PreparedQuery pq = datastore.prepare(q); for
	 * (Entity res : pq.asIterable()) { ToDoItem item = cursorToToDoItem(res);
	 * list.add(item); } return list; }
	 */

	/*
	 * public List<ToDoItem> getAllToDo() { List<ToDoItem> list = new
	 * ArrayList<ToDoItem>();
	 * 
	 * Cursor cursor = database.query(MySQLiteHelper.TABLE_TODOLIST, allColumns,
	 * null, null, null, null, null);
	 * 
	 * cursor.moveToFirst(); while (!cursor.isAfterLast()) { ToDoItem item =
	 * cursorToToDoItem(cursor); list.add(item); cursor.moveToNext(); } // Make
	 * sure to close the cursor cursor.close(); return list; }
	 */

	/**
	 * formToDoItem
	 * Input : database result.
	 * Output : todoItem object.
	 * Description : converts the database result into the todoitem object.
	 */

	private Device cursorToToDoItem(Entity result) {
		Device deviceObj = new Device();
		deviceObj.setId(result.getKey().getId());
		deviceObj.setUserId((Long) result
				.getProperty(DBUtilConstants.DEVICE_USERID));
		deviceObj.setName((String) result
				.getProperty(DBUtilConstants.DEVICE_NAME));
		deviceObj.setNickname((String) result
				.getProperty(DBUtilConstants.DEVICE_NICKNAME));
		deviceObj.setActive((Boolean) result
				.getProperty(DBUtilConstants.DEVICE_ACTIVE));
		deviceObj.setIsdeleted((Boolean) result
				.getProperty(DBUtilConstants.DEVICE_DELETED));
		deviceObj.setMicAddr((String) result
				.getProperty(DBUtilConstants.DEVICE_MICROCONTROLLERADDRESS));
		deviceObj.setMod_date((Long) result
				.getProperty(DBUtilConstants.DEVICE_LAST_MODIFIED));
		deviceObj.setStatus((Boolean) result
				.getProperty(DBUtilConstants.DEVICE_STATE));
		try{
		deviceObj.setPriority((Long) result
				.getProperty(DBUtilConstants.DEVICE_PRIORITY));
		}catch(Exception e){
			
		}
		return deviceObj;
	}
	
	// for syncing
	/*private ToDoItem cursorToToDoItemSync(Entity result) {
		ToDoItem item = new ToDoItem();
		item.setId(result.getKey().getId());
		item.setUserId((Long) result
				.getProperty(DBUtilConstants.COLUMN_TASK_USERID));
		item.setName((String) result
				.getProperty(DBUtilConstants.COLUMN_TASK_TODONAME));
		item.setNote((String) result
				.getProperty(DBUtilConstants.COLUMN_TASK_TODONOTE));
		item.setDueTime((Long) result
				.getProperty(DBUtilConstants.COLUMN_TASK_DUETIME));
		item.setMod_date((Long) result
				.getProperty(DBUtilConstants.COLUMN_TASK_MODIFIED));
		Long duetime = item.getDueTime();
		String dueDateStr = "";
		int hr=0;
		int sc=0;
		int min=0;
		
		if (duetime != null && duetime != -1) {
			try {
				Calendar cld = Calendar.getInstance();
				cld.setTimeInMillis(duetime);
				min = cld.get(Calendar.MINUTE);
				hr =cld.get(Calendar.HOUR);
				sc = cld.get(Calendar.SECOND);
				SimpleDateFormat dformat = new SimpleDateFormat(
						"dd-MMM-yyyy");
				dueDateStr = dformat.format(duetime);
			} catch (NullPointerException exp) {
				dueDateStr = "";
			}
		}
		item.setDuedatestr(dueDateStr);
		item.setDate_hr(hr);
		item.setDate_min(min);
		item.setDate_sec(sc);
		item.setNoDueTime((Boolean) result
				.getProperty(DBUtilConstants.COLUMN_TASK_NODUETIME));
		item.setPriority((Long) result
				.getProperty(DBUtilConstants.COLUMN_TASK_PRIORITY));
		item.setChecked((Boolean) result
				.getProperty(DBUtilConstants.COLUMN_TASK_CHECKED));
		item.setIsdeleted((Boolean) result
				.getProperty(DBUtilConstants.COLUMN_TASK_DELETED));
		return item;
	}*/
	
//	public static JSONObject getJSONObject(Device item) throws JSONException{
//		JSONObject jsonobj = new JSONObject();
//		jsonobj.put("name", item.getName());
//		jsonobj.put("date", item.getDuedatestr());
//		jsonobj.put("prior", item.getPriority());
//		jsonobj.put("hr", item.getDate_hr());
//		jsonobj.put("sec", item.getDate_sec());
//		jsonobj.put("min", item.getDate_min());
//		jsonobj.put("notes", item.getNote());
//		jsonobj.put("nodue", item.isNoDueTime()?"false":"checked");
//		jsonobj.put("taskid", item.getId());
//		return jsonobj;
//	}

}
