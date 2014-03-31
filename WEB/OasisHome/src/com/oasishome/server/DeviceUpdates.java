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
public class DeviceUpdates extends HttpServlet {
	// private static final Logger log =
	// Logger.getLogger(GetTasks.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		SyncService.syncDevices();
		return;
	}
}
