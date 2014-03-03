package com.oasis.android;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.oasis.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Lists all the categories for the application
 *
 */
public class UserDevicesList extends Activity implements OnClickListener,OnItemClickListener {
	private View logoutButton, editUserButton, addListButton;
	private TextView userNameLabel;
	private String userNameInSession = "";
    ArrayList<List> listItems ;
	private ListView list;
	private static Logger logger = Logger.getLogger("GTodo");

	// static constants for code
	private static final int LIST_EDIT = 1;
	private static final int LIST_DELETE = 2;
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.list_page);
        
	        SessionManager sm = new SessionManager();
	        sm.getUserLoginDetails(this);
	        //check if the user is logged in 
	        if(sm.getUserLoggedIn()==false){
	        	this.startActivity(new Intent(this, Login.class));
	        }
	        Integer loggedInUserId = sm.getUserId();
	        DBOperationsHandler dboper = new DBOperationsHandler();
	        listItems = dboper.getUserLists(loggedInUserId, this);
	        userNameInSession = dboper.getUserNameById(loggedInUserId, this);
	        logger.info(loggedInUserId+" --- Session .... ");
	        
	        // components and their event listeners
	        logoutButton = (ImageView)findViewById(R.id.listsPageLogout);
	        editUserButton = (ImageView)findViewById(R.id.listsPageEdit);
	        addListButton = (ImageView)findViewById(R.id.listPageAdd);
	        userNameLabel = (TextView)findViewById(R.id.listsPageTitleLabel);
	        
	        userNameLabel.setText(userNameInSession+"'s Home");
	        logoutButton.setOnClickListener((OnClickListener) this); 
	        addListButton.setOnClickListener((OnClickListener) this); 
	        editUserButton.setOnClickListener((OnClickListener) this); 
	        
	        list = (ListView) findViewById(R.id.list);
	        ListArrayAdapter listAdapter = new ListArrayAdapter(this,listItems);
	        list.setAdapter(listAdapter); 
	    //    list.setOnItemClickListener((OnItemClickListener)this);
	    //    this.registerForContextMenu(list);
	 }
	
//	@Override
	public void onClick(View view) {
		if (view == this.logoutButton){
			SessionManager sm = new SessionManager();
			sm.logoutUser(this);
			Intent loginpage = new Intent(this, Login.class);
			loginpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(loginpage);
		} else if(view == this.editUserButton){
			this.startActivity(new Intent(this, EditUser.class));
		} else if(view == this.addListButton){
			addListToListView();
		}
			
	}

//	@Override
	public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		List currentList = listItems.get(position);		
		SessionManager sm = new SessionManager();
		sm.setCurrentListDetails(currentList, this);
		sm.setCurrentTaskID(this,currentList.getId());
		this.startActivity(new Intent(this, Tasks.class));

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Settings");
		menu.add(Menu.NONE, LIST_EDIT, Menu.NONE, "Edit");
		menu.add(Menu.NONE, LIST_DELETE, Menu.NONE, "Delete");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
		case LIST_EDIT:
			editSelectedList(info.id);
			break;
		case LIST_DELETE:
			deleteListFromView(info.id);
			break;
		}
		
		return super.onContextItemSelected(item);
		}
	
	//Delete the selected list from the list view
	private void deleteListFromView(final long rowID){
			 (new AlertDialog.Builder(this))
					.setTitle("Delete list " + listItems.get((int)rowID).getName())
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {						
			//			@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							deleteListData(rowID);
						}
					})
		            .setNegativeButton("Cancel", null)
		            .show();
		
	}
	
	//Delete list data from listItems and database
	private void deleteListData(long rowId){
		int listID = listItems.get((int)rowId).getId();
        DBOperationsHandler dboper = new DBOperationsHandler();
        listItems.remove((int)rowId);
        dboper.deleteList(this, listID);
        ((ListArrayAdapter)list.getAdapter()).notifyDataSetChanged();	
	}
	
	//Add one list by clicking the add button
	private void addListToListView(){
		 final EditText editText = new EditText(this);
		 
		 (new AlertDialog.Builder(this))
			.setTitle("Add new device")
			.setView(editText)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {						
		//		@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					//addNewListData(editText.getText().toString());
				}
			})
         .setNegativeButton("Cancel", null)
         .show();
	}
	
	//Add one list to listItems and database
	private void addNewListData(String name){
        SessionManager sm = new SessionManager();
        sm.getUserLoginDetails(this);
        Integer loggedInUserId = sm.getUserId();
        int listNum = listItems.size();
        int newListID = 1;
        if(listNum > 0){
        	newListID = listItems.get(listNum -1).getId() + 1;
        }
		int numberOfItems = 0;
		int showCompleted = 1;
		
		List list = new List(loggedInUserId, newListID, name, numberOfItems, showCompleted);
		
        DBOperationsHandler dboper = new DBOperationsHandler();
    	listItems.add(list);
        dboper.addList(this, list);
        ((ListArrayAdapter)this.list.getAdapter()).notifyDataSetChanged();			
	}
	
	//Edit one list by context menu
	private void editSelectedList(final long rowId){
		String listName =  listItems.get((int)rowId).getName();
		 final EditText editText = new EditText(this);
		 editText.setText(listName);
		 
		 (new AlertDialog.Builder(this))
			.setTitle("Edit list " + listName)
			.setView(editText)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {						
		//		@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					editListData(rowId, editText.getText().toString());
				}
			})
         .setNegativeButton("Cancel", null)
         .show();
	}
	
	//Edit the selected list's name
	private void editListData(final long rowId, String name){
		int listId = listItems.get((int)rowId).getId();
		
        DBOperationsHandler dboper = new DBOperationsHandler();
        listItems.get((int)rowId).setName(name);
        dboper.updateList(this, listId, name);
        ((ListArrayAdapter)list.getAdapter()).notifyDataSetChanged();				
	}
	
}
