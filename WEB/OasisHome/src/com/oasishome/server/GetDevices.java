package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetDevices extends HttpServlet {
	//private static final Logger log = Logger.getLogger(GetTasks.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
	//	HttpSession serv = req.getSession();
		PrintWriter pout = resp.getWriter();

//		Long userid = (Long) serv.getAttribute("loggedinUserId");
		Gson jsonbjG = new Gson();
		com.google.appengine.labs.repackaged.org.json.JSONObject jsonbj = new com.google.appengine.labs.repackaged.org.json.JSONObject();
		//String micAddress = (String) serv.getAttribute("micAddress");
		String name = req.getParameter("uname");
		String pwd = req.getParameter("pwd");
		DBUtil dbutil = new DBUtil();
		Long userid = null;
		if(name!=null){			
			userid = dbutil.validateUserLogin(name, PwdEncrypt.encrypt(pwd));		
			System.out.print(userid);
		}
		System.out.print(userid);
		if(userid == null){
			try {
				jsonbj.put("status", false);
				jsonbj.put("errorMessage", "username-password not valid :( ");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pout.print(jsonbj.toString());
			return;
		}
		String micAddress = "";// (String) serv.getAttribute("micAddress");
		ArrayList<Device> devices = new ArrayList<Device>();
		System.out.println("****************SUCCESS"+userid);
		HashMap<String, ArrayList<Device>> devicesListings= new HashMap<String, ArrayList<Device>>();
		try {
		devicesListings= dbutil.getDevicesListing(userid,micAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(devicesListings.size() > 0) {
			ArrayList<Device> activeList = (ArrayList<Device>) devicesListings.get("ActiveList");
			ArrayList<Device> inactiveList = (ArrayList<Device>)devicesListings.get("InActiveList");
			req.setAttribute("ActiveList", activeList);
			req.setAttribute("InActiveList", inactiveList);
			System.out.print(activeList);
			
			try {
				jsonbj.put("ActiveList", jsonbjG.toJson(activeList));
				jsonbj.put("InActiveList", jsonbjG.toJson(inactiveList));	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
		pout.write(jsonbj.toString());
		//req.setAttribute("tasks", tasksInTheList);
		/*RequestDispatcher rd = req.getRequestDispatcher("jsps/getTasks.jsp");
		resp.setHeader("Cache-Control", "no-cahce");
		resp.setHeader("Expires", "0");
		rd.forward(req, resp);*/
	}
}
