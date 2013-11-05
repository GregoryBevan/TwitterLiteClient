// Generated code from Butter Knife. Do not modify!
package com.twitterliteclient;

import android.view.View;
import butterknife.Views.Finder;

public class CreateUserOrLoginActivity$$ViewInjector {
  public static void inject(Finder finder, final com.twitterliteclient.CreateUserOrLoginActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131230720);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230720' for field 'loginText' was not found. If this field binding is optional add '@Optional'.");
    }
    target.loginText = (android.widget.EditText) view;
    view = finder.findById(source, 2131230722);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230722' for field 'btn' was not found. If this field binding is optional add '@Optional'.");
    }
    target.btn = (android.widget.Button) view;
    view = finder.findById(source, 2131230721);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131230721' for field 'emailText' was not found. If this field binding is optional add '@Optional'.");
    }
    target.emailText = (android.widget.EditText) view;
  }

  public static void reset(com.twitterliteclient.CreateUserOrLoginActivity target) {
    target.loginText = null;
    target.btn = null;
    target.emailText = null;
  }
}
