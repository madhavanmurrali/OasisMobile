package com.oasishome.server;

public class User {
	  private long id;
	  private String name;
	  private String encryptedPwd;
	  private String micAddress;

	  public String getMicAddress() {
		return micAddress;
	}

	public void setMicAddress(String micAddress) {
		this.micAddress = micAddress;
	}

	public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }
	  
	  public String getPwd() {
		  return encryptedPwd;
	  }
	  
	  public void setPwd(String pwd) {
		  this.encryptedPwd = pwd;
	  }

	  @Override
	  public String toString() {
	    return name;
	  }
} 
