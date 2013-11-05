package com.twitterliteclient;

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

	public MessagesAdapter(Context context, int resource, MessageGetDTO[] objects) {
		super(context, resource, objects);
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
		MessageGetDTO item = getItem(position);
		holder.date.setText((String)item.get("creation"));
		holder.text.setText((String)item.get("text"));
		return convertView;
	}

	class ViewHolder {
		@InjectView(R.id.msg_date) TextView	date;
		@InjectView(R.id.msg_text) TextView	text;
		
		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}
}
