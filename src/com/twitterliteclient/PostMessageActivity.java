package com.twitterliteclient;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
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

import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.message.model.MessageGetDTO;
import com.appspot.twitterlitesample.message.model.MessageSetDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.util.ErrorUtil;


public class PostMessageActivity extends BaseMenuActivity {

	@InjectView(R.id.msg) EditText msgText;
	@InjectView(R.id.post_msg_btn) Button btn;
	@InjectView(R.id.progress) ProgressBar progress;
	
	private Message msgService;
	private Boolean isUpdate;
	
	private class MessageTask extends AsyncTask<Void, Void, MessageGetDTO> {

		private Editable text;
		private String key; // this could be either a message key, on the senderKey
		private Boolean isUpdate;
		private Activity activity;

		public MessageTask(String key, Editable text, Activity activity, Boolean isUpdate) {
			this.key = key;
			this.text = text;
			this.activity = activity;
			this.isUpdate = isUpdate;
		}
		
		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);
			btn.setVisibility(View.GONE);
		}
		
		protected MessageGetDTO actionOnMessage() throws GoogleJsonResponseException, IOException {
			MessageSetDTO dto = new MessageSetDTO();
			dto.setText(text.toString());
			if (isUpdate) {
				msgService.patch(key, dto).execute();
				return null;
			} else
				return msgService.post(key, dto).execute();
		}
		
		@Override
		protected MessageGetDTO doInBackground(Void... params) {
			try {
				return actionOnMessage();
			} catch(Exception e) {
				ErrorUtil.handleEndpointsException(activity, e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MessageGetDTO dto) {
			super.onPostExecute(dto);
			if (dto != null || isUpdate) {
				Toast.makeText(activity, "Message Successfully " + (isUpdate ? "updated: ":"posted: ") + text, Toast.LENGTH_SHORT).show();
				// could go to the UserMessagesList
				if (isUpdate) {
					Intent returnIntent = new Intent();
					Bundle extra = activity.getIntent().getBundleExtra("dto");
					extra.putString("text", text.toString());
					returnIntent.putExtra("dto", extra);
					setResult(RESULT_OK, returnIntent);
					finish();
				}
			}
			
			progress.setVisibility(View.GONE);
			btn.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_message_layout);
		Views.inject(this);
		this.msgService = new Message.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		
		Intent intent = getIntent();
		
		this.isUpdate = intent.getBooleanExtra("isUpdate", Boolean.FALSE);
		Bundle dto = intent.getBundleExtra("dto");
		final String msgKey;
		if (dto != null) {
			msgText.setText(dto.getString("text"));
			btn.setText("Update");
			msgKey = dto.getString("key");
		}
		else
			msgKey = null;
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Editable text = msgText.getText();
				
				if (text.length() == 0 || text.length() > 140) {
					Toast.makeText(PostMessageActivity.this, "The text length must be between one and 140 characters", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String currentUserKey = getApp().getCurrentUserKey();
				
				if (currentUserKey != null)
					new MessageTask((isUpdate && msgKey != null) ? msgKey : currentUserKey, text, PostMessageActivity.this, isUpdate).execute();
				else
					startActivity(new Intent(PostMessageActivity.this, CreateUserOrLoginActivity.class));
			}
		});
	}
}
