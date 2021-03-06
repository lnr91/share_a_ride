package com.example.locationbased;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class EnterLocationFragment extends SherlockFragment {
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	Activity parentActivity;
	SharedPreferences prefs;
	TextView displayName;
	Button buttonSubmit; // Button to submit info
	Button buttonCurrentLocation;// button to populate sourceField with
									// currentAddress
	EditText destinationField; // Edit field of destination
	EditText sourceField; // Edit field of source
	LocationManager locationManager;
	LocationListener locationListener;
	TextView locationText;
	Geocoder geocoder; // Geocoder used in Reverse Geocoding..ie from address to
	static final String TAG = "EnterLocationFragment";
	EnterLocationFragInterface mcallback;
	static InputMethodManager imm;

	public interface EnterLocationFragInterface {
		public void showOnMap(Location location);

		public void showMap();

		public void postAndProcessData(String destination, String source);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.d(TAG, "OnAttach");
		super.onAttach(activity);
		mcallback = (EnterLocationFragInterface) getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "Oncreateview");
		View view = inflater.inflate(R.layout.layout_enter_location, null,
				false);

		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE); // for hiding soft keyboard

		buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
		buttonCurrentLocation = (Button) view
				.findViewById(R.id.buttonCurrentLocation);
		destinationField = (EditText) view.findViewById(R.id.destinationField);
		sourceField = (EditText) view.findViewById(R.id.sourceField);
		locationText = (TextView) view.findViewById(R.id.textCurrentLocation);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		 Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 if(lastKnownLocation!=null) mcallback.showOnMap(lastKnownLocation);
		locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {

				Log.d(TAG, "OnProvider Enabled");
			}

			@Override
			public void onProviderDisabled(String provider) {

				Log.d(TAG, "OnProvider Disabled");
			}

			private void makeUseOfNewLocation(Location location) {

				Log.d(TAG,
						"onLocationChanged with location "
								+ location.toString());
				if (isBetterLocation(location, LocationData.currentBestLocation)) {
					LocationData.currentBestLocation = location;
					mcallback.showOnMap(location);
					getAndSetAddressFromLocation(location);

				}

			}

			private void getAndSetAddressFromLocation(Location location) {
				geocoder = new Geocoder(getActivity());
				LocationData.currentLatitude = location.getLatitude();
				LocationData.currentLongitude = location.getLongitude();
				List<Address> addresses;
				try {
					addresses = geocoder.getFromLocation(
							location.getLatitude(), location.getLongitude(), 5);

					LocationData.currentAddress = new StringBuilder("");
					if (addresses.size() > 0) {

						Address address = addresses.get(0);
						LocationData.currentAddress.setLength(0);
						for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
							LocationData.currentAddress.append(
									address.getAddressLine(i)).append(" \n");

						}
					}
					locationText.setText(LocationData.currentAddress); // set
																		// current
																		// address
					// in a textview
					Log.d(TAG, LocationData.currentAddress.toString());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "couldnt get addreses from geocoder");
					e.printStackTrace();
				}
				return;
			}

			@Override
			public void onLocationChanged(Location arg0) {
				Log.d(TAG, "onLocationChanged");
				makeUseOfNewLocation(arg0);

			}

		};
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "OnActivityCreated");
		buttonSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// if(!=null)
				// imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS)
				// ;

				String source = sourceField.getText().toString();
				String destination = destinationField.getText().toString();
				if (LocationData.currentAddress != null) {
					if (source.equalsIgnoreCase(LocationData.currentAddress
							.toString())) {
						// do nothing..have to send current location itself as
						// source location to server
						Log.d(TAG, "Your source is same as current address");
						source = null;
					}
				}

				mcallback.postAndProcessData(destination, source);
				// new ForwardGeocoding().execute(destination,source);
			}
		});

		buttonCurrentLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sourceField.setText(LocationData.currentAddress);
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreated");
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "OnPaused");
		locationManager.removeUpdates(locationListener);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "OnResumed");
		mcallback.showMap();

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, locationListener);

	}

	// Helper functions for determining accuracy of location
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
