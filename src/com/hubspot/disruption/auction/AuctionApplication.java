package com.hubspot.disruption.auction;

import android.app.Application;

import com.hubspot.disruption.auction.models.AuctionItem;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AuctionApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(AuctionItem.class);

    // Add your initialization code here
    Parse.initialize(this, "JPa6xJnjNpZr7wkueoaw4IXb9YEwY0XUyiAP818I", "mnUoXzGrtG0z5UzU50gSOIE1Gj4gWwODZT5gimSw");


    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
