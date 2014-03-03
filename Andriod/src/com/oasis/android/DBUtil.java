package com.oasis.android;

import java.util.logging.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Takes care of Db connections
 * 
 */
public class DBUtil extends SQLiteOpenHelper {

	// Database
	private static final String DB_NAME = "GTodoDB";

	// Upgrade Handling
	private static final int DB_VERSION_NUMBER = 2;

	// Tables for GTodo
	public static final String TABLE_USER = "User";
	public static final String TABLE_LIST = "List";
	public static final String TABLE_TASK = "Task";

	// Columns for each Table
	public static final String USER_USERID = "userid";
	public static final String USER_USERNAME = "name";
	public static final String USER_PWD = "password";
	public static final String USER_SECQTN = "securityQ";
	public static final String USER_SECANS = "securityA";

	public static final String LIST_NAME = "name";
	public static final String LIST_SHOWCOMPLETED = "showcompleted";
	public static final String LIST_ID = "listid";
	public static final String LIST_USERID = "userid";

	public static final String TASK_NAME = "name";
	public static final String TASK_ID = "taskid";
	public static final String TASK_DUEDATE = "duedate";
	public static final String TASK_NOTES = "notes";
	public static final String TASK_PRIORITY = "priority";
	public static final String TASK_ALERT = "alert";
	public static final String TASK_ISCOMP = "iscomplete";
	public static final String TASK_ISENABLED = "isenabled";
	public static final String TASK_LISTID = "listid";

	public static final String defaultListName = "ToDo";

	private static Logger logger = Logger.getLogger("GTodo");

	public DBUtil(Context context) {
		super(context, DB_NAME, null, DB_VERSION_NUMBER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// create all the tables for the application
		String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ USER_USERID + " INTEGER PRIMARY KEY autoincrement" + ","
				+ USER_USERNAME + " text not null" + "," + USER_PWD
				+ " text not null" + "," + USER_SECQTN + " text" + ","
				+ USER_SECANS + " text)";
		db.execSQL(CREATE_USERS_TABLE);

		String CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_LIST + "(" + LIST_ID
				+ " INTEGER PRIMARY KEY autoincrement" + "," + LIST_NAME
				+ " text not null" + "," + LIST_SHOWCOMPLETED
				+ " Integer default 1" + "," + LIST_USERID + " Integer" + ","
				+ "FOREIGN KEY(" + LIST_USERID + ") REFERENCES " + TABLE_USER
				+ "(" + USER_USERID + "))";
		db.execSQL(CREATE_LIST_TABLE);

		String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASK + "(" + TASK_ID
				+ " INTEGER PRIMARY KEY autoincrement" + "," + TASK_NAME
				+ " text not null" + "," + TASK_ALERT + " Integer" + ","
				+ TASK_DUEDATE + " Integer" + "," + TASK_ISCOMP
				+ " Integer default 0" + "," + TASK_ISENABLED
				+ " Integer default 1" + "," + TASK_NOTES + " text" + ","
				+ TASK_PRIORITY + " Integer default 1" + "," + TASK_LISTID
				+ " Integer" + ",FOREIGN KEY(" + TASK_LISTID + ") REFERENCES "
				+ TABLE_LIST + "(" + LIST_ID + "))";
		db.execSQL(CREATE_TASK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		// not required for now ... add db changes for successive revisions here
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);

		// Create tables again
		onCreate(db);
	}

}
