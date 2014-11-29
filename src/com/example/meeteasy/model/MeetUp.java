package com.example.meeteasy.model;

public class MeetUp {
	private int id;
	private String name;
	private String description;
	private String time;
	private Contact[] members;

	public MeetUp(int id, String name, String description, String time, Contact[] members) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.time = time;
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Contact[] getMembers() {
		return members;
	}

	public void setMembers(Contact[] members) {
		this.members = members;
	}

}
