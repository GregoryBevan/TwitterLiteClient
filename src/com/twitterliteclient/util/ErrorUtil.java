package com.twitterliteclient.util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public class ErrorUtil {

	public static void handleEndpointsException(final Activity activity, final Exception e) {
		if (e instanceof GoogleJsonResponseException) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String msg;
					try {
						msg = new JSONObject(((GoogleJsonResponseException)e).getContent()).getString("message");
					} catch (JSONException e) {
						msg = "The connection with the server failed. Please, try again.";
					}
					Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
		else if (e instanceof IOException) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, "The connection with the server failed. Please, try again.", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
