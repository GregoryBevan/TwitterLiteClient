package com.twitterliteclient;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.user.User;
import com.appspot.twitterlitesample.user.model.UserGetDTO;
import com.appspot.twitterlitesample.user.model.UsersCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.twitterliteclient.util.ErrorUtil;

public class UserListActivity extends BaseMenuActivity {
	
	private static final int PAGE_SIZE = 10;
	
	@InjectView(R.id.users) ListView usersList;
	@InjectView(R.id.no_users_placeholder) TextView noUsersView;
	
	private String currentCursor = null;
	private User userService;
	private UsersAdapter mAdapter;
	private USER_LIST_TYPE type;
	
	public static interface RequestUserAction {
		public abstract UsersCollection execute() throws IOException, GoogleJsonResponseException;
	}
	
	private class GetUsersTask extends AsyncTask<Void, Void, List<UserGetDTO>> {

		private UserListActivity context = UserListActivity.this;
		private RequestUserAction action;
		
		public GetUsersTask(RequestUserAction action) {
			this.action = action;
		}

		@Override
		protected List<UserGetDTO> doInBackground(Void... params) {
			try {
				UsersCollection users = action.execute();
				context.currentCursor = users.getCursor();
				return users.getList();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<UserGetDTO> dtos) {
			super.onPostExecute(dtos);
			if (dtos != null) {
				if (mAdapter == null) {
					if (dtos.size() == 0)
						noUsersView.setVisibility(View.VISIBLE);
					mAdapter = new UsersAdapter(context, R.layout.message_item, dtos);
					usersList.setAdapter(mAdapter);
				}
				else {
					mAdapter.addAll(dtos);
					mAdapter.notifyDataSetChanged();
					Toast.makeText(context, "NEXT USER BATCH LOADED FROM SERVER", Toast.LENGTH_SHORT).show();
				}
			} else
				Log.d("USER MESSAGES", "PROBLEM ON ADAPTER");
		}
	}
	
	private class FollowActionsTask extends AsyncTask<Void, Void, Void> {

		private String followedKey;
		private String followerKey;
		private Activity activity;
		private boolean isFollowAction;
		private Button actionBtn;
		private ProgressBar progress;
		private int listCellPosition;
		
		public FollowActionsTask(String followedKey, String followerKey, Activity activity, int listCellPosition, Button actionBtn, ProgressBar progress, boolean isFollowAction) {
			super();
			this.followedKey = followedKey;
			this.followerKey = followerKey;
			this.activity = activity;
			this.actionBtn = actionBtn;
			this.progress = progress;
			this.isFollowAction = isFollowAction;
			this.listCellPosition = listCellPosition;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			actionBtn.setVisibility(View.GONE);
			actionBtn.setText(isFollowAction ? R.string.unfollow : R.string.follow);
			progress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (isFollowAction)
					userService.follow(followedKey, followerKey).execute();
				else
					userService.unfollow(followedKey, followerKey).execute();
			} catch (Exception e) {
				ErrorUtil.handleEndpointsException(activity, e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String msg = isFollowAction ? "FOLLOW ACTION" : "UNFOLLOW ACTION";
			Toast.makeText(UserListActivity.this, msg, Toast.LENGTH_SHORT).show();
			actionBtn.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			
			if (!isFollowAction && type == USER_LIST_TYPE.FOLLOWING) {
				mAdapter.remove(mAdapter.getItem(listCellPosition));
				mAdapter.notifyDataSetChanged();
			}
			
		}
	}
	
	public OnClickListener setFollowActionButtonOnClickListener(final UserGetDTO user, final Button actionBtn, final ProgressBar progress, final int listCellPosition) {
		final String currentUserKey = getApp().getCurrentUserKey();
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				new FollowActionsTask(user.getUserKey(), currentUserKey, UserListActivity.this, listCellPosition, actionBtn, progress, !user.getIsFollowedByCurrentUser()).execute();
			}
		};
	}
	
	public RequestUserAction dispathRequestAction(final String userKeyParam) {
		switch (type) {
		case FOLLOWERS:
			return new RequestUserAction() {
				@Override
				public UsersCollection execute() throws IOException, GoogleJsonResponseException {
					return userService.followers(PAGE_SIZE, userKeyParam).setCursor(currentCursor).execute();
				}
			};
		case FOLLOWING:
			return new RequestUserAction() {
				@Override
				public UsersCollection execute() throws IOException, GoogleJsonResponseException {
					return userService.followed(PAGE_SIZE, userKeyParam).setCursor(currentCursor).execute();
				}
			};
		case ALL:
			return new RequestUserAction() {
				@Override
				public UsersCollection execute() throws IOException, GoogleJsonResponseException {
					return userService.list(PAGE_SIZE).setCursor(currentCursor).execute();
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
		setContentView(R.layout.users_list_layout);
		this.userService = new User.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null).build();
		Views.inject(this);
		
		this.type = USER_LIST_TYPE.valueOf(getIntent().getExtras().getString("list_type"));
		
		getActionBar().setTitle(this.type.toString());
		
		final String currentUserKey = getApp().getCurrentUserKey();
		String userKey = getIntent().getStringExtra("senderKey");
		final String userKeyParam = userKey != null ? userKey : currentUserKey;
		
		// retrieve users
		new GetUsersTask(dispathRequestAction(userKeyParam)).execute();
		
        OnScrollListener endlessListScrollListener = new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
				boolean loadMore = firstVisible + visibleCount >= totalCount;
				Log.d("ENDLESS SCROLL", "FirstVisible " + firstVisible);
				Log.d("ENDLESS SCROLL", "visibleCount " + visibleCount);
				Log.d("ENDLESS SCROLL", "totalCount " + totalCount);
				
				if (loadMore) {
					new GetUsersTask(dispathRequestAction(userKeyParam)).execute();
				}
			}
		};
		
		usersList.setOnScrollListener(endlessListScrollListener);
		
		usersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				UserGetDTO dto = mAdapter.getItem(position);
				Intent intent = new Intent(UserListActivity.this, MessageListActivity.class);
				intent.putExtra("list_type", MSG_LIST_TYPE.USER_MSGS.toString());
				intent.putExtra("senderKey", dto.getUserKey());
				startActivity(intent);
			}
		});
	}
}
