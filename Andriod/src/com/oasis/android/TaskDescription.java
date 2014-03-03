package com.oasis.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import com.oasis.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Add / Edit task for the application
 *
 */
public class TaskDescription extends Activity implements OnClickListener, OnCheckedChangeListener {
	// properties
	private Task task;
	private int userId;
	private int listID;
	private static Logger logger = Logger.getLogger("GTodo");

	// components of the page
	private TextView UsernameTextBox, NotesTextBox;
	private ImageView Back, Logout;
	private Button Cancel;
	CheckBox noDueDateCheckbox;
	private DatePicker DueDatePicker, AlertDatePicker;
	private TimePicker DueTimePicker, AlertTimePicker;
	private RadioButton noneBtn, lowBtn, highBtn;
	private Spinner belongedList;
	private ArrayList<Integer> belongedListIDList;

	// general date related vars
	private int year;
	private int month;
	private int day;
	private SessionManager sm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sm = new SessionManager();
		sm.getUserLoginDetails(this);
		sm.getCurrentListDetails(this);
		listID = sm.getCurrentListID();
		int taskId = sm.getCurrentTaskDetails(this);
		userId = sm.getUserId();

		logger.info(listID + " " + taskId + " " + userId);
		if (taskId == -1) {
			this.task = new Task();
		} else {
			DBOperationsHandler dboper = new DBOperationsHandler();
			this.task = dboper.getTaskById(this, taskId);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_description_page);

		// get all the components
		Logout = (ImageView) findViewById(R.id.taskDescriptionPageLogout);
		Back = (ImageView) findViewById(R.id.taskDescriptionPageBack);
		Cancel = (Button)findViewById(R.id.taskDescriptionPageCancelButton);
		UsernameTextBox = (TextView) findViewById(R.id.taskDescriptionPageUsernameTextBox);
		NotesTextBox = (TextView) findViewById(R.id.taskDescriptionPageNotesTextBox);
		noDueDateCheckbox = (CheckBox) findViewById(R.id.noDueDateCheckbox);

		DueDatePicker = (DatePicker) findViewById(R.id.taskDescriptionPageDueDatePicker);
		DueTimePicker = (TimePicker) findViewById(R.id.taskDescriptionPageDueTimePicker);
		AlertDatePicker = (DatePicker) findViewById(R.id.taskDescriptionPageAlertDatePicker);
		AlertTimePicker = (TimePicker) findViewById(R.id.taskDescriptionPageAlertTimePicker);

		noneBtn = (RadioButton) findViewById(R.id.taskDescriptionPagePriorityNone);
		lowBtn = (RadioButton) findViewById(R.id.taskDescriptionPagePriorityLow);
		highBtn = (RadioButton) findViewById(R.id.taskDescriptionPagePriorityHigh);

