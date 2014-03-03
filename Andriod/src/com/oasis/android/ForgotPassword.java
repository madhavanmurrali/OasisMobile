package com.oasis.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.oasis.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Forgot password page for the user
 *
 */
public class ForgotPassword extends Activity implements OnClickListener,
		OnItemSelectedListener {

	// members
	private View cancelButton, doneButton;
	private TextView secQ;
	private Spinner user;
	private int numusers, f;
	private ArrayList<GTodoUser> lists;
	private GTodoUser userobj;
	private static Logger logger = Logger.getLogger("GTodo");

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_page);

		cancelButton = (Button) findViewById(R.id.forgotPasswordCancelButton);
		doneButton = (Button) findViewById(R.id.forgotPasswordDoneButton);
		user = (Spinner) findViewById(R.id.forgotPasswordPageUsernameTextBox);
		secQ = (TextView) findViewById(R.id.forgotPasswordPageSecurityQuestions);

		// init the display
		this.display();

		// set the event listeners
		cancelButton.setOnClickListener((OnClickListener) this);
		doneButton.setOnClickListener((OnClickListener) this);
		user.setOnItemSelectedListener((OnItemSelectedListener) this);
		user.requestFocus();
	}

	// @Override
	// update the security question on user name change
	public void onItemSelected(AdapterView<?> parentItem, View vw, int pos,
			long identifier) {
			String selectedUsername = (parentItem.getItemAtPosition(pos)).toString();
			logger.info("testing"+selectedUsername);
			for (int i = 0; i < numusers; i++) {
				if (selectedUsername .equalsIgnoreCase(lists.get(i).getUserName())) {
					userobj = lists.get(i);
				}
			}
			secQ.setText(userobj.getSecurityQuestion());
	}

	// @Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		return;
	}

	// @Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view == this.cancelButton) {
			this.startActivity(new Intent(this, Login.class));
		} else if (view == this.doneButton) {

			String newListName = user.getSelectedItem().toString();
			for (int i = 0; i < numusers; i++) {
				if (newListName == lists.get(i).getUserName()) {
					userobj = lists.get(i);
					f = i;
				}
			}

			this.validate();
		}
	}

	
	// validate the params and reset the password
	private void validate() {
		String secA = ((EditText) findViewById(R.id.forgotPasswordPageSecurityAnswerTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.forgotPasswordPageSecurityAnswerTextBox))
				.getText().toString() : null;

		String newpass = ((EditText) findViewById(R.id.forgotPasswordPagePasswordTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.forgotPasswordPagePasswordTextBox))
				.getText().toString() : null;

		String newpass1 = ((EditText) findViewById(R.id.forgotPasswordPageConfirmPasswordTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.forgotPasswordPageConfirmPasswordTextBox))
				.getText().toString() : null;

		if (secA == null || ("").equals(secA.trim())) {
			constructErrorDialog("Specify a security answer",
					R.id.forgotPasswordPageSecurityAnswerTextBox).show();
		}

		else if (!userobj.getSecurityAnswer().equals(secA)) {
			constructErrorDialog("Security Answer not correct",
					R.id.forgotPasswordPageSecurityAnswerTextBox).show();
		}

		else if (newpass == null || ("").equals(newpass.trim())) {
			constructErrorDialog("Password is required ",
					R.id.forgotPasswordPagePasswordTextBox).show();
		} else if (newpass.trim().length() < 4) {
			constructErrorDialog("Password must be greater than 4 characters ",
					R.id.forgotPasswordPagePasswordTextBox).show();
		}

		else if (newpass1 == null || ("").equals(newpass1.trim())) {
			constructErrorDialog("Confirm your password",
					R.id.forgotPasswordPageConfirmPasswordTextBox).show();
		}

		else if (!newpass.equals(newpass1)) {
			constructErrorDialog(
					"Passwords don't match. Re-confirm the password",
					R.id.forgotPasswordPageConfirmPasswordTextBox).show();
		}

		else {
			userobj.setPassword(newpass);
			lists.get(f).setPassword(newpass);
			
			// invoke the db api for the updation
			DBOperationsHandler dboper = new DBOperationsHandler();
			dboper.editPasswordForUser(this, userobj, newpass);

			AlertDialog.Builder error = new AlertDialog.Builder(this);
			error.setMessage("Password Changed Succesfully")
					.setCancelable(false)
					.setPositiveButton("okay ! ",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = new Intent(ForgotPassword.this,
											Login.class);
									startActivity(i);
								}
							});
			AlertDialog errorAlert = error.create();
			errorAlert.show();
		}

	}

	// init the display with users from db
	private void display() {
		DBOperationsHandler dboper = new DBOperationsHandler();
		lists = dboper.getUsers(this);
		numusers = lists.size();

		if (lists.size() > 0) {
			String usernames[] = new String[lists.size()];
			for (int i = 0; i < lists.size(); i++) {
				usernames[i] = lists.get(i).getUserName();
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, usernames);
			user.setAdapter(adapter);
		}
	}

	// error dialog for common usage
	private AlertDialog constructErrorDialog(String msg, final int identifier) {
		AlertDialog.Builder error = new AlertDialog.Builder(this);
		error.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("okay ! ",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								;
							}
						});
		AlertDialog errorAlert = error.create();
		return errorAlert;
	}

}
