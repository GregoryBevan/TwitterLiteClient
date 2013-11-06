package com.twitterliteclient;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.message.model.MessageGetDTO;
import com.appspot.twitterlitesample.message.model.MessagesCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.ui.SwipeDismissListViewTouchListener;
import com.twitterliteclient.util.ErrorUtil;

public class UserMessagesActivity extends BaseMenuActivity {
	
	@InjectView(R.id.user_msgs) ListView msgsList;
	
	private Message msgService;
	private static final int PAGE_SIZE = 10;
	private static int ON_MESSAGE_UPDATE_REQUEST_CODE = 1;
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
				if (mAdapter == null) {
					mAdapter = new MessagesAdapter(UserMessagesActivity.this, R.layout.message_item, dtos);
					msgsList.setAdapter(mAdapter);
				}
			} else
				Log.d("USER MESSAGES", "PROBLEM ON ADAPTER");
		}
	}
	
	private class DeleteMessageTask extends AsyncTask<Void, Void, Void> {

		private String msgKey;
		private String text;
		private Activity activity;
		
		public DeleteMessageTask(String text, String msgKey, Activity activity) {
			super();
			this.text = text;
			this.msgKey = msgKey;
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				msgService.delete(msgKey).execute();
			} catch (Exception e) {
				ErrorUtil.handleEndpointsException(activity, e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(UserMessagesActivity.this, "REMOVED MSG: " + text, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ON_MESSAGE_UPDATE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle dto = data.getBundleExtra("dto");
				int pos = dto.getInt("position");
				mAdapter.getItem(pos).setText(dto.getString("text"));
				mAdapter.notifyDataSetChanged();
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
		
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		msgsList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	MessageGetDTO dto = mAdapter.getItem(position);
                                	String msgKey = dto.getMessageKey();
                                	String text = dto.getText();
                                    mAdapter.remove(mAdapter.getItem(position));
                                    new DeleteMessageTask(text, msgKey, UserMessagesActivity.this).execute();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        msgsList.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        msgsList.setOnScrollListener(touchListener.makeScrollListener());
        
		msgsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MessageGetDTO dto = mAdapter.getItem(position);
				Intent intent = new Intent(UserMessagesActivity.this, PostMessageActivity.class);
				Bundle b = new Bundle();
				b.putString("text", dto.getText());
				b.putString("key", dto.getMessageKey());
				b.putInt("position", position);
				intent.putExtra("dto", b);
				intent.putExtra("isUpdate", true);
				UserMessagesActivity.this.startActivityForResult(intent, ON_MESSAGE_UPDATE_REQUEST_CODE);
			}
		});
		
		new GetUserMessagesTask(currentUserKey).execute();
	}
}
