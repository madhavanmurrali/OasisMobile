package com.oasis.android;

import java.util.ArrayList;
import java.util.HashMap;

import com.oasis.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Tasks page activity
 * @author Pujita
 */
public class Tasks extends Activity implements OnClickListener{
	private int listId;
	public SessionManager  sm;
	private DBOperationsHandler dboper;
	private View backButton, logoutButton, settingButton, addButton;
	private TextView listNameLabel;
	private ListView listView, completedListView;
	private static boolean isCompletedHidden;
	private static boolean isSortByPriority;
	private TaskListArrayAdapter taskListAdapter, completedTaskListAdapter;
	private ArrayList<Task> tasksByDate, completedTasks;
	private int currentTaskIndex, listNumber;
	private static final int SETTINGS_SHOW = 1;
	private static final int SETTINGS_DELETE = 2;
	private static final int SETTINGS_SORT = 3;
	private static final int LIST_RENAME = 4;
	private static final int LIST_DELETE = 5;
	private static final int LIST_COMPLETE = 6;
	public static final int UNCOMPLETED_TASKS_LIST = 1;
	public static final int COMPLETED_TASKS_LIST = 2;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_page);
		
		// Get current list details from the database
        sm = new SessionManager();
        sm.getCurrentListDetails(this);
        listId = sm.getCurrentListID();
        String currentListName = sm.getCurrentListName();

        // Get all the relevant Views form layout
		backButton = (ImageView) findViewById(R.id.tasksPageBack);
		logoutButton = (ImageView) findViewById(R.id.tasksPageLogout);
		settingButton = (ImageView) findViewById(R.id.tasksPageEdit);
		addButton = (ImageView) findViewById(R.id.tasksPageAdd);
        listNameLabel = (TextView)findViewById(R.id.tasksPageTitleLabel);
		listNameLabel.setText(currentListName); // change this

		// Add listeners
		settingButton.setOnClickListener((OnClickListener) this);
		addButton.setOnClickListener((OnClickListener) this);
		backButton.setOnClickListener((OnClickListener) this);
		logoutButton.setOnClickListener((OnClickListener) this);
		
		// Initialize all tasks		
		HashMap<String, ArrayList<Task>> listsMap = new HashMap<String, ArrayList<Task>>();
		dboper = new DBOperationsHandler();
		if (isSortByPriority) {
			listsMap = dboper.getTasksForCurrentUser(this, listId, List.SORTBY_PRIORITY);
		} else {
			listsMap = dboper.getTasksForCurrentUser(this, listId, List.SORTBY_DATE);
		}
		
		tasksByDate = listsMap.get("UnCompletedMissedTasks");
		tasksByDate.addAll(listsMap.get("UnCompleted"));
		completedTasks = listsMap.get("AllCompleted");

		
		// Find all lists and add TaskListArrayAdapter to them
		listView = (ListView) findViewById(R.id.tasksList);
		completedListView = (ListView) findViewById(R.id.tasksCompletedList);
		
		// Set tasks to be shown depending on sort option
		taskListAdapter = new TaskListArrayAdapter(this, tasksByDate, UNCOMPLETED_TASKS_LIST);		
		completedTaskListAdapter = new TaskListArrayAdapter(this, completedTasks, COMPLETED_TASKS_LIST);

		// Assign adapter to ListView
		listView.setAdapter(taskListAdapter);
		completedListView.setAdapter(completedTaskListAdapter);		
		
		// Show or hide completed tasks
		if(isCompletedHidden) {
			completedListView.setVisibility(View.INVISIBLE);		
		} else {
			completedListView.setVisibility(View.VISIBLE); 
		}		
	}

	/**
	 * Once a task is checked, remove it from task list and shift it to completed list
	 * @param position index of the Task
	 */
	public void checkTask(int position) {
		Task task = tasksByDate.get(position);
		task.toggleCompleted();
		dboper.markTaskAsComplete(this, task.getTaskId(), 1);
		tasksByDate.remove(position);		
		completedTasks.add(task);
		this.taskListAdapter.notifyDataSetChanged();
		this.completedTaskListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * If a completed task is unchecked, add it back to the task list and remove it from the completed list
	 * @param position index of the Task
	 */
	public void uncheckTask(int position) {		
		Task task = completedTasks.get(position);
		task.toggleCompleted();
				
		// Remove from completed list
		completedTasks.remove(position);
		this.completedTaskListAdapter.notifyDataSetChanged();
		
		// Add to database
		dboper.markTaskAsComplete(this, task.getTaskId(), 0);
		HashMap<String, ArrayList<Task>> listsMap = dboper.getTasksForCurrentUser(this, listId, List.SORTBY_DATE);
		tasksByDate = listsMap.get("UnCompletedMissedTasks");
		tasksByDate.addAll(listsMap.get("UnCompleted"));
		// Refresh list adapter
		taskListAdapter = new TaskListArrayAdapter(this, tasksByDate, UNCOMPLETED_TASKS_LIST);
		listView.setAdapter(taskListAdapter);
	}
	
	/**
	 * Onclick method for all the buttons on the screen
	 */
	//@Override
	public void onClick(View view) {
		if (view == this.backButton) {
			this.startActivity(new Intent(this, UserDevicesList.class));
		} else if (view == this.logoutButton) {
			SessionManager sm = new SessionManager();
			sm.logoutUser(this);
			Intent loginpage = new Intent(this, Login.class);
			loginpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(loginpage);
		} else if (view == this.addButton) {
			SessionManager sm = new SessionManager();
			sm.setCurrentTaskID(this,-1);
			this.startActivity(new Intent(this, TaskDescription.class));
		} else if (view == this.settingButton) {
			registerForContextMenu(view); 
		    openContextMenu(view);
		    unregisterForContextMenu(view);
		}
	}
	
	@Override 
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if(v == this.settingButton){
			this.createSettingsMenu(menu);
		}else {
			this.createTasksMenu(menu);
		}	
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case SETTINGS_SHOW:
			if(isCompletedHidden) {
				completedListView.setVisibility(View.VISIBLE);
				isCompletedHidden = false;
			} else {
				completedListView.setVisibility(View.INVISIBLE); 
				isCompletedHidden = true;
			}			
			break;
			
		case SETTINGS_DELETE:			
			this.completedTaskListAdapter.clear();
			this.completedTaskListAdapter.notifyDataSetChanged();
			dboper.clearCompletedTasks(this,listId);
			break;
			
		case SETTINGS_SORT:					
			HashMap<String, ArrayList<Task>> listsMap = new HashMap<String, ArrayList<Task>>();
			if (!isSortByPriority) {
				listsMap = dboper.getTasksForCurrentUser(this, listId, List.SORTBY_PRIORITY);
				isSortByPriority = true;
			} else {
				listsMap = dboper.getTasksForCurrentUser(this, listId, List.SORTBY_DATE);
				isSortByPriority = false;
			}
			tasksByDate = listsMap.get("UnCompletedMissedTasks");
			tasksByDate.addAll(listsMap.get("UnCompleted"));
			completedTasks = listsMap.get("AllCompleted");
			
			// Set tasks to be shown depending on sort option
			taskListAdapter = new TaskListArrayAdapter(this, tasksByDate, UNCOMPLETED_TASKS_LIST);		
			completedTaskListAdapter = new TaskListArrayAdapter(this, completedTasks, COMPLETED_TASKS_LIST);

			// Assign adapter to ListView
			listView.setAdapter(taskListAdapter);
			completedListView.setAdapter(completedTaskListAdapter);	
			break;
			
		case LIST_RENAME:
			this.startActivity(new Intent(this, TaskDescription.class));			
			break;
			
		case LIST_DELETE:
			sm = new SessionManager();
			int task = sm.getCurrentTaskDetails(this);
			dboper.deleteTask(this, task);
			// Remove it from list
			if (this.listNumber == UNCOMPLETED_TASKS_LIST) {		
				tasksByDate.remove(currentTaskIndex);
				this.taskListAdapter.notifyDataSetChanged();
			} else if (this.listNumber == COMPLETED_TASKS_LIST) {
				completedTasks.remove(currentTaskIndex);
				this.completedTaskListAdapter.notifyDataSetChanged();	
			}					
			break;
			
		case LIST_COMPLETE:			
			if (listNumber == UNCOMPLETED_TASKS_LIST) {
				checkTask(currentTaskIndex);
			} else if (listNumber == COMPLETED_TASKS_LIST){
				uncheckTask(currentTaskIndex);
			}
			Toast.makeText(this, "Mark as completed", Toast.LENGTH_LONG).show();
			break;
		}
	
		return super.onContextItemSelected(item);
	}
	
	private void createSettingsMenu(ContextMenu menu){
		menu.setHeaderTitle("Settings");
		if (isCompletedHidden) {
			menu.add(0, SETTINGS_SHOW, 0, "Show completed tasks");
		} else {
			menu.add(0, SETTINGS_SHOW, 0, "Hide completed tasks");
		}
		menu.add(0, SETTINGS_DELETE, 0, "Delete completed tasks");
		if (isSortByPriority){
			menu.add(0, SETTINGS_SORT, 0, "Sort by Due Date");
		} else {
			menu.add(0, SETTINGS_SORT, 0, "Sort by Priority");
		}
	}
	
	private void createTasksMenu(ContextMenu menu){
		menu.setHeaderTitle("Update task");
		menu.add(0, LIST_RENAME, 0, "Edit");
		menu.add(0, LIST_DELETE, 0, "Delete");
		menu.add(0, LIST_COMPLETE, 0, "Mark / Unmark");
	}

	public void setCurrentListNumber (int number) {
		this.listNumber = number;
	}
	
	public void setCurrentTaskIndex (int index) {
		this.currentTaskIndex = index;
	}
}


