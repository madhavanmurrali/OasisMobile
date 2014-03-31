package com.oasishome.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetBillPredictions extends HttpServlet {
	// private static final Logger log =
	// Logger.getLogger(GetTasks.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv = req.getSession();
		PrintWriter pout = resp.getWriter();

		Long userid = (Long) serv.getAttribute("loggedinUserId");
		Gson jsonbjG = new Gson();
		com.google.appengine.labs.repackaged.org.json.JSONObject jsonbj = new com.google.appengine.labs.repackaged.org.json.JSONObject();
		// String micAddress = (String) serv.getAttribute("micAddress");
		String name = req.getParameter("uname");
		String pwd = req.getParameter("pwd");
		DBUtil dbutil = new DBUtil();

		Boolean isMobile = req.getParameter("isMobile") != null ? (Boolean
				.parseBoolean(req.getParameter("isMobile"))) : true;

		if (userid == null && name != null) {
			userid = dbutil.validateUserLogin(name, PwdEncrypt.encrypt(pwd));
			System.out.print(userid);
		}
		System.out.print(userid);
		if (userid == null) {
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

		com.oasishome.server.User user = dbutil.getUserById(userid);
		String micAddress = user.getMicAddress();// (String)
													// serv.getAttribute("micAddress");
		ArrayList<Device> devices = new ArrayList<Device>();
		System.out.println("****************SUCCESS" + userid);
		HashMap<String, Object> devicesListings = new HashMap<String, Object>();
		org.json.JSONArray jqChartTable = new org.json.JSONArray();
		org.json.JSONArray jqChartTops = new org.json.JSONArray();
		try {
			devicesListings = dbutil.getDeviceStats(userid, micAddress);
			HashMap statstable = (HashMap) devicesListings.get("statsDevices");
			Iterator keys = statstable.keySet().iterator();
			while (keys.hasNext()) {
				Device dec = (Device) keys.next();
				String devName = (String) dec.getName();
				HashMap statsMap = (HashMap) statstable.get(dec);
				Double percent = (Double) statsMap.get("percent");
				List dataRow = new ArrayList();
				dataRow.add(devName);
				dataRow.add(percent + "");
				dataRow.add(percent * .5 + "");
				jqChartTable.put(dataRow);
			}

			HashMap topUsages = (HashMap) devicesListings
					.get("udpateValsTopUsage");
			Long gTotal = (Long) devicesListings.get("globalTotal");
			Iterator keys1 = topUsages.keySet().iterator();
			while (keys1.hasNext()) {
				int dec = (int) keys1.next();
				Double cVal = (Double) topUsages.get(dec);
				Double per = cVal / (double) gTotal;
				List dataRow = new ArrayList();
				switch (dec) {
				case Device.FAN_TYPE:
					dataRow.add("Fans");
					break;
				case Device.LIGHT_TYPE:
					dataRow.add("Lights");
					break;

				case Device.TV_TYPE:
					dataRow.add("TV");
					break;

				default:
					dataRow.add("Others");
					break;

				}

				dataRow.add(per);
				jqChartTops.put(dataRow);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		req.setAttribute("username", user.getName());
		req.setAttribute("jqChartTable", jqChartTable.toString());
		req.setAttribute("jqChartTops", jqChartTops.toString());

		if (isMobile) {
			try {
				jsonbj.put("jqChartTable", jqChartTable);
				jsonbj.put("jqChartTops", jqChartTops);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pout.write(jsonbj.toString());
			return;
		}
		// req.setAttribute("devices", jsonbj);
		RequestDispatcher rd = req
				.getRequestDispatcher("jsps/getBillPredictions.jsp");
		resp.setHeader("Cache-Control", "no-cahce");
		resp.setHeader("Expires", "0");
		rd.forward(req, resp);
		return;
	}
}
