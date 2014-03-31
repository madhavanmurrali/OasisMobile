package com.oasishome.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gwt.logging.client.DevelopmentModeLogHandler;
import com.oasishome.server.SyncServiceAction.requestType;

public class SyncService {
	private static final Logger log = Logger.getLogger(SyncService.class
			.getName());

	// API FOR SYNCHTONIZATION
	public static String fetchDevicesApiHome(String home_id) {
		log.log(Level.INFO, "SyncService called!");
		System.out.print("testing on oper");
		String outputJson = "";

		HttpClient client = new DefaultHttpClient();
		DBUtil db = new DBUtil();
		String url = (String) db.prop.get(home_id + "_READ");
		System.out.print(url);
		if (url == null)
			return null;

		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				outputJson += line;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return outputJson;
	}
	
	public static void syncDevices()
	{
		// accordingly ..
		String devicesList = SyncService.fetchDevicesApiHome(DBUtilConstants.AWARE_HOME_ID);
		if (devicesList != null) {
			org.json.JSONArray json;
			try {
				json = new org.json.JSONArray(devicesList);
				SyncService.checkDevices(json, DBUtilConstants.AWARE_HOME_ID,null);
			} catch (org.json.JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void checkDevices(org.json.JSONArray json, String micAddr,
			Long userId) {
		DBUtil db = new DBUtil();

		for (int i = 0; i < json.length(); i++) {
			try {
				String jsonObj = (String) json.get(i);
				jsonObj = jsonObj.replace("'", "");
				org.json.JSONObject jsonObj1 = new org.json.JSONObject(jsonObj);
				if (jsonObj1.has("uStatus") && jsonObj1.has("uName")) {
					String name = (String) jsonObj1.get("uName");
					Boolean status = (Boolean) jsonObj1.get("uStatus");
					Device dev = new Device();
					dev.setMicAddr(micAddr);
					dev.setName(name);
					dev.setStatus(status);
					dev.setActive(false);
					Long devid = db.getDeviceByMICANDNAME(micAddr, name);
					if (devid == null) {
						long newid = db.createItem(dev);
						dev.setId(newid);
						db.createStatEntry(dev);
						if(userId!=null){
						dev.setUserId(userId);
						db.createItemUserMap(dev);
						}
						System.out.println("Adding");
					} else {
						Device udpated = db.getItemByItemId(devid);
						boolean prevStatus = udpated.isStatus();
						udpated.setStatus(status);
						db.updateItem(udpated);
						if(prevStatus != status){
							db.createStatEntry(udpated);
						}
						System.out.println("updating");
					}
				}

			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				// device wont be added
				e.printStackTrace();
			} catch(Exception exp){
				exp.printStackTrace();
			}
		}

	}

	// API FOR SYNCHTONIZATION
	public static String updateDeviceWrite(String home_id, String name,
			Boolean state) {
		log.log(Level.INFO, "SyncService called!");
		System.out.print("testing on oper");
		String outputJson = "";
		DBUtil db = new DBUtil();
		name=name.replaceAll("\\s", "");
		String url = (String) db.prop.get(home_id + "_WRITE") + name + "/"
				+ (state ? "1" : "0");
		System.out.print(url);

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.addHeader(BasicScheme.authenticate(
				new UsernamePasswordCredentials((String)db.prop.get(home_id + "_UNAME"), (String)db.prop.get(home_id + "_PWD")), "UTF-8",
				false));

		if (url == null)
			return null;

		HttpResponse response;
		try {
			response = client.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				outputJson += line;
			}
			System.out.println(outputJson+" STATUS");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return outputJson;
	}

}
