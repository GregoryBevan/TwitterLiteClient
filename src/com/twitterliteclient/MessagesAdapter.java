package com.twitterliteclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.appspot.twitterlitesample.message.model.MessageGetDTO;

public class MessagesAdapter extends ArrayAdapter<MessageGetDTO> {

	public MessagesAdapter(Context context, int resource, List<MessageGetDTO> dtos) {
		super(context, resource, dtos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView != null)
			holder = (ViewHolder) convertView.getTag();
		else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		MessageGetDTO msg = getItem(position);
		
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date(msg.getCreation()));
		holder.date.setText(date);
		holder.text.setText(msg.getText());
		holder.login.setText(msg.getSender().getLogin());
		
		if(msg.getIsMessageFromCurrentUser())
			convertView.setBackgroundResource(android.R.color.holo_blue_dark);
		
		return convertView;
	}
	
	class ViewHolder {
		@InjectView(R.id.sender_login) TextView	login;
		@InjectView(R.id.msg_date) TextView	date;
		@InjectView(R.id.msg_text) TextView	text;
		
		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}
}
