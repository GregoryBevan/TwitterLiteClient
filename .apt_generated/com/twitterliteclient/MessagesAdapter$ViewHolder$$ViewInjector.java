// Generated code from Butter Knife. Do not modify!
package com.twitterliteclient;

import android.view.View;
import butterknife.Views.Finder;

public class MessagesAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.twitterliteclient.MessagesAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131230723);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230723' for field 'date' was not found. If this field binding is optional add '@Optional'.");
    }
    target.date = (android.widget.TextView) view;
    view = finder.findById(source, 2131230725);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230725' for field 'text' was not found. If this field binding is optional add '@Optional'.");
    }
    target.text = (android.widget.TextView) view;
    view = finder.findById(source, 2131230724);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230724' for field 'login' was not found. If this field binding is optional add '@Optional'.");
    }
    target.login = (android.widget.TextView) view;
  }

  public static void reset(com.twitterliteclient.MessagesAdapter.ViewHolder target) {
    target.date = null;
    target.text = null;
    target.login = null;
  }
}
