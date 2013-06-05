package com.example.locationbased;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urbanairship.push.PushManager;

public class FirstActivity extends Activity {
	static final String TAG = "locationbasedactivity";
	public static SharedPreferences prefs;
	EditText emailIdET;
	EditText passwordET;
	Button loginButton;
	Button signupButton;
	String emailId,password;
	String action="login";
	String apid;
	String gcm_regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		Log.d(TAG, "Oncreated FirstActivity");
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		emailIdET = (EditText) findViewById(R.id.emailID);
		passwordET = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		signupButton = (Button) findViewById(R.id.signupButton);
        
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	emailId=emailIdET.getText().toString();
	password=passwordET.getText().toString();
	Log.d(TAG, "Emailid and passwrd are "+emailId+" and "+password);
	new ValidateLogin().execute(emailId,password,action);
			
				
			}
		});
		
		
		signupButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FirstActivity.this, MainActivity.class); 
			//	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);	
				finish();
	
			}
		});
		
		
		
		
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "On Paused FirstActivity");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "On Resumed FirstActivity");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "On Stopped FirstActivity");
	}
	
	
	class ValidateLogin extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			while(apid==null)
				{apid = PushManager.shared().getAPID();
				gcm_regid = PushManager.shared().getGcmId();
			Log.d(TAG, "APID is "+apid);
			Log.d(TAG, "GCM id is"+gcm_regid);
				}
		List<NameValuePair> values=new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("email_id", params[0]));
		values.add(new BasicNameValuePair("password",params[1]));
		values.add(new BasicNameValuePair("action",action));
		values.add(new BasicNameValuePair("uairship_id", apid));
		values.add(new BasicNameValuePair("gcm_reg_id", gcm_regid));
	
			
	HttpClient httpClient =new DefaultHttpClient();
	HttpPost httpPost = new HttpPost("http://www.sharearide.net63.net/controller.php");
	HttpResponse httpResponse=null;
	String response=null;
	
		InputStream is;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(values));
			httpResponse=httpClient.execute(httpPost);
			HttpEntity entity=httpResponse.getEntity();
			is = entity.getContent();
			response=convertStreamToString(is);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
	if(response.trim().equals("valid"))
     return "valid";
	else
	return "invalid";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result.equals("valid"))
			{
		    Log.d(TAG, result);
			Editor editor =prefs.edit();
			editor.putBoolean("loggedin", true);
			editor.putString("emailId", emailId);
			editor.putString("password",password);
			editor.commit();
			LocationData.emailId=emailId;
			LocationData.password=password;
			Intent intent = new Intent(FirstActivity.this, LocationMainEnterActivity.class); 
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);	
			finish();
			
			}
			else
				Toast.makeText(FirstActivity.this, "Invalid username/password", Toast.LENGTH_LONG).show();
		}
		
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
