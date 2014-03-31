package com.oasishome.server;

import java.io.Serializable;

public class Device implements Serializable {
	private static final long serialVersionUID = 2L;

	private long id = -1;
	private Long userId;
	private String name;
	private String nickname;
	private String imageSrc;
	private String xpos;
	private String ypos;
	private long time;
	private int type=-1;
	public static final int LIGHT_TYPE=1;
	public static final int FAN_TYPE=2;
	public static final int TV_TYPE=3;
	public static final int OTHERS_TYPE=4;
	
	
	

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	private boolean status = false;
	private boolean active = false;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	private String micAddr;

	public String getMicAddr() {
		return micAddr;
	}

	public void setMicAddr(String micAddr) {
		this.micAddr = micAddr;
	}

	private long priority = 0;
	private long mod_date;

	private boolean isdeleted = false;

	// static constants used in code
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_HIGH = 2;
	public static final int PRIORITY_LOW = 1;
	public static final int TASK_COMPLETE = 1;
	public static final int TASK_PENDING = 0;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public long getMod_date() {
		return mod_date;
	}

	public void setMod_date(long mod_date) {
		this.mod_date = mod_date;
	}

	

	public boolean isIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(boolean isdeleted) {
		this.isdeleted = isdeleted;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getYpos() {
		return ypos;
	}

	public void setYpos(String ypos) {
		this.ypos = ypos;
	}

	public String getXpos() {
		return xpos;
	}

	public void setXpos(String xpos) {
		this.xpos = xpos;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
