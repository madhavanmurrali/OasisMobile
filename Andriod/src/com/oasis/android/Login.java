package com.oasis.android;

import com.oasis.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * The login page for the application
 *
 */
public class Login extends Activity implements OnClickListener,
		OnFocusChangeListener {
	/** Called when the activity is first created. */

	// members
	private View loginButton, forgotPasswordButton, newUserButton;
	private View userNameTextBox, passwordTextBox;
	private static int INITMODE = 100;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

		// not required here ...
		DBUtil dbutil = new DBUtil(this);
		dbutil.getWritableDatabase();
		dbutil.close();
		// check login
		loginButton = (Button) findViewById(R.id.loginPageLoginButton);
	//	forgotPasswordButton = (Button) findViewById(R.id.loginPageForgotPasswordButton);
		newUserButton = (Button) findViewById(R.id.loginPageNewUserButton);
		userNameTextBox = (TextView) findViewById(R.id.loginPageUsernameTextBox);
		passwordTextBox = (TextView) findViewById(R.id.loginPagePasswordTextBox);
		((EditText) passwordTextBox).setTag(R.string.Text_Status_Init, "1");
		((EditText) userNameTextBox).setTag(R.string.Text_Status_Init, "1");

		// set the listeners
		loginButton.setOnClickListener((OnClickListener) this);
		//forgotPasswordButton.setOnClickListener((OnClickListener) this);
		newUserButton.setOnClickListener((OnClickListener) this);
		passwordTextBox.setOnFocusChangeListener((OnFocusChangeListener) this);
		userNameTextBox.setOnFocusChangeListener((OnFocusChangeListener) this);
	}

	public void onClick(View view) {
		// validate and allow access
		if (view == this.loginButton) {
			if(!validateUserLogin()) return;
			GTodoUser userObj = new GTodoUser();
			userObj.setUserName(((EditText) findViewById(R.id.loginPageUsernameTextBox))
					.getText().toString());
			userObj.setPassword(((EditText) findViewById(R.id.loginPagePasswordTextBox))
					.getText().toString());
			if (DBOperationsHandler.verifyUserCredentials(this, userObj)) {
				SessionManager sm = new SessionManager();
				sm.setLoginDetails(userObj, this);
			this.startActivity(new Intent(this, UserDevicesList.class));
			} else {
				constructErrorDialog("Invalid credentials ..",
						R.id.loginPageUsernameTextBox).show();
			}
		} else if (view == this.forgotPasswordButton){
			this.startActivity(new Intent(this, ForgotPassword.class));
		} else if (view == this.newUserButton){
			this.startActivity(new Intent(this, NewUser.class));
		}
	}

	// User interface goodies
	public void onFocusChange(View view, boolean hasFocus) {
		if (view == this.passwordTextBox && hasFocus) {
			if (((EditText) this.passwordTextBox)
					.getTag(R.string.Text_Status_Init) == "1") {
				((EditText) this.passwordTextBox).setTag(
						R.string.Text_Status_Init, "0");
				((EditText) this.passwordTextBox).setText("");
			}
		} else if (view == this.userNameTextBox && hasFocus) {
			if (((EditText) this.userNameTextBox)
					.getTag(R.string.Text_Status_Init) == "1") {
				((EditText) this.userNameTextBox).setTag(
						R.string.Text_Status_Init, "0");
				((EditText) this.userNameTextBox).setText("");
			}
		}
	}

	// validations
	private Boolean validateUserLogin() {
		String username = ((EditText) findViewById(R.id.loginPageUsernameTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.loginPageUsernameTextBox))
				.getText().toString() : null;
		String password = ((EditText) findViewById(R.id.loginPagePasswordTextBox))
				.getText() != null ? ((EditText) findViewById(R.id.loginPagePasswordTextBox))
				.getText().toString() : null;
		if (username == null || ("").equals(username.trim())) {
			constructErrorDialog("Enter username",
					R.id.loginPageUsernameTextBox).show();
			return false;
		}
		if (password == null || ("").equals(password)) {
			constructErrorDialog("Password is required ",
					R.id.loginPagePasswordTextBox).show();
			return false;
		}
		return true;
	}

	// common alert dialogs 
	private AlertDialog constructErrorDialog(String msg, final int identifier) {
		AlertDialog.Builder error = new AlertDialog.Builder(this);
		error.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("okay ! ",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								(findViewById(identifier)).requestFocus();
							}
						});
		AlertDialog errorAlert = error.create();
		return errorAlert;
	}
}