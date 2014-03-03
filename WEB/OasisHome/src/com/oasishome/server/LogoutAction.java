package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class LogoutAction extends HttpServlet {
	//private static final Logger log = Logger.getLogger(LoginAction.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv=req.getSession();
		serv.invalidate();
		
	}
}
