package com.example.locationbased;

import com.actionbarsherlock.app.SherlockFragment;

import android.location.Location;

public class LocationData{
	

	public static String emailId;
	public static String password;
	public static String error="none";
	public static double sourceLongitude;
	public static double sourceLatitude;
	public static double destinationLatitude;
	public static double destinationLongitude;
	public static double currentLatitude;
	public static double currentLongitude;
	static StringBuilder currentAddress; // address in text of source
	static StringBuilder destinationAddress = new StringBuilder();// address in text of
															// source
	static 	StringBuilder sourceAddress = new StringBuilder();
	static Location currentBestLocation = null;
	static String response;
	static String url="http://www.sharearide.net63.net/controller.php";
	static String ACTION_SEND_MESSAGE="com.example.locationbased.action.sendmessage"; // intent action when message is sent by user
	
	
	
	
	

}
