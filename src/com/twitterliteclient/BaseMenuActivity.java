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

		this.app = (TwitterLiteApplication) getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public enum MSG_LIST_TYPE {
		TIMELINE, USER_MSGS, ALL;
	}
	
	public enum USER_LIST_TYPE {
		FOLLOWERS, FOLLOWING, FRIENDS, ALL;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.post_msg:
			startActivity(new Intent(this, PostMessageActivity.class));
			return true;
		case R.id.timeline:
			intent = new Intent(this, MessageListActivity.class);
			intent.putExtra("list_type", MSG_LIST_TYPE.TIMELINE.toString());
			startActivity(intent);
			return true;
		case R.id.user_msgs:
			intent = new Intent(this, MessageListActivity.class);
			intent.putExtra("list_type", MSG_LIST_TYPE.USER_MSGS.toString());
			startActivity(intent);
			return true;
		case R.id.all_msgs:
			intent = new Intent(this, MessageListActivity.class);
			intent.putExtra("list_type", MSG_LIST_TYPE.ALL.toString());
			startActivity(intent);
			return true;
		case R.id.followers:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra("list_type", USER_LIST_TYPE.FOLLOWERS.toString());
			startActivity(intent);
			return true;
		case R.id.following:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra("list_type", USER_LIST_TYPE.FOLLOWING.toString());
			startActivity(intent);
			return true;
		case R.id.all_users:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra("list_type", USER_LIST_TYPE.ALL.toString());
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
