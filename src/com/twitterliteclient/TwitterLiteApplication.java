package com.twitterliteclient;

import android.app.Application;

public class TwitterLiteApplication extends Application {
	private String currentUserKey = null;

	public String getCurrentUserKey() {
		return currentUserKey;
	}

	public void setCurrentUserKey(String currentUserKey) {
		this.currentUserKey = currentUserKey;
	}
}
