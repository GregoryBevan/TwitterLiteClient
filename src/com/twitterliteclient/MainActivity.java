package com.twitterliteclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.appspot.twitterlitesample.auth.Auth;
import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.user.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Auth authService = new Auth.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		User userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		Message msgService = new Message.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
