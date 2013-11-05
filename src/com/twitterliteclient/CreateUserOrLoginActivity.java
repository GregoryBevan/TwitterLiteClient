package com.twitterliteclient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.auth.Auth;
import com.appspot.twitterlitesample.auth.model.LoginDTO;
import com.appspot.twitterlitesample.user.User;
import com.appspot.twitterlitesample.user.model.CreateUserdDTO;
import com.appspot.twitterlitesample.user.model.UserGetDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;

public class CreateUserOrLoginActivity extends Activity {

	@InjectView(R.id.login) EditText loginText;
	@InjectView(R.id.email) EditText emailText;
	@InjectView(R.id.create_button) Button btn;
	
	/**
	 * Service objects that manage requests to the backend.
	 */
	public Auth authService;
	public User userService;
	
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
				dto.setLogin(login.toString());
				dto.setEmail(email.toString());
				UserGetDTO udto = userService.create(dto).execute();
				LoginDTO ldto = new LoginDTO();
				ldto.setLogin(login.toString());
				ldto.setEmail(email.toString());
				authService.login(ldto);
				return udto;
			} catch (final GoogleJsonResponseException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String msg;
						try {
							msg = new JSONObject(e.getContent()).getString("message");
						} catch (JSONException e) {
							msg = "The connection with the server failed. Please, try again.";
						}
						
						Toast.makeText(CreateUserOrLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});
			} catch (final IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(CreateUserOrLoginActivity.this, "The connection with the server failed. Please, try again.", Toast.LENGTH_SHORT).show();
					}
				});
				Log.d("LOGIN", e.getMessage(), e);
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(UserGetDTO dto) {
			super.onPostExecute(dto);
			
			if (dto != null)
				Toast.makeText(CreateUserOrLoginActivity.this, "User Successfully created", Toast.LENGTH_SHORT).show();
			
			// TODO: 
			// set some king of current session 
			// move to all users
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user_layout);
		
		this.authService = new Auth.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		this.userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		
		Views.inject(this);
		
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
