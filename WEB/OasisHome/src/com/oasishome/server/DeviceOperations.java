package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mortbay.util.ajax.JSON;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class DeviceOperations extends HttpServlet {
	private static final Logger log = Logger.getLogger(GetDevices.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv = req.getSession();
		JSONObject jsobj = new JSONObject();
		PrintWriter pout = resp.getWriter();

		// String username = (String) serv.getAttribute("loggedinUserName");
		// Long userid = (Long) serv.getAttribute("loggedinUserId");

		String name = req.getParameter("uname");
		String pwd = req.getParameter("pwd");
		DBUtil dbutil = new DBUtil();
		Long userid = null;
		if (name != null) {
			userid = dbutil.validateUserLogin(name, PwdEncrypt.encrypt(pwd));
			if (userid == null) {
				try {
					jsobj.put("status", false);
					jsobj.put("errorMessage", "username-password not valid :( ");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pout.print(jsobj.toString());
				return;
			}
		}

		String action = req.getParameter("action") != null ? req
				.getParameter("action") : null;
		System.out.print("test");

		if (action != null
				&& (action.equalsIgnoreCase("addDevice") || action
						.equalsIgnoreCase("editDevice"))) {

			String devicename = URLDecoder.decode(req
					.getParameter("devicename"));
			Long newedittaskid = null;
			boolean editmode = false;
			
			if (action.equalsIgnoreCase("editDevice")) {
				editmode = true;
				newedittaskid = req.getParameter("id") != null ? Long
						.parseLong(req.getParameter("id")) : null;
				if (newedittaskid != null) {
					// ins = dbutil.getItemByItemId(newedittaskid);
					// taskchecked = ins.isChecked();
				}
			}

			Device deviceObj = new Device();
			deviceObj.setName(devicename);
			deviceObj.setUserId(userid);

			if (action.equalsIgnoreCase("editDevice")) {
				/*
				 * if(newedittaskid!=null){ ins =
				 * dbutil.getItemByItemId(newedittaskid);
				 * taskObj.setId(newedittaskid);
				 * taskObj.setChecked(ins.isChecked()); if(ins.getUserId()!=null
				 * && ins.getUserId().equals(userid)){
				 * dbutil.updateItem(taskObj); } try { jsobj.put("status",
				 * true); jsobj.put("taskId", newedittaskid); } catch
				 * (JSONException e) { e.printStackTrace(); } } PrintWriter pout
				 * = resp.getWriter(); pout.write(jsobj.toString());
				 */
			} else {

				String nickname = URLDecoder.decode(req
						.getParameter("nick_name"));
				deviceObj.setNickname(nickname);
				Long prior = req.getParameter("priority") != null ? Long
						.parseLong(req.getParameter("priority")) : 0;
				deviceObj.setPriority(prior);
				String micAddr = URLDecoder.decode(req
						.getParameter("micro_address"));
				deviceObj.setMicAddr(micAddr);
				Boolean status = req.getParameter("status") != null ? Boolean
						.parseBoolean(req.getParameter("status")) : false;
				deviceObj.setStatus(status);

				Boolean active = req.getParameter("active") != null ? Boolean
						.parseBoolean(req.getParameter("active")) : false;
				deviceObj.setActive(active);

				Long newdeviceid = dbutil.createItem(deviceObj);
				deviceObj.setId(newdeviceid);
				System.out.print("CREADED" + userid);
				
				dbutil.createStatEntry(deviceObj);
				try {
					jsobj.put("status", true);
					jsobj.put("deviceId", newdeviceid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pout.write(jsobj.toString());
			}
		} else if (action != null && action.equalsIgnoreCase("updateDevice")) {
			//called by the micro controller alone ..
			
			
			Device deviceObj = new Device();
			String micAddr = URLDecoder.decode(req
					.getParameter("micro_address"));
			deviceObj.setMicAddr(micAddr);
			Boolean status = req.getParameter("status") != null ? Boolean
					.parseBoolean(req.getParameter("status")) : false;
			deviceObj.setStatus(status);
			String devicename = URLDecoder.decode(req
					.getParameter("devicename"));
			deviceObj.setName(devicename);
			
			Long id = dbutil.getDeviceByMICANDNAME(micAddr, devicename);
			
			if(id==null){
				deviceObj.setNickname(devicename);				
				deviceObj.setActive(false);
				deviceObj.setStatus(status);
				Long newdeviceid = dbutil.createItem(deviceObj);
				System.out.print("CREADED" + userid);
				deviceObj.setId(newdeviceid);
				dbutil.createStatEntry(deviceObj);
				try {
					jsobj.put("status", true);
					jsobj.put("deviceId", newdeviceid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pout.write(jsobj.toString());
			}else{
				Device udated = dbutil.getItemByItemId(id);
				udated.setStatus(status);
				dbutil.updateItem(udated);
				dbutil.createStatEntry(udated);
				try {
					jsobj.put("status", true);
					jsobj.put("deviceId", id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pout.write(jsobj.toString());
			}
			
			
			
		} else if (action != null && action.equalsIgnoreCase("toggleDevice")) {
			/*
			 * Long taskid = req.getParameter("taskId") != null ? Long
			 * .parseLong(req.getParameter("taskId")) : null;
			 * System.out.print("test");
			 * 
			 * if (taskid != null) { DBUtil dbutil = new DBUtil();
			 * dbutil.completeItem(taskid, userid); }
			 */
		} else if (action != null && action.equalsIgnoreCase("deleteDevice")) {
			/*
			 * Long taskid = req.getParameter("taskId") != null ? Long
			 * .parseLong(req.getParameter("taskId")) : null; if (taskid !=
			 * null) { DBUtil dbutil = new DBUtil(); dbutil.deleteItem(taskid,
			 * userid); }
			 */
		}

	}
}
