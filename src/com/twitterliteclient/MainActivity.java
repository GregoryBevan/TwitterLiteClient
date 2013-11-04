package com.twitterliteclient;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import butterknife.InjectView;

import com.appspot.twitterlitesample.auth.Auth;
import com.appspot.twitterlitesample.auth.model.LoginDTO;
import com.appspot.twitterlitesample.user.User;
import com.appspot.twitterlitesample.user.model.CreateUserdDTO;
import com.appspot.twitterlitesample.user.model.UserGetDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class MainActivity extends Activity {

	@InjectView(R.id.login) EditText loginText;
	@InjectView(R.id.email) EditText emailText;
	@InjectView(R.id.submit) Button btn;
	
	private Auth authService;
	private User userService;
	
	private class CreateUserAndLoginTask extends AsyncTask<Void, Void, UserGetDTO> {
		
		private Editable login;
		private Editable email;
		
		public CreateUserAndLoginTask(Editable email, Editable login) {
			this.email = email;
			this.login = login;
		}
		
		@Override
		protected UserGetDTO doInBackground(Void... params) {
			try {
				CreateUserdDTO dto = new CreateUserdDTO();
				dto.set("login", login);
				dto.set("email", email);
				userService.create(dto).execute();
				LoginDTO ldto = new LoginDTO();
				ldto.set("login", login);
				ldto.set("email", email);
				authService.login(ldto);
				
			} catch (IOException e) {
				 Log.d("LOGIN", e.getMessage(), e);
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(UserGetDTO result) {
			super.onPostExecute(result);
			
			// TODO: 
			// set some king of current session 
			// move to all users
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.authService = new Auth.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		this.userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Editable login = loginText.getText();
				final Editable email = emailText.getText();
				new CreateUserAndLoginTask(email, login).execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
