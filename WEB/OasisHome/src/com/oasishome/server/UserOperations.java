package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mortbay.util.ajax.JSON;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class UserOperations extends HttpServlet {
	//private static final Logger log = Logger.getLogger(GetTasks.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.print("testing on oper");
		String action = req.getParameter("action") != null ? req
				.getParameter("action") : null;

		if (action != null && action.equalsIgnoreCase("adduser")) {

			String name = req.getParameter("name");
			String pwd = req.getParameter("pwd");
			String mic = req.getParameter("mic");

			DBUtil dbutil = new DBUtil();
			User userObj = new User();
			userObj.setName(name);
			userObj.setPwd(PwdEncrypt.encrypt(pwd));
			userObj.setMicAddress(mic);
			JSONObject jsobj = new JSONObject();
			if (dbutil.selectUser(name)) {
				try {
					jsobj.put("status", false);
					jsobj.put("errorMessage", "The user mentioned already exist");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				userObj = dbutil.createUser(userObj);

				try {
					HttpSession serv=req.getSession();
					jsobj.put("status", true);
					jsobj.put("userID", userObj.getId());
					serv.setAttribute("loggedinUserName", name);
					serv.setAttribute("loggedinUserId", userObj.getId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			PrintWriter pout = resp.getWriter();
			pout.write(jsobj.toString());
		}
	}
}
