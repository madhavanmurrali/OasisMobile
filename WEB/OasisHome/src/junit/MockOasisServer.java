package junit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import com.oasishome.server.PwdEncrypt;
import com.oasishome.server.SyncServiceAction;
import com.oasishome.server.Device;
import com.oasishome.server.SyncServiceAction.requestType;

public class MockOasisServer {
	/*
	
	private String urlString = "http://localhost:8888/syncService";
	private URL wtmLocation;
	private URLConnection connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean setup = false;
	private Long id = null;
	
	@Before
	public void setup() {
		setup = true;
		try {
			wtmLocation = new URL(urlString);
			connection = wtmLocation.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
		} catch (MalformedURLException e) {
			setup = false;
			e.printStackTrace();
		} catch (IOException e) {
			setup = false;
			e.printStackTrace();
		}
	}
	
	@After
	public void teardown() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenericInteraction() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		
		SyncServiceAction request = new SyncServiceAction(requestType.generic, true, null);
		SyncServiceAction response;
		
		try {
			// send a request
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.flush();
			
			// check response
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			
			if (!response.success()) fail("Didn't succeed in response");
			
		} catch (IOException e) {
			fail("Exception in interaction.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Exception reading response");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateUser() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		// test is always the same
		
		SyncServiceAction request = new SyncServiceAction(requestType.validateUser, true, null);
		SyncServiceAction response;
		String username = "simba";
		String password = "scar";
		
		try {
			// request whether a username-password combo is valid (bad way to do it)
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(username);
			out.writeObject(password);
			out.flush();
			
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if (response.success()) {
				fail("simba should not be registered with literal password 'scar'");
			}
			
		} catch (IOException e) {
			fail("IO error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Problem reading response");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateUserEncrypted() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		// first time: should report as user not registered
		// second time: should report successful validation
		
		SyncServiceAction request = new SyncServiceAction(requestType.validateUser, true, null);
		SyncServiceAction response;
		String username = "simba";
		String password = "scar";
		String encrypted = PwdEncrypt.encrypt(password);
		
		try {
			// properly ask for a username-password validation with encryption
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(username);
			out.writeObject(encrypted);
			out.flush();
			
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if (response.success()) {
				System.out.println("simba is registered with encrypted password 'scar'");
				id = (Long)in.readObject();
				System.out.println("userID: " + id);
			}
			if (!response.success()) System.out.println("simba is not registered with encrypted password 'scar'");
			
		} catch (IOException e) {
			fail("IO error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Problem reading response");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRegisterUser() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		// first time: should register the user
		// second time: should report user already registered
		
		SyncServiceAction request = new SyncServiceAction(requestType.registerUser, true, null);
		SyncServiceAction response;
		String username = "simba";
		String password = "scar";
		String encrypted = PwdEncrypt.encrypt(password);
		
		try {
			// proper way to register a new user (encryption)
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(username);
			out.writeObject(encrypted);
			out.flush();
			
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if (!response.success()) {
				System.out.println("simba failed to register with encrypted password 'scar'");
				System.out.println(response.error());
			} else {
				System.out.println("simba registered with encrypted password 'scar'");
				id = (Long)in.readObject();
				System.out.println("userID: " + id);
			}			
		} catch (IOException e) {
			fail("IO error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Problem reading response");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPullDBNoId() {
		if (!setup) fail("Didn't correctly setup the environment!");
		// should be the same response for all runs
		
		SyncServiceAction request = new SyncServiceAction(requestType.pullDB, true, null);
		SyncServiceAction response;
		
		try {
			// issue a pullDB request
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.flush();
			
			// response should be an error message
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if(response.success() == true) fail("Shouldn't have succeeded with no userID");
			System.out.println("Error as expected: " + response.error()); 
			
		} catch (IOException e) {
			fail("IO failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Exception findins response classes!");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPullDBFalseId() {
		if (!setup) fail("Didn't correctly setup the environment!");
		// should be the same response for all runs
		
		SyncServiceAction request = new SyncServiceAction(requestType.pullDB, true, null);
		SyncServiceAction response;
		Long testID = 1234567890L;
		
		try {
			// issue a pullDB request
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(testID);
			out.flush();
			
			// response should be error, id not in database
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if(response.success() == true) fail("Shouldn't have succeeded with false userID");
			System.out.println("Error as expected: " + response.error());
			
		} catch (IOException e) {
			fail("IO failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Couldn't find class?");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPullDB() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		//if (id == null) fail("No user available for testing!");
		id = 1L;
		// dumps tasks if they exist...
		
		SyncServiceAction request = new SyncServiceAction(requestType.pullDB, true, null);
		SyncServiceAction response;
		
		try {
			// send request, and userId as Long
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(id);
			out.flush();
			
			// response should contain the list of tasks
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if (!response.success()) fail("Should have succeeded in providing response");
			@SuppressWarnings("unchecked")
			ArrayList<ToDoItem> list = (ArrayList<ToDoItem>)in.readObject();
			System.out.println("LIST for user " + id + ": ");
			for (ToDoItem item : list) {
				System.out.println("\t" + item.getId() + ": " + item.getName() + " - " + item.getNote());
			}
		} catch (IOException e) {
			fail("IO failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("Class finding error");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSynchronize() {
		if (!setup) fail("Didn't correctly Setup the environment!");
		//if (id == null) fail("No user available for testing!");
		id = 1L;
		
		ArrayList<ToDoItem> not_synced = null;
		ArrayList<ToDoItem> from_CDB = null;
		
		// send a list of tasks that I have changed since last sync with CDB
		SyncServiceAction request = new SyncServiceAction(requestType.synchronize, true, null);
		SyncServiceAction response;
		ArrayList<ToDoItem> list = new ArrayList<ToDoItem>();
		ToDoItem td1 = new ToDoItem();
		ToDoItem td2 = new ToDoItem();
		ToDoItem td3 = new ToDoItem();
		td1.setId(-1); // new item
		td1.setMod_date(System.currentTimeMillis() - (1000 * 30));
		td1.setName("Hello");
		td1.setNoDueTime(true);
		td1.setUserId(id);
		td2.setId(11); // already existing item
		td2.setMod_date(System.currentTimeMillis() - (1000 * 20));
		td2.setName("World");
		td2.setNoDueTime(true);
		td2.setUserId(id);
		td3.setId(12); // already existing item
		td3.setMod_date(System.currentTimeMillis() - (1000 * 40));
		td3.setName("!!!!!!!");
		td3.setNoDueTime(true);
		td3.setUserId(id);
		list.add(td1);
		list.add(td2);
		list.add(td3);
		// also send timestamp of last update (-1 if never has been)
		Date date = new Date(System.currentTimeMillis() - (1000 * 60));
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			out.writeObject(request);
			out.writeObject(id);
			out.writeObject(list);
			out.writeObject(date);
			out.flush();
			
			// get back a list of my tasks that were not updated (for reporting to user)
			in = new ObjectInputStream(connection.getInputStream());
			response = (SyncServiceAction)in.readObject();
			if (!response.success()) {
				fail("Should have synced");
				System.out.println("ERROR: " + response.error());
			}
			not_synced = (ArrayList<ToDoItem>)in.readObject();
			// get back a list of tasks that CDB has changed since my last update
			from_CDB = (ArrayList<ToDoItem>)in.readObject();
			
			if (not_synced == null) fail("Should have got something back...");
			if (from_CDB == null) fail("Should have got list back...");
			
		} catch (IOException e1) {
			fail("IO exception");
			e1.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			fail("Class not found");
			e.printStackTrace();
			return;
		}
		
		for (ToDoItem item : not_synced) {
			System.out.println("CDB didn't add: " + item.getId() + ": " + item.getName());
		}
		for (ToDoItem item : from_CDB) {
			System.out.println("LDB must handle: " + item.getId() + ": " + item.getName());
		}
	}*/
}
