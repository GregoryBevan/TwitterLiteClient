// Generated code from Butter Knife. Do not modify!
package com.twitterliteclient;

import android.view.View;
import butterknife.Views.Finder;

public class PostMessageActivity$$ViewInjector {
  public static void inject(Finder finder, final com.twitterliteclient.PostMessageActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131230727);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230727' for field 'msgText' was not found. If this field binding is optional add '@Optional'.");
    }
    target.msgText = (android.widget.EditText) view;
    view = finder.findById(source, 2131230728);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230728' for field 'btn' was not found. If this field binding is optional add '@Optional'.");
    }
    target.btn = (android.widget.Button) view;
  }

  public static void reset(com.twitterliteclient.PostMessageActivity target) {
    target.msgText = null;
    target.btn = null;
  }
}
