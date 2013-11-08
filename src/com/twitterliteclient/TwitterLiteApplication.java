package com.twitterliteclient;

import android.app.Application;

import com.appspot.twitterlitesample.auth.model.UserGetDTO;

public class TwitterLiteApplication extends Application {
	private UserGetDTO currentUser = null;

	public String getCurrentUserKey() {
		return currentUser.getUserKey();
	}
	
	public UserGetDTO getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(UserGetDTO dto) {
		this.currentUser = dto;
	}

}
