package com.twitterliteclient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.message.model.MessageGetDTO;
import com.appspot.twitterlitesample.message.model.MessageSetDTO;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;


public class PostMessageActivity extends BaseMenuActivity {

	@InjectView(R.id.msg) EditText msgText;
	@InjectView(R.id.post_msg_btn) Button btn;
	
	private Message msgService;
	private Boolean isUpdate;
	
	private class MessageTask extends AsyncTask<Void, Void, MessageGetDTO> {

		private Editable text;
		private String key; // this could be either a message key, on the senderKey
		private Boolean isUpdate;

		public MessageTask(String key, Editable text, Boolean isUpdate) {
			this.key = key;
			this.text = text;
			this.isUpdate = isUpdate;
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

						Toast.makeText(PostMessageActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});
			} catch (final IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								PostMessageActivity.this,
								"The connection with the server failed. Please, try again.",
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.d("POST MESSAGE", e.getMessage(), e);
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MessageGetDTO dto) {
			super.onPostExecute(dto);
			if (dto != null || isUpdate) {
				Toast.makeText(PostMessageActivity.this, "Message Successfully " + (isUpdate ? "updated: ":"posted: ") + text, Toast.LENGTH_SHORT).show();
				// could go to the UserMessagesList
				if (isUpdate)
					finish();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_message_layout);
		
		this.msgService = new Message.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		
		Intent intent = getIntent();
		
		this.isUpdate = intent.getBooleanExtra("isUpdate", Boolean.TRUE);
		
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
					new MessageTask(currentUserKey, text, isUpdate).execute();
				else
					startActivity(new Intent(PostMessageActivity.this, CreateUserOrLoginActivity.class));
			}
		});
	}
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		Intent intent = getIntent();
		Views.inject(this);
		Bundle dto = intent.getBundleExtra("dto");
		if (dto != null) {
			msgText.setText(dto.getString("text"));
			btn.setText("Update");
		}
		return super.onCreateView(name, context, attrs);
	}
}
