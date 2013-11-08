package com.twitterliteclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.auth.model.UserGetDTO;
import com.appspot.twitterlitesample.user.User;
import com.appspot.twitterlitesample.user.model.UserSetDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.util.ErrorUtil;

public class EditUserActivity extends BaseMenuActivity {

		@InjectView(R.id.login) EditText loginText;
		@InjectView(R.id.email) EditText emailText;
		@InjectView(R.id.first_name) EditText firstNameText;
		@InjectView(R.id.last_name) EditText lastNameText;
		@InjectView(R.id.update_button) Button update_btn;
		@InjectView(R.id.progress) ProgressBar progress;
		
		/**
		 * Service objects that manage requests to the backend.
		 */
		public User userService;
		
		private class UpdateUserTask extends AsyncTask<Void, Void, Void> {
			
			private UserSetDTO setDTO;
			private Activity activity;
			private String userKey;
			
			public UpdateUserTask(UserSetDTO setDTO, String userKey, Activity activity) {
				this.setDTO = setDTO;
				this.activity = activity;
				this.userKey = userKey;
			}
			
			@Override
			protected void onPreExecute() {
				progress.setVisibility(View.VISIBLE);
				update_btn.setVisibility(View.GONE);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					userService.patch(userKey, setDTO).execute();
				} catch(Exception e) {
					ErrorUtil.handleEndpointsException(activity, e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(EditUserActivity.this, "User Successfully Updated", Toast.LENGTH_SHORT).show();
				progress.setVisibility(View.GONE);
				update_btn.setVisibility(View.VISIBLE);
				
				// update current user
				UserGetDTO dto = new UserGetDTO();
				dto.setEmail(setDTO.getEmail());
				dto.setLogin(setDTO.getLogin());
				dto.setFirstName(setDTO.getFirstName());
				dto.setLastName(setDTO.getLastName());
				getApp().setCurrentUser(dto);
			}
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.edit_profile_layout);
			
			this.userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
			Views.inject(this);
			
			final UserGetDTO currentUser = getApp().getCurrentUser();
			loginText.setText(currentUser.getLogin());
			emailText.setText(currentUser.getEmail());
			firstNameText.setText(currentUser.getFirstName());
			lastNameText.setText(currentUser.getLastName());
			
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Editable login = loginText.getText();
					final Editable email = emailText.getText();
					final Editable firstName = firstNameText.getText();
					final Editable lastName = lastNameText.getText();
					UserSetDTO dto = new UserSetDTO();
					dto.setEmail(email.toString());
					dto.setLogin(login.toString());
					dto.setFirstName(firstName.toString());
					dto.setLastName(lastName.toString());
					new UpdateUserTask(dto, currentUser.getUserKey(), EditUserActivity.this).execute();
				}
			};
			
			update_btn.setOnClickListener(listener);
		}
}
