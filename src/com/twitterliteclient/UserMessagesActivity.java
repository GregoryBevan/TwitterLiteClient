package com.twitterliteclient;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.message.model.MessageGetDTO;
import com.appspot.twitterlitesample.message.model.MessagesCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class UserMessagesActivity extends BaseMenuActivity {
	
	@InjectView(R.id.user_msgs) ListView msgsList;
	
	private Message msgService;
	private static final int PAGE_SIZE = 10; 
	private String currentCursor = null;
	
	MessagesAdapter mAdapter;
	
	private class GetUserMessagesTask extends AsyncTask<Void, Void, List<MessageGetDTO>> {

		private String senderKey;
		
		

		public GetUserMessagesTask(String senderKey) {
			this.senderKey = senderKey;
		}

		@Override
		protected List<MessageGetDTO> doInBackground(Void... params) {
			
			try {
				MessagesCollection msgs = msgService.user().messages().list(PAGE_SIZE, senderKey).execute();
				UserMessagesActivity.this.currentCursor = msgs.getCursor();
				return msgs.getList();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<MessageGetDTO> dtos) {
			super.onPostExecute(dtos);
			if (dtos != null) {
				mAdapter = new MessagesAdapter(UserMessagesActivity.this, R.layout.message_item, dtos.toArray(new MessageGetDTO[dtos.size()]));
				msgsList.setAdapter(mAdapter);
				
				msgsList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						MessageGetDTO dto = mAdapter.getItem(position);
						Intent intent = new Intent(UserMessagesActivity.this, PostMessageActivity.class);
						Bundle b = new Bundle();
						b.putString("text", dto.getText());
						b.putString("key", dto.getMessageKey());
						intent.putExtra("dto", b);
						intent.putExtra("isUpdate", true);
					}
				});
			} else {
				Log.d("USER MESSAGES", "PROBLEM ON ADAPTER");
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_msgs_layout);
		this.msgService = new Message.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		Views.inject(this);
		String currentUserKey = getApp().getCurrentUserKey();
		
		new GetUserMessagesTask(currentUserKey).execute();
	}
}
