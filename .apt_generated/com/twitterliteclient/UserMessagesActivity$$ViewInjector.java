// Generated code from Butter Knife. Do not modify!
package com.twitterliteclient;

import android.view.View;
import butterknife.Views.Finder;

public class UserMessagesActivity$$ViewInjector {
  public static void inject(Finder finder, final com.twitterliteclient.UserMessagesActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131230728);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230728' for field 'msgsList' was not found. If this field binding is optional add '@Optional'.");
    }
    target.msgsList = (android.widget.ListView) view;
  }

  public static void reset(com.twitterliteclient.UserMessagesActivity target) {
    target.msgsList = null;
  }
}
