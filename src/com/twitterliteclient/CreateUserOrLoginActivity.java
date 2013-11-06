package com.twitterliteclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.auth.Auth;
import com.appspot.twitterlitesample.auth.model.LoginDTO;
import com.appspot.twitterlitesample.auth.model.UserGetDTO;
import com.appspot.twitterlitesample.user.User;
import com.appspot.twitterlitesample.user.model.CreateUserdDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.util.ErrorUtil;

public class CreateUserOrLoginActivity extends Activity {

	@InjectView(R.id.login) EditText loginText;
	@InjectView(R.id.email) EditText emailText;
	@InjectView(R.id.create_button) Button create_btn;
	@InjectView(R.id.login_button) Button login_btn;
	@InjectView(R.id.button_group) ViewGroup btn_group;
	@InjectView(R.id.progress) ProgressBar progress;
	
	/**
	 * Service objects that manage requests to the backend.
	 */
	public Auth authService;
	public User userService;
	
	private class CreateUserOrLoginTask extends AsyncTask<Void, Void, UserGetDTO> {
		
		private Editable login;
		private Editable email;
		private Activity activity;
		private boolean isCreate;
		
		public CreateUserOrLoginTask(Editable email, Editable login, Activity activity, boolean isCreate) {
			this.email = email;
			this.login = login;
			this.activity = activity;
			this.isCreate = isCreate;
		}
		
		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);
			btn_group.setVisibility(View.GONE);
		}
		
		@Override
		protected UserGetDTO doInBackground(Void... params) {
			try {
				
				if (isCreate) {
					CreateUserdDTO dto = new CreateUserdDTO();
					dto.setLogin(login.toString());
					dto.setEmail(email.toString());
					userService.create(dto).execute();
				}
				
				LoginDTO ldto = new LoginDTO();
				ldto.setLogin(login.toString());
				ldto.setEmail(email.toString());
				return authService.login(ldto).execute();
			} catch (Exception e) {
				ErrorUtil.handleEndpointsException(activity, e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(UserGetDTO dto) {
			super.onPostExecute(dto);
			if (dto != null) {
				Toast.makeText(CreateUserOrLoginActivity.this, "User Successfully " + (isCreate ? "created":"logged in") , Toast.LENGTH_SHORT).show();
				// this is a really basic way of keeping the currentUser Information
				((TwitterLiteApplication)getApplication()).setCurrentUserKey((String)dto.get("userKey"));
				CreateUserOrLoginActivity.this.startActivity(new Intent(CreateUserOrLoginActivity.this, PostMessageActivity.class));
			}
			progress.setVisibility(View.GONE);
			btn_group.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user_layout);
		
		this.authService = new Auth.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		this.userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		
		Views.inject(this);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCreate = true;
				if (v.getId() == create_btn.getId())
					isCreate = true;
				else if (v.getId() == login_btn.getId())
					isCreate = false;
				
				final Editable login = loginText.getText();
				final Editable email = emailText.getText();
				new CreateUserOrLoginTask(email, login, CreateUserOrLoginActivity.this, isCreate).execute();
			}
		};
		
		create_btn.setOnClickListener(listener);
		login_btn.setOnClickListener(listener);
		
		getActionBar().hide();
	}
}
