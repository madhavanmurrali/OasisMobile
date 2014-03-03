/*Check the DB

//for deleting an account
	public void deleteUser(Context context, int userID)
	{
		ArrayList<List> lists2 =  new ArrayList<List>();
		lists2 = getUserLists( userID,context);
		
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		String[] str = {String.valueOf(userID)};
		db.delete(DBUtil.TABLE_USER, DBUtil.USER_USERID + "=?", str);
		for(int i=0; i< lists2.size(); i++)
		{
			deleteList(context,lists2.get(i).getId());
		}
		db.close();
	}

// Editing for EditUser page
	public void editAccountForUser (Context context, GTodoUser user) 
	{
		ContentValues values = new ContentValues();
		values.put(DBUtil.USER_USERNAME, user.getUserName());
		values.put(DBUtil.USER_PWD, user.getPassword());
		values.put(DBUtil.USER_SECQTN, user.getSecurityQuestion());
		values.put(DBUtil.USER_SECANS, user.getSecurityAnswer());
		DBUtil dbUtil = new DBUtil(context);
		SQLiteDatabase db = dbUtil.getWritableDatabase();
		db.update(DBUtil.TABLE_USER, values, DBUtil.USER_USERID + "=?",
				new String[] { String.valueOf(user.getUserId()) });
		db.close();
	}


*/

package com.oasis.android;

import com.oasis.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Edit user page 
 *
 */
