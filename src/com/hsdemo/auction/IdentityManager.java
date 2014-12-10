package com.hsdemo.auction;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by jtsuji on 11/14/14.
 */
public class IdentityManager {
  public static final String PREFS_EMAIL_KEY = "EMAIL";
  public static final String PREFS_NAME_KEY = "NAME";
  public static final String SHAREDPREFS_KEY = "auction";

  public static String getName(Activity context) {
    SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
    return prefs.getString(PREFS_NAME_KEY, "");
  }

  public static String getEmail(Activity context) {
    SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
    return prefs.getString(PREFS_EMAIL_KEY, "");
  }

  public static void setEmail(String email, Activity context) {
    SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
    prefs.edit().putString(IdentityManager.PREFS_EMAIL_KEY, email).apply();

  }

  public static void setName(String name, Activity context) {
    SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
    prefs.edit().putString(IdentityManager.PREFS_NAME_KEY, name).apply();
  }
}
