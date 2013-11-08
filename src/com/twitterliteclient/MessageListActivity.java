package com.twitterliteclient;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.message.Message;
import com.appspot.twitterlitesample.message.model.MessageGetDTO;
import com.appspot.twitterlitesample.message.model.MessagesCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.ui.SwipeDismissListViewTouchListener;
import com.twitterliteclient.util.ErrorUtil;

public class MessageListActivity extends BaseMenuActivity {
	
	private static final int PAGE_SIZE = 10;
	private static int ON_MESSAGE_UPDATE_REQUEST_CODE = 1;
	
	@InjectView(R.id.msgs) ListView msgsList;
	@InjectView(R.id.no_msgs_placeholder) TextView noMsgsView;
	
	private String currentCursor = null;
	private Message msgService;
	private MessagesAdapter mAdapter;
	private MSG_LIST_TYPE type;
	
	public static interface RequestAction {
		public abstract MessagesCollection execute() throws IOException, GoogleJsonResponseException;
	}
	
	private class GetMessagesTask extends AsyncTask<Void, Void, List<MessageGetDTO>> {

		private MessageListActivity context = MessageListActivity.this;
		private RequestAction action;
		
		public GetMessagesTask(RequestAction action) {
			this.action = action;
		}

		@Override
		protected List<MessageGetDTO> doInBackground(Void... params) {
			try {
				MessagesCollection msgs = action.execute();
				context.currentCursor = msgs.getCursor();
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
					if (dtos.size() == 0)
						noMsgsView.setVisibility(View.VISIBLE);
					mAdapter = new MessagesAdapter(context, R.layout.message_item, dtos);
					msgsList.setAdapter(mAdapter);
				}
				else {
					mAdapter.addAll(dtos);
					mAdapter.notifyDataSetChanged();
					Toast.makeText(context, "NEXT MESSAGE BATCH LOADED FROM SERVER", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(MessageListActivity.this, "REMOVED MSG: " + text, Toast.LENGTH_SHORT).show();
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
	
	public RequestAction dispathRequestAction(final String userKeyParam) {
		switch (type) {
		case TIMELINE:
			return new RequestAction() {
				@Override
				public MessagesCollection execute() throws IOException, GoogleJsonResponseException {
					return msgService.user().timeline().list(PAGE_SIZE, userKeyParam).setCursor(currentCursor).execute();
				}
			};
		case USER_MSGS:
			return new RequestAction() {
				@Override
				public MessagesCollection execute() throws IOException, GoogleJsonResponseException {
					return msgService.user().messages().list(PAGE_SIZE, userKeyParam).setCursor(currentCursor).execute();
				}
			};
		case ALL:
			return new RequestAction() {
				@Override
				public MessagesCollection execute() throws IOException, GoogleJsonResponseException {
					return msgService.list(PAGE_SIZE).setCursor(currentCursor).execute();
				}
			};
		default:
			break;
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msgs_list_layout);
		this.msgService = new Message.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		Views.inject(this);
		this.type = MSG_LIST_TYPE.valueOf(getIntent().getStringExtra("list_type"));
		getActionBar().setTitle(this.type.toString());
		final String currentUserKey = getApp().getCurrentUserKey();
		String senderKey = getIntent().getStringExtra("senderKey");
		
		// retrieve messages
		// this way we can control who owns the message list
		// it would be better to pass more information to show at the top of the list
		final String userKeyParam = senderKey != null ? senderKey : currentUserKey;
		new GetMessagesTask(dispathRequestAction(userKeyParam)).execute();
		
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		msgsList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                            	MessageGetDTO dto = mAdapter.getItem(position);
                            	// test if message can be deleted
                            	return dto.getIsMessageFromCurrentUser();
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	MessageGetDTO dto = mAdapter.getItem(position);
                                	String msgKey = dto.getMessageKey();
                                	String text = dto.getText();
                                    mAdapter.remove(mAdapter.getItem(position));
                                    new DeleteMessageTask(text, msgKey, MessageListActivity.this).execute();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        msgsList.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        
        final OnScrollListener dismissCompliantScrollListener = touchListener.makeScrollListener();
        
        OnScrollListener endlessListScrollListener = new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissCompliantScrollListener.onScrollStateChanged(view, scrollState);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
				boolean loadMore = firstVisible + visibleCount >= totalCount;

				if (loadMore) {
					new GetMessagesTask(dispathRequestAction(userKeyParam)).execute();
				}
			}
		};
        
        msgsList.setOnScrollListener(endlessListScrollListener);
        
        // only current user messages are editable
        if (this.type == MSG_LIST_TYPE.USER_MSGS && senderKey == null) {
			msgsList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					MessageGetDTO dto = mAdapter.getItem(position);
					Intent intent = new Intent(MessageListActivity.this, PostMessageActivity.class);
					Bundle b = new Bundle();
					b.putString("text", dto.getText());
					b.putString("key", dto.getMessageKey());
					b.putInt("position", position);
					intent.putExtra("dto", b);
					intent.putExtra("isUpdate", true);
					MessageListActivity.this.startActivityForResult(intent, ON_MESSAGE_UPDATE_REQUEST_CODE);
				}
			});
        }
	}
}
