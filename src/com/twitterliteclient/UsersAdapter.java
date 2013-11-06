package com.twitterliteclient;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.user.model.UserGetDTO;

public class UsersAdapter extends ArrayAdapter<UserGetDTO> {

	public UsersAdapter(Context context, int resource, List<UserGetDTO> dtos) {
		super(context, resource, dtos);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView != null)
			holder = (ViewHolder) convertView.getTag();
		else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		UserGetDTO user = getItem(position);
		
		holder.login.setText("@" + user.getLogin());
		holder.email.setText(user.getEmail());
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		lastName = lastName != null ? lastName : "";
		holder.firstName.setText( (firstName != null ? firstName + " " : "Anonymous") + lastName);
		if (user.getIsFollowedByCurrentUser())
			convertView.setBackgroundResource(android.R.color.holo_purple);
		
		return convertView;
	}
	
	class ViewHolder {
		@InjectView(R.id.login) TextView		login;
		@InjectView(R.id.email) TextView		email;
		@InjectView(R.id.name) TextView	firstName;
		
		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}

}
