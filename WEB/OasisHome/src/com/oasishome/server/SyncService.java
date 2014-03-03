package com.oasishome.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oasishome.server.SyncServiceAction.requestType;

@SuppressWarnings("serial")
public class SyncService extends HttpServlet {
	private static final Logger log = Logger.getLogger(SyncService.class.getName());
	
	
	// API FOR SYNCHTONIZATION
//	public void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException, ServletException {
//		log.log(Level.INFO, "SyncService called!");
//		System.out.print("testing on oper");
//
//		ObjectInputStream in = new ObjectInputStream(req.getInputStream());
//		ObjectOutputStream out = new ObjectOutputStream(resp.getOutputStream());
//		SyncServiceAction request = null;
//		SyncServiceAction response = null;
//		
//		// verify input
//		try {
//			request = (SyncServiceAction)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		if (request == null) return;
//		
//		// call method according to request type
//		switch(request.getType()) {
//		case validateUser:
//			validateUser(in, out, request, response);
//			break;
//		case registerUser:
//			registerUser(in, out, request, response);
//			break;
//		case synchronize:
//			synchronize(in, out, request, response);
//			break;
//		case pullDB:
//			pullDB(in, out, request, response);
//			break;
//		default:
//			response = new SyncServiceAction(requestType.generic, true, "ERROR: Generic type does nothing");
//			out.writeObject(response);
//			out.flush();
//		}
//		
//		in.close();
//		out.close();
//	}
//	
//	
//	// API FOR SYNCHTONIZATION - VALIDATE USER .. EVERY LOGIN IN MTM GOES THROUGH THIS ..
//	private void validateUser(ObjectInputStream in, ObjectOutputStream out, 
//			SyncServiceAction request, SyncServiceAction response)
//			throws IOException, ServletException {
//		System.out.print( "SyncService.validateUser requested");
//		
//		String username;
//		String password;
//		Long id = null;
//		try {
//			username = (String)in.readObject();
//			password = (String)in.readObject();
//			id = (new DBUtil()).validateUserLogin(username, password);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		if (id == null) {
//			response = new SyncServiceAction(requestType.validateUserResponse, false, "ERROR: username/password combo invalid");
//		} else {
//			response = new SyncServiceAction(requestType.validateUserResponse, true, null);
//		}
//		out.writeObject(response);
//		if (id != null) out.writeObject(id);
//		out.flush();		
//		
//		System.out.print( "SyncService.validateUser completed");
//		
//	}
//	
//	// API FOR SYNCHTONIZATION - RESGISTER NEW USER FROM MTM
//	private void registerUser(ObjectInputStream in, ObjectOutputStream out, 
//			SyncServiceAction request, SyncServiceAction response)
//			throws IOException, ServletException {
//		log.log(Level.INFO, "SyncService.registerUser requested");
//		
//		String username;
//		String password;
//		User user = new User();
//		try {
//			username = (String)in.readObject();
//			if ((new DBUtil()).selectUser(username)) {
//				response = new SyncServiceAction(requestType.registerUserResponse, false, "ERROR: this user already exists");
//				out.writeObject(response);
//				out.flush();
//				return;
//			}
//			password = (String)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		user.setName(username);
//		user.setPwd(password);
//		user = (new DBUtil()).createUser(user);
//		response = new SyncServiceAction(requestType.registerUserResponse, true, null);
//		out.writeObject(response);
//		out.writeObject(new Long(user.getId()));
//		out.flush();
//		out.close();
//	}
//	
//	// API FOR SYNCHTONIZATION - SYNCING FROM MTM ON LOGIN AND USER REQUEST
//	
//	@SuppressWarnings("unchecked")
//	private void synchronize(ObjectInputStream in, ObjectOutputStream out, 
//			SyncServiceAction request, SyncServiceAction response)
//			throws IOException, ServletException {
//
//		System.out.print( "SyncService.synhronize requested");
//		
//		// try to get the ID //
//		Long userId = null;
//		try {
//			userId = (Long)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//	
//			return;
//		} catch (IOException e) {
//			response = new SyncServiceAction(requestType.synchronizeResponse, false, "Request didn't include ID");
//			out.writeObject(response);
//			out.flush();
//			out.close();
//			return;
//		}
//		// verify the existance of the user //
//		User user = new DBUtil().getUserById(userId);
//		if(user == null) {
//			response = new SyncServiceAction(requestType.synchronizeResponse, false, "Provided user ID doesn't exist");
//			out.writeObject(response);
//			out.flush();
//			out.close();
//			return;
//		}
//		// try to get the ldb update list //
//		ArrayList<Device> ldb_updates = null;
//		try {
//			ldb_updates = (ArrayList<Device>)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			response = new SyncServiceAction(requestType.synchronizeResponse, false, "Class not found");			
//			return;
//		} catch (IOException e) {
//			response = new SyncServiceAction(requestType.synchronizeResponse, false, "Request didn't include update list");
//			out.writeObject(response);
//			out.flush();
//			out.close();
//			return;
//		}
//		// try to get the date //
//		Long last_modified = null;
//		try {
//			last_modified = (Long)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			response = new SyncServiceAction(requestType.synchronizeResponse, false, "Request didn't include last modified date");
//			out.writeObject(response);
//			out.flush();
//			out.close();
//			return;
//		}
//		System.out.print( "SyncService.synhronize requested 12 12");
//		
//		ArrayList<Device> db_faults = new ArrayList<Device>(); // over writes ..
//		ArrayList<Device> cdb_updates = new ArrayList<Device>(); // -1 cases ..
//		ArrayList<Device> new_updates = new ArrayList<Device>(); // new task  / add edit
//		
//		// ldb changes -> cdb
//		for (Device item : ldb_updates) {
//			// if item is new, place in cdb, and on return cdb_update list because now it has a new id
//			if (item.getGlobal_id() == -1) {
//				item.setGlobal_id(new DBUtil().createItem(item));
//				cdb_updates.add(item);
//				continue;
//			}
//			// find analogous item, and get it's date
//			Device cdb_item = new DBUtil().getItemByItemId(item.getGlobal_id());
//			// if cdb < ldb, update cdb
//			if (cdb_item.getMod_date() < item.getMod_date()) {
//				item.setId(cdb_item.getId());
//				new DBUtil().updateItem(item);
//			} else {
//				// else form the list of updates and send
//				cdb_item.setGlobal_id(cdb_item.getId());				
//				cdb_item.setId(item.getId());
//				db_faults.add(cdb_item);
//			}
//		}
//		
//		
//		
//		last_modified=last_modified==null?-1:last_modified;
//		// record cdb changes;
//		for (Device item : new DBUtil().getAllItemsByIdSync(userId)) {
//			System.out.print( " "+last_modified+" --> "+ item.getMod_date());
//				
//			if (item.getMod_date() > last_modified) {
//				item.setGlobal_id(item.getId());
//				new_updates.add(item);
//			}
//		}
//		response = new SyncServiceAction(requestType.synchronizeResponse, true, null);
//		out.writeObject(response);
//		out.writeObject(db_faults);
//		out.writeObject(cdb_updates);
//		out.writeObject(new_updates);
//		out.flush();
//		out.close();
//	}
//	
//	private void pullDB(ObjectInputStream in, ObjectOutputStream out, 
//			SyncServiceAction request, SyncServiceAction response) 
//			throws IOException, ServletException {
//		log.log(Level.INFO, "SyncService.pullDB requested");
//		
//		// try to get the ID //
//		Long id = null;
//		try {
//			id = (Long)in.readObject();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			response = new SyncServiceAction(requestType.pullDBResponse, false, "Request didn't include ID");
//			out.writeObject(response);
//			out.flush();
//			return;
//		}
//		
//		// verify the existance of the user //
//		User user = new DBUtil().getUserById(id);
//		if(user == null) {
//			response = new SyncServiceAction(requestType.pullDBResponse, false, "Provided user ID doesn't exist");
//			out.writeObject(response);
//			out.flush();
//			return;
//		}
//		
//		// return the list of tasks for that user
//		ArrayList<Device> list = new DBUtil().getAllItemsByIdSync(id);
//		response = new SyncServiceAction(requestType.pullDBResponse, true, null);
//		out.writeObject(response);
//		out.writeObject(list);
//		out.flush();
//	}
}
