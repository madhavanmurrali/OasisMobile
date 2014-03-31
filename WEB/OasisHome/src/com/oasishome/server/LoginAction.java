package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jws.soap.SOAPBinding.Use;
import javax.mail.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.catalina.startup.HomesUserDatabase;
import org.json.simple.JSONArray;

import com.google.appengine.api.search.query.ExpressionParser.negation_return;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class LoginAction extends HttpServlet {
	// private static final Logger log =
	// Logger.getLogger(LoginAction.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv = req.getSession();
		PrintWriter pout = resp.getWriter();
		JSONObject jsobj = new JSONObject();

		if (serv.getAttribute("loggedinUserId") != null) {
			try {
				jsobj.put("status", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			pout.print(jsobj.toString());
			return;
		}

		String name = req.getParameter("name");
		String pwd = req.getParameter("pwd");

		if (name != null) {
			DBUtil dbutil = new DBUtil();
			Long id = dbutil.validateUserLogin(name, PwdEncrypt.encrypt(pwd));

			if (id != null) {
				serv.setAttribute("loggedinUserName", name);
				serv.setAttribute("loggedinUserId", id);
				com.oasishome.server.User user = dbutil.getUserById(id);
				// do the syncing here..
				// based on the Home - Hit Apis and get the devices update DB
				// accordingly ..
				String devicesList = SyncService.fetchDevicesApiHome(user
						.getMicAddress());
				if (devicesList != null) {
					org.json.JSONArray json;
					try {
						json = new org.json.JSONArray(devicesList);
						SyncService.checkDevices(json, user.getMicAddress(),id);
					} catch (org.json.JSONException e1) {
						e1.printStackTrace();
					}
				}
				try {
					jsobj.put("status", true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				try {
					jsobj.put("status", false);
					jsobj.put("errorMessage", "username-password not valid :( ");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pout.print(jsobj.toString());

		}
	}
}