public class EditUser extends Activity implements OnClickListener
{
	private View cancelButton, doneButton, delButton;
	private TextView curruser;	
	private int userId;
	private GTodoUser username = new GTodoUser();
	private Spinner secQ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.edit_account_info_page);
	    
	    //gets the present Logged in userId and from it the user details
	    SessionManager sm = new SessionManager();
		sm.getUserLoginDetails(this);
		userId = sm.getUserId();
		DBOperationsHandler dboper = new DBOperationsHandler();
		username = dboper.getUserDetails(userId, this);

		// set the corresponding handlers
		cancelButton = (Button)findViewById(R.id.editAccountCancelButton);
        doneButton = (Button)findViewById(R.id.editAccountDoneButton);
        delButton = (Button)findViewById(R.id.editAccountDeleteButton);
        
        curruser = (EditText)findViewById(R.id.editAccountPageUsernameTextBox);
        secQ = (Spinner)findViewById(R.id.editAccountPageSecurityQuestions);
        this.display();
        
        delButton.setOnClickListener((OnClickListener) this); 
        cancelButton.setOnClickListener((OnClickListener) this); 
        doneButton.setOnClickListener((OnClickListener) this);
	}

	//@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == this.delButton)
		{
			AlertDialog.Builder error = new AlertDialog.Builder(this);
			
		// delete my account	
			  error.setMessage("Are you sure you want to delete this Account?").setCancelable(true)
			  		.setPositiveButton("Yes do it!", new DialogInterface.OnClickListener() 
  					{
						public void onClick(DialogInterface dialog,int which) {
							DBOperationsHandler dboper1 = new DBOperationsHandler();
							  dboper1.deleteUser(EditUser.this, userId);
			
							  AlertDialog.Builder error1 = new AlertDialog.Builder(EditUser.this);
							  error1.setMessage("Account Deleted Succesfully").setCancelable(false).setPositiveButton("okay! ",new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,int which) {
														Intent i=new Intent(EditUser.this,Login.class);
														startActivity(i);
													}
												});
							  AlertDialog errorAlert1 = error1.create();
							  errorAlert1.show();
					}
					})
			  		.setNegativeButton("Cancel ",new DialogInterface.OnClickListener() 
			  					{
									public void onClick(DialogInterface dialog,int which) {
										;
									}
								});
			  AlertDialog errorAlert = error.create();
			  errorAlert.show();
	
		}
		
		else if (view == this.cancelButton)
		{
			this.startActivity(new Intent(this, UserDevicesList.class));
		}
		
		else if (view == this.doneButton)
		{
			this.validate();
		}
	}
	
	// init the select box for security questions.
	private void display()
	{
		String b[] = { "Name of your first School ?",
				"Name of your first Bike ?", "What is your favorite sport ?",
				"What is your lucky number ?" };		
		curruser.setText(username.getUserName());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, b);
		secQ.setAdapter(adapter);
	}

	// validate the details and submit for edit account.
	private void validate()
	{
		String curruser1 = ((EditText) findViewById(R.id.editAccountPageUsernameTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.editAccountPageUsernameTextBox))
				.getText().toString() : null;
				
		String oldpass = ((EditText) findViewById(R.id.editAccountPageOldPasswordTextBox))
						.getText() != null ? ((EditText) findViewById(R.id.editAccountPageOldPasswordTextBox))
						.getText().toString() : null;
						
		String newpass = ((EditText) findViewById(R.id.editAccountPagePasswordTextBox))
						.getText() != null ? ((EditText) findViewById(R.id.editAccountPagePasswordTextBox))
						.getText().toString() : null;

		String confpass = ((EditText) findViewById(R.id.editAccountPageConfirmPasswordTextBox))
						.getText() != null ? ((EditText) findViewById(R.id.editAccountPageConfirmPasswordTextBox))
						.getText().toString() : null;
						
		String secA = ((EditText) findViewById(R.id.editAccountPageSecurityAnswerTextBox))
						.getText() != null ? ((EditText) findViewById(R.id.editAccountPageSecurityAnswerTextBox))
						.getText().toString() : null;
		
		
		if (curruser1 == null || ("").equals(curruser1.trim())) 
		{
			constructErrorDialog("User Name can't be Empty",R.id.editAccountPageUsernameTextBox).show();
		}	
		
		else if (!username.getPassword().equals(oldpass)) 
		{
			constructErrorDialog("Enter the Exact old Password",R.id.editAccountPageOldPasswordTextBox).show();
		}
		
		else if (newpass == null || ("").equals(newpass.trim())) 
		{
			constructErrorDialog("Enter a New Password ",R.id.editAccountPagePasswordTextBox).show();
		}
		
		else if (newpass.trim().length()<4) 
		{
			constructErrorDialog("Password must be greater than 4 characters ",R.id.editAccountPagePasswordTextBox).show();
		}
		
		else if (confpass == null || ("").equals(confpass.trim())) 
		{
			constructErrorDialog("Confirm your New password",R.id.editAccountPageConfirmPasswordTextBox).show();
		}
		
		else if (!newpass.equals(confpass)) 
		{
			constructErrorDialog("New Passwords don't match. Re-confirm the password",R.id.editAccountPageConfirmPasswordTextBox).show();
		}
		
		else if (secA == null || ("").equals(secA.trim())) 
		{
			constructErrorDialog("Security Answer Field Empty",R.id.editAccountPageSecurityAnswerTextBox).show();
		}
		
		else 
		{
		  
		  username.setUserId(userId);
		  username.setUserName(curruser1);
		  username.setPassword(newpass);
		  username.setSecurityQuestion(secQ.getSelectedItem().toString());
		  username.setSecurityAnswer(secA);
		  
		  // invoke the db api to perform the update user info.
		  DBOperationsHandler dboper = new DBOperationsHandler();
		  dboper.editAccountForUser(this, username);
		  
		  AlertDialog.Builder error = new AlertDialog.Builder(this);
		  error.setMessage("Account Changed Succesfully").setCancelable(false).setPositiveButton("okay ! ",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									Intent i=new Intent(EditUser.this,UserDevicesList.class);
									startActivity(i);
								}
							});
		  AlertDialog errorAlert = error.create();
		  errorAlert.show();
		}
		
	}

	// general method for contructing error dialogs
	private AlertDialog constructErrorDialog(String msg, final int identifier) 
	{
		AlertDialog.Builder error = new AlertDialog.Builder(this);
		error.setMessage(msg).setCancelable(false).setPositiveButton("okay ! ",	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								;
							}
						});
		AlertDialog errorAlert = error.create();
		return errorAlert;
	}

}
