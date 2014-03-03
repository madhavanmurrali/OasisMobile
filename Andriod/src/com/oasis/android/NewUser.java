package com.oasis.android;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oasis.android.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * New user registration page
 *
 */
public class NewUser extends Activity implements OnClickListener {

	private View cancelButton, doneButton,username;
	private Logger logger = Logger.getLogger("users");
	private String securityQs[] = { "Name of your first School ?",
			"Name of your first Bike ?", "What is your favorite sport ?",
			"What is your lucky number ?" };

	//@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_account_page);

		Spinner secComp = (Spinner) findViewById(R.id.accountPageSecurityQuestions);
		ArrayAdapter<String> secAdp = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, securityQs);
		secComp.setAdapter(secAdp);
		cancelButton = (Button) findViewById(R.id.accountPageCancelButton);
		doneButton = (Button) findViewById(R.id.accountPageDoneButton);
		username = (EditText)findViewById(R.id.accountPageUsernameTextBox);
		username.requestFocus();

		// set the event listeners
		cancelButton.setOnClickListener((OnClickListener) this);
		doneButton.setOnClickListener((OnClickListener) this);
	}

	//@Override
	public void onClick(View view) {
		// perform the specific actions
		if (view == this.cancelButton) {
			this.startActivity(new Intent(this, Login.class));
		} else if (view == this.doneButton) {
			GTodoUser userObj = new GTodoUser();
			userObj.setUserName(((EditText) findViewById(R.id.accountPageUsernameTextBox))
					.getText().toString());
			userObj.setPassword(((EditText) findViewById(R.id.accountPagePasswordTextBox))
					.getText().toString());
			userObj.setSecurityAnswer(((EditText) findViewById(R.id.accountPageSecurityAnswerTextBox))
					.getText().toString());
			userObj.setSecurityQuestion(((Spinner) findViewById(R.id.accountPageSecurityQuestions))
					.getSelectedItem().toString());

			// validate the details
			if (!validateUserRegistration())
				return;
			// call the db api for new user registration
			DBOperationsHandler dboper = new DBOperationsHandler();
			HashMap status = dboper.addNewUser(this, userObj);
			if((Boolean)status.get("error")){
				constructErrorDialog((String)status.get("errorMessage"),R.id.accountPageUsernameTextBox).show();
				return;
			}
			//set the session details
			SessionManager sm = new SessionManager();
			sm.setLoginDetails(userObj, this);
			this.startActivity(new Intent(this, UserDevicesList.class));
		}
	}

	// validate the user inputs
	private Boolean validateUserRegistration() {
		String username = ((EditText) findViewById(R.id.accountPageUsernameTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.accountPageUsernameTextBox))
				.getText().toString() : null;
		String password = ((EditText) findViewById(R.id.accountPagePasswordTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.accountPagePasswordTextBox))
				.getText().toString() : null;
		String currpassword = ((EditText) findViewById(R.id.accountPageConfirmPasswordTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.accountPageConfirmPasswordTextBox))
				.getText().toString() : null;
		String secans = ((EditText) findViewById(R.id.accountPageSecurityAnswerTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.accountPageSecurityAnswerTextBox))
				.getText().toString() : null;
		String secqs = ((Spinner) findViewById(R.id.accountPageSecurityQuestions))
				.getSelectedItem() != null ? ((Spinner) findViewById(R.id.accountPageSecurityQuestions))
				.getSelectedItem().toString() : null;
		Boolean status = true;
		if (username == null || ("").equals(username.trim())) {
			constructErrorDialog("Choose an username",R.id.accountPageUsernameTextBox).show();
			return false;
		}
		if (password == null || ("").equals(password.trim())) {
			constructErrorDialog("Password is required ",R.id.accountPagePasswordTextBox).show();
			return false;
		}
		if (password.trim().length()<4) {
			constructErrorDialog("Password must be greater than 4 characters ",R.id.accountPagePasswordTextBox).show();
			return false;
		}
		if (currpassword == null || ("").equals(currpassword.trim())) {
			constructErrorDialog("Confirm your password",R.id.accountPageConfirmPasswordTextBox).show();
			return false;
		}
		if (!password.equals(currpassword)) {
			constructErrorDialog("Passwords donot match",R.id.accountPageConfirmPasswordTextBox).show();
			return false;
		}
		logger.info(secans+" - --- ---- -- --"+secqs);
		if (secqs == null || ("").equals(secqs.trim())) {
			constructErrorDialog("Choose a security question",R.id.accountPageSecurityQuestions).show();
			return false;
		}
		if (secans == null || ("").equals(secans.trim())) {
			constructErrorDialog("Enter a security answer",R.id.accountPageSecurityAnswerTextBox).show();
			return false;
		}
		return status;
	}

	// common construct for error dialogs
	private AlertDialog constructErrorDialog(String msg,final int identifier) {
		AlertDialog.Builder error = new AlertDialog.Builder(this);
		error.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("okay ! ",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								(findViewById(identifier))
										.requestFocus();
							}
						});
		AlertDialog errorAlert = error.create();
		return errorAlert;
	}
}
