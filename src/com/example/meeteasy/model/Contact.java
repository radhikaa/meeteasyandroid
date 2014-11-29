package com.example.meeteasy.model;

import android.graphics.Bitmap;

public class Contact {
	private String name;
	private String userPhoneNumber;
	private Bitmap image;
	public Contact(String name, String userPhoneNumber, Bitmap image) {
		this.name = name;
		this.userPhoneNumber = userPhoneNumber;
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}
	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}

}
