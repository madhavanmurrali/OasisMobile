package com.oasis.android;

/**
 * Class representing the categories of GTodo App
 *
 */
public class List {
	
	// members for the properties
	private String name;
	private int numberOfItems;
	private int showCompleted;
	private int id;
	private int user_id;
	public static int SORTBY_DATE = 1;
	public static int SORTBY_PRIORITY =2;
	
	// getters and setters
	public List(String name){
		this.name = name;
		this.numberOfItems = 0;
		this.showCompleted = 1;
	}
	public List(String name, int number){
		this.name = name;
		this.numberOfItems = number;
	}
	
	public List(int user_id, int id, String name, int number, int showCompleted){
		this.user_id = user_id;
		this.id = id;
		this.name = name;
		this.numberOfItems = number;
		this.showCompleted = showCompleted;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	public int getNumberOfItems(){
		return this.numberOfItems ;
	}
	
	public int getShowCompleted() {
		return showCompleted;
	}
	public void setShowCompleted(int showCompleted) {
		this.showCompleted = showCompleted;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	
	
	
}
