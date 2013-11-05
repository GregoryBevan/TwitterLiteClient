package com.twitterliteclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseMenuActivity extends Activity {

	private TwitterLiteApplication app;
	
	protected TwitterLiteApplication getApp() {
		return this.app;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.app = (TwitterLiteApplication)getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		switch (item.getItemId()) {
        case R.id.post_msg:
        	startActivity(new Intent(this, PostMessageActivity.class));
            return true;
        case R.id.user_msgs:
        	startActivity(new Intent(this, UserMessagesActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
	
}
