package com.hubspot.disruption.auction.api;

import com.hubspot.disruption.auction.models.AuctionItem;
import com.hubspot.disruption.auction.models.Bid;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtsuji on 11/14/14.
 */
public class DataManager {
  private List<AuctionItem> items = new ArrayList<AuctionItem>();

  static DataManager singletonInstance;

  public static DataManager getInstance() {
    if (singletonInstance == null)
      singletonInstance = new DataManager();

    return singletonInstance;
  }

  public void fetchAllItems(final Runnable after) {
    ParseQuery<AuctionItem> query = ParseQuery.getQuery(AuctionItem.class);
    query.findInBackground(new FindCallback<AuctionItem>() {
      @Override
      public void done(List<AuctionItem> items, ParseException e) {
        items.addAll(items);

        if (after != null)
          after.run();
      }
    });
  }

  public List<AuctionItem> getAllItems() {
    return items;
  }
}
