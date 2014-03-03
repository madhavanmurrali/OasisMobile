// Author; Michael Landes
package com.oasishome.server;

import java.io.Serializable;

public class SyncServiceAction implements Serializable {
	private static final long serialVersionUID = 2L;

	public enum requestType {
		generic,
		registerUser,
		validateUser,
		synchronize,
		pullDB,
		registerUserResponse,
		validateUserResponse,
		synchronizeResponse,
		pullDBResponse
	}
	
	private requestType type;
	private boolean success;
	private String error;
	
	public SyncServiceAction(requestType _type, boolean _success, String _error) {
		type = _type;
		success = _success;
		error = _error;
	}
	
	public requestType getType() { return type; }
	public boolean success() { return success; }
	public String error() { return error; }
}
