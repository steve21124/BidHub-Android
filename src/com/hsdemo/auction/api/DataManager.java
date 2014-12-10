package com.hsdemo.auction.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hsdemo.auction.events.BidsRefreshedEvent;
import com.hsdemo.auction.models.AuctionItem;
import com.hsdemo.auction.models.Bid;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DataManager {
  private List<AuctionItem> allItems = new ArrayList<AuctionItem>();
  static DataManager singletonInstance;
  Handler handler = new Handler();

  public static final int BID_FETCH_INTERVAL = 3000;
  public static final int RETRY_INTERVAL = 60000;

  public static final String QUERY_ALL = "ALL";
  public static final String QUERY_NOBIDS = "NOBIDS";
  public static final String QUERY_MINE = "MINE";

  ParseQuery<Bid> inflightQuery;
  AsyncHttpClient client = new AsyncHttpClient();
  Context context;

  public DataManager() {}

  public static DataManager getInstance() {
    if (singletonInstance == null)
      singletonInstance = new DataManager();

    return singletonInstance;
  }

  public void fetchAllItems() {
    fetchAllItems(new Runnable() {
      @Override
      public void run() {
        EventBus.getDefault().post(new BidsRefreshedEvent());
      }
    });
  }

  public void fetchAllItems(final Runnable after) {
    Log.i("TEST", "Fetching all items.");
    ParseQuery<AuctionItem> query = ParseQuery.getQuery(AuctionItem.class);
    query.addAscendingOrder("closetime");
    query.addAscendingOrder("name");
    query.findInBackground(new FindCallback<AuctionItem>() {
      @Override
      public void done(List<AuctionItem> items, ParseException e) {
        Log.i("TEST", "Items fetched");

        if (items == null) {
          Toast.makeText(context, "Looks like the backend is a little bit sad right now. Try again in a minute?", Toast.LENGTH_LONG).show();
        }
        else {
          allItems.clear();
          allItems.addAll(items);

          after.run();
        }
      }
    });
  }

  public List<AuctionItem> getItemsMatchingQuery(String query, Activity context) {
    if (query.equals(QUERY_ALL)) {
      return allItems;
    }
    else if (query.equals(QUERY_MINE)) {
      ArrayList<AuctionItem> mine = new ArrayList<AuctionItem>();
      for (AuctionItem item : allItems) {
        if (item.hasUserBid(context))
          mine.add(item);
      }

      return mine;
    }
    else if (query.equals(QUERY_NOBIDS)) {
      ArrayList<AuctionItem> noBids = new ArrayList<AuctionItem>();
      for (AuctionItem item : allItems) {
        if (getBidQuantityForItem(item.getObjectId()) == 0)
          noBids.add(item);
      }

      return noBids;
    }
    else {
      ArrayList<String> queryWords = new ArrayList<String>();
      queryWords.addAll(Arrays.asList(query.split(" ")));

      ArrayList<AuctionItem> results = new ArrayList<AuctionItem>();
      for (AuctionItem item : allItems) {
        for (String word : queryWords) {
          if (word.length() > 1 &&
                  (item.getName().toLowerCase().contains(word.toLowerCase()) || item.getDonorName().toLowerCase().contains(word.toLowerCase()) ||
                          item.getDescription().toLowerCase().contains(word.toLowerCase())))
            results.add(item);
        }
      }

      return results;
    }
  }

  public AuctionItem getItemForId(String id) {
    for (AuctionItem item : allItems) {
      if (item.getObjectId().equals(id))
        return item;
    }

    return null;
  }

  public void beginBidCoverage(Context context) {
    Log.i("TEST", "Beginning coverage...");
    this.context = context;

    if (allItems.size() > 0)
      refreshBids.run();
    else
      fetchAllItems(refreshBids);
  }

  public void refreshBidsNow(final Runnable after) {
    Log.i("TEST", "Refreshing bids...");
    EventBus.getDefault().post(new BidsRefreshedEvent());

    if (after != null)
      after.run();
  }

  private Runnable refreshBids = new Runnable() {
    @Override
    public void run() {

      // Post an emergency retry runnable
      handler.removeCallbacks(retryRefreshBids);
      handler.postDelayed(retryRefreshBids, RETRY_INTERVAL);

      // Don't bother if we don't have the items yet
      if (allItems.size() == 0) {
        handler.removeCallbacks(refreshBids);
        handler.postDelayed(refreshBids, BID_FETCH_INTERVAL);
        return;
      }

      fetchAllItems(new Runnable() {
        @Override
        public void run() {
          EventBus.getDefault().post(new BidsRefreshedEvent());
          // handler.postDelayed(refreshBids, BID_FETCH_INTERVAL);
        }
      });
    }
  };

  private Runnable retryRefreshBids = new Runnable() {
    @Override
    public void run() {
      Log.i("TEST", "Retrying...");

      if (inflightQuery != null) {
        Log.i("TEST", "Cancelling inflight query.");
        inflightQuery.cancel();
        inflightQuery = null;
      }

      handler.removeCallbacks(refreshBids);
      handler.removeCallbacks(retryRefreshBids);
      refreshBids.run();
    }
  };

  public int getBidQuantityForItem(String itemId) {
    return getItemForId(itemId).getNumberOfBids();
  }

  public void endBidCoverage() {
    Log.i("TEST", "Ending coverage...");
    handler.removeCallbacks(refreshBids);
  }

  public static abstract class BidCallback {
    public abstract void bidResult(boolean placed);
  }
}
