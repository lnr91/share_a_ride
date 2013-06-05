package com.example.locationbased;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class ShowCoPassengersFragment extends SherlockFragment{
	
	 PassengerAdapter passengerAdapter;
		String result;
		ArrayList<PassengerProfile> copassengersList;
		String from[]={"email_id","source_address"};
		int to[]={R.id.emailIdPassenger,R.id.sourceAddressPassenger};
		ListView listView;
		String TAG="ShowCoPassengersFragment";
		ShowCoPassengerFragInterface mcallback;
		
		public interface ShowCoPassengerFragInterface
		{
			public void openProfileOfPassenger(PassengerProfile profile);
		}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view= inflater.inflate(R.layout.layout_show_copassengers, null,false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return view;
				
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		result=LocationData.response;
		
		if(result==null)
		{
		LocationData.response=savedInstanceState.getString("result");	
		result=LocationData.response;
		}
		
		
		mcallback=(ShowCoPassengerFragInterface) getActivity();
		 listView=(ListView)getView(). findViewById(R.id.listview1);
			copassengersList = new ArrayList<PassengerProfile>();

			
			if(result.trim().equalsIgnoreCase("noresults"))
				{Log.d("locationbasedactivity","There are no results");
				
				
				}				
			else if (!result.trim().equalsIgnoreCase("noresults")) 
				{ Log.d("locationbasedactivity", "have inside if loop");
					
				try {
					JSONObject jsonObject =new JSONObject(result);
					JSONArray  usersArray = jsonObject.getJSONArray("users");
					for (int i = 0; i < usersArray.length(); i++) {
				    JSONObject jsonObject1 = usersArray.getJSONObject(i);
				    HashMap passengerDetails =new HashMap();
				    Log.d("locationbasedactivity", "inside THE showcopassengers");
				    Log.d("locationbasedactivity",jsonObject1.get("email_id").toString());
				    passengerDetails.put("email_id", jsonObject1.get("email_id").toString());
				    passengerDetails.put("source_address", jsonObject1.get("source_address").toString());
				    PassengerProfile profile =new PassengerProfile();
				   profile.emailIdPassenger=jsonObject1.getString("email_id");
				   profile.firstNamePassenger=jsonObject1.getString("first_name");
				   profile.sourceLatitudePassenger=jsonObject1.getDouble("source_latitude");
				   profile.sourceLongitudePassenger=jsonObject1.getDouble("source_longitude");
				   profile.destinationLatitudePassenger=jsonObject1.getDouble("destination_latitude");
				   profile.destinationLongitudePassenger=jsonObject1.getDouble("destination_longitude");
				   profile.sourceAddressPassenger=jsonObject1.getString("source_address");
				   profile.destinationAddressPassenger=jsonObject1.getString("destination_address");
				   profile.modeOfPassenger=jsonObject1.getString("mode_of_rider");
				   profile.timeOfRidePassenger=jsonObject1.getString("time_of_ride");
				   profile.destinationDistance=jsonObject1.getDouble("distanced");
				   profile.sourceDistance=jsonObject1.getDouble("distances");
				   copassengersList.add(profile);	
				   Log.d("locationbasedactivity", "Name is"+ profile.firstNamePassenger);
				}
				
				
				
				
				passengerAdapter = new PassengerAdapter(getActivity(), R.layout.row_passenger, copassengersList);

			     listView.setAdapter(passengerAdapter);   
			     listView.setOnItemClickListener(new OnItemClickListener() {
			    	  @Override
			    	  public void onItemClick(AdapterView<?> parent, View view,
			    	    int position, long id) {
			    		  Log.d(TAG, "OnItem clicked");
			    	   mcallback.openProfileOfPassenger(copassengersList.get(position));
			    		  
			    		  // Toast.makeText(getActivity(),"Click ListItem Number " + position, Toast.LENGTH_LONG).show();
			    	    
			    	  }
			    	}); 
			     Log.d("locationbasedactivity", "size is "+copassengersList.size());
		           //Log.d("locationbasedactivity","Passengeradapter item 1 is "+ passengerAdapter.getItem(0).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("locationbasedactivity", "JsonException has occured");
					e.printStackTrace();
				}	
				
				}
				else
				{ //setContentView(R.layout.no_passengers_found);
					
					
				}
		            
			
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("result", result);
		
	}
}