		belongedList = (Spinner) findViewById(R.id.taskDescriptionPageLists);
		// init the calendar to contain todays date + 1hr as the due date
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		DueDatePicker.init(year, month, day, null);
		DueTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY) + 1);
		DueTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
		
		// set the event listeners
		Logout.setOnClickListener((OnClickListener) this);
		Back.setOnClickListener((OnClickListener) this);
		Cancel.setOnClickListener((OnClickListener)this);
		noDueDateCheckbox.setOnCheckedChangeListener((OnCheckedChangeListener) this);

		// update the display
		this.displayTask();
	}

	//@Override
	public void onClick(View view) {
		SessionManager sm = new SessionManager();
		if (view == this.Back) {
			if (validateTaskDescData()) {
				this.updateList();
				sm.backToTasks(this);
				this.startActivity(new Intent(this, Tasks.class));
			}
		} else if (view == this.Logout) {
			sm.logoutUser(this);
			Intent loginpage = new Intent(this, Login.class);
			loginpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(loginpage);
		}else if(view == this.Cancel){
			this.startActivity(new Intent(this, Tasks.class));
		}

	}

	//@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			//If checkbox is checked for "no due date", disable DueDatePicker
			DueDatePicker.setEnabled(false);
			DueTimePicker.setEnabled(false);
		} else {
			//If checkbox isn't checked for "no due date", enable DueDatePicker
			DueDatePicker.setEnabled(true);
			DueTimePicker.setEnabled(true);
		}
	}
	
	//Validate the fields for a tasks are valid
	private boolean validateTaskDescData() {
		if (UsernameTextBox.getText().toString().trim().equals("")) {
			// Check task name should not be empty
			(new AlertDialog.Builder(this)).setTitle("Error message ")
					.setMessage("Task description should not be empty!!!")
					.setPositiveButton("OK", null).show();
			UsernameTextBox.requestFocus();
			return false;
		} else {
			// Check due date must be later than current time(today)
			if(noDueDateCheckbox.isChecked()){
				return true;
			}else{
				Date dueDate;
				Calendar cld = Calendar.getInstance();
				cld.set(DueDatePicker.getYear(), DueDatePicker.getMonth(),
						DueDatePicker.getDayOfMonth(),
						DueTimePicker.getCurrentHour(),
						DueTimePicker.getCurrentMinute());
				dueDate = cld.getTime();

				Date currentDate = new Date();
				if (dueDate.getTime() <= currentDate.getTime()) {
					(new AlertDialog.Builder(this))
							.setTitle("Error message ")
							.setMessage(
									"Due date should not be ealier than current time today!!!")
							.setPositiveButton("OK", null).show();
					DueDatePicker.requestFocus();
					return false;
				}				
			}
		}

		return true;
	}

	private void displayTask() {
		belongedListIDList = new ArrayList<Integer>();
		int selectedListIndex = 0;
		int currentListId = sm.getCurrentListID();

		// get the task details from the db
		DBOperationsHandler dboper = new DBOperationsHandler();
		ArrayList<List> lists = dboper.getUserLists(userId, this);
		if (lists.size() > 0) {
			String listNames[] = new String[lists.size()];
			for (int i = 0; i < lists.size(); i++) {
				int listId = lists.get(i).getId();
				belongedListIDList.add(listId);
				listNames[i] = lists.get(i).getName();
				if(listId == currentListId){
					selectedListIndex = i;
				}
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, listNames);
			belongedList.setAdapter(adapter);
			belongedList.setSelection(selectedListIndex);
		}

		if (this.task.getTaskId() != -1) {
			// Display task's property to each widget
			// Name
			UsernameTextBox.setText(this.task.getName());

			// Due date
			Calendar cld = Calendar.getInstance();
			long dueDate = this.task.getDueDate();
			if(dueDate < 0){
				//No due date
				noDueDateCheckbox.setChecked(true);
				DueDatePicker.setEnabled(false);
				DueTimePicker.setEnabled(false);
			}else{
				//Set due date in DatePicker and TimePicker
				noDueDateCheckbox.setChecked(false);
				try {
					cld.setTimeInMillis(dueDate);

					DueDatePicker.init(cld.get(Calendar.YEAR),
							cld.get(Calendar.MONTH),
							cld.get(Calendar.DAY_OF_MONTH), null);
					DueTimePicker.setCurrentHour(cld.get(Calendar.HOUR_OF_DAY));
					DueTimePicker.setCurrentMinute(cld.get(Calendar.MINUTE));

				} catch (IllegalArgumentException exp) {
					cld = Calendar.getInstance();
					DueDatePicker.init(cld.get(Calendar.YEAR),
							cld.get(Calendar.MONTH),
							cld.get(Calendar.DAY_OF_MONTH), null);
					DueTimePicker.setCurrentHour(cld.get(Calendar.HOUR_OF_DAY));
					DueTimePicker.setCurrentMinute(cld.get(Calendar.MINUTE));
					logger.info("invalid date !!");
				}
			}
			
			// Priority
			if (this.task.getPriority() == Task.PRIORITY_LOW) {
				lowBtn.setChecked(true);
			} else if (this.task.getPriority() == Task.PRIORITY_NORMAL) {
				noneBtn.setChecked(true);
			} else if (this.task.getPriority() == Task.PRIORITY_HIGH) {
				highBtn.setChecked(true);
			}

			// Notes
			NotesTextBox.setText(this.task.getNotes());

			// Alert
			Calendar cld2 = Calendar.getInstance();
			try {
				cld2.setTimeInMillis(this.task.getAlert());
				AlertDatePicker.init(cld2.get(Calendar.YEAR),
						cld2.get(Calendar.MONTH),
						cld2.get(Calendar.DAY_OF_MONTH), null);
				AlertTimePicker.setCurrentHour(cld2.get(Calendar.HOUR_OF_DAY));
				AlertTimePicker.setCurrentMinute(cld2.get(Calendar.MINUTE));

			} catch (IllegalArgumentException exp) {
				cld2 = Calendar.getInstance();
				AlertDatePicker.init(cld2.get(Calendar.YEAR),
						cld2.get(Calendar.MONTH),
						cld2.get(Calendar.DAY_OF_MONTH), null);
				AlertTimePicker.setCurrentHour(cld2.get(Calendar.HOUR_OF_DAY));
				AlertTimePicker.setCurrentMinute(cld2.get(Calendar.MINUTE));
				logger.info("invalid alert date !!");
			}
		}
		
		// List
		belongedList.setSelection(selectedListIndex);
	}

	// Construct a new task for updated contents
	private void updateList() {
		this.task.setName(UsernameTextBox.getText().toString());

		Calendar cld = Calendar.getInstance();
		if(noDueDateCheckbox.isChecked()){
			//No due date, let duedate=-1 in task object
			this.task.setDueDate(-1);
		}else{
			Date dueDate;
			cld.set(DueDatePicker.getYear(), DueDatePicker.getMonth(),
					DueDatePicker.getDayOfMonth(), DueTimePicker.getCurrentHour(),
					DueTimePicker.getCurrentMinute());
			dueDate = cld.getTime();
			this.task.setDueDate(dueDate.getTime());		
		}

		Date alertDate = Calendar.getInstance().getTime();
		cld.set(AlertDatePicker.getYear(), AlertDatePicker.getMonth(),
				AlertDatePicker.getDayOfMonth(),
				AlertTimePicker.getCurrentHour(),
				AlertTimePicker.getCurrentMinute());
		alertDate = cld.getTime();
		this.task.setAlert(alertDate.getTime());

		this.task.setNotes(NotesTextBox.getText().toString());

		if (noneBtn.isChecked()) {
			this.task.setPriority(Task.PRIORITY_NORMAL);
		} else if (lowBtn.isChecked()) {
			this.task.setPriority(Task.PRIORITY_LOW);
		} else if (highBtn.isChecked()) {
			this.task.setPriority(Task.PRIORITY_HIGH);
		}

		DBOperationsHandler dboper = new DBOperationsHandler();

		// Update belonged list id if change the belonged list name
		int selectedIndex = (int)belongedList.getSelectedItemId();
		int newListId = belongedListIDList.get(selectedIndex);		
		String newListName = belongedList.getSelectedItem().toString();

		this.task.setListId(newListId);
		this.task.setListName(newListName);

		if (this.task.getTaskId() == -1) {
			this.task.setTaskId(dboper.addTaskForUser(this, this.task,
					newListId));
		} else {
			dboper.editTaskForUser(this, this.task, newListId);
		}
	}

}
