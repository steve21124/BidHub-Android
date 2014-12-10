package com.hubspot.disruption.auction;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.hubspot.disruption.auction.models.AuctionItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class ItemListActivity extends ActionBarActivity {

  @InjectView(R.id.itemslist)
  ListView itemsList;

  ArrayList<AuctionItem> allItems = new ArrayList<AuctionItem>();
  BaseAdapter adapter;

  boolean gotFirstBids;

  @InjectView(R.id.toolbar)
  Toolbar toolbar;

  @InjectView(R.id.base_tint_darken)
  View tint;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    DisplayUtils.init(this);

    setContentView(R.layout.main);
    ButterKnife.inject(this);



    setSupportActionBar(toolbar);

    toolbar.setTitleTextAppearance(this, R.style.basefont_light);
    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    tint.setVisibility(View.GONE);

    getSupportActionBar().setTitle("Auction Items");
	}

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  Handler handler = new Handler();

  public Runnable getBids = new Runnable() {
    @Override
    public void run() {
      ParseQuery query = ParseQuery.getQuery("NewBid");
      query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          Log.i("TEST", "Refreshing...");

          for (ParseObject bid : list) {
            for (AuctionItem item : allItems) {
              if (item.objectId.equals(bid.get("item"))) {
                item.bidQty++;
                if (bid.get("email").equals(IdentityManager.email)) {

                }

                // Someone else's bid.
                else {
                  item.currentPrice = Math.max(bid.getInt("amt"), item.currentPrice);
                }

              }
            }
          }

          handler.postDelayed(getBids, 1000);

          if (!gotFirstBids)
            setup();

          gotFirstBids = true;
          adapter.notifyDataSetChanged();
        }
      });
    }
  };

  public void setup() {
    adapter = new AuctionItemAdapter();
    itemsList.setAdapter(adapter);
    itemsList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
          if (initializing)
            initializing = false;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });
  }

  public void bidOnItem(AuctionItem item, int amt) {
    item.myBid = item.currentPrice + amt;

    ParseObject bid = new ParseObject("NewBid");
    bid.put("item", item.getObjectId());
    bid.put("email", IdentityManager.email);
    bid.put("name", IdentityManager.name);
    bid.put("amt", item.myBid);
    bid.saveInBackground();

    adapter.notifyDataSetChanged();
  }

  boolean initializing = true;

  public class AuctionItemAdapter extends BaseAdapter {

    public class CardViewHolder {

      @InjectView(R.id.dashcard)
      CardView card;

      @InjectView(R.id.dashcard_top)
      public RelativeLayout top;

      @InjectView(R.id.dashcard_contentcontainer)
      public View contentContainer;

      @InjectView(R.id.dashcard_top_separator)
      public View topSeparator;

      @InjectView(R.id.dashcard_bottom)
      public View bottom;

      @InjectView(R.id.dashcard_loadingspinner)
      ProgressBar loader;

      @InjectView(R.id.dashcard_icon)
      TextView icon;

      @InjectView(R.id.banner)
      NetworkImageView banner;

      @InjectView(R.id.itemcard_title)
      TextView title;

      @InjectView(R.id.itemcard_donor)
      TextView donor;

      @InjectView(R.id.itemcard_description)
      TextView description;

      @InjectView(R.id.itemcard_price)
      TextView price;

      @InjectView(R.id.plusone)
      Button plusOne;

      @InjectView(R.id.plusfive)
      Button plusFive;

      @InjectView(R.id.plusten)
      Button plusTen;

      @InjectView(R.id.itemcard_messagecontainer)
      View messageContainer;

      @InjectView(R.id.itemcard_message)
      TextView message;

      @InjectView(R.id.itemcard_bidqty)
      TextView bidQty;

      public CardViewHolder(View view) {
        ButterKnife.inject(this, view);
        DisplayUtils.pictosifyTextView(icon);
      }

    }

    @Override
    public int getCount() {
      return allItems.size();
    }

    @Override
    public AuctionItem getItem(int position) {
      return allItems.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    ArrayList<String> fetchedImages = new ArrayList<String>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      CardViewHolder v;

      if (convertView == null) {
        View itemView = getLayoutInflater().inflate(R.layout.item, null);
        v = new CardViewHolder(itemView);
        itemView.setTag(v);
        convertView = itemView;
      }
      else {
        v = (CardViewHolder) convertView.getTag();
      }


      final AuctionItem item = getItem(position);


      if (!fetchedImages.contains(item.imageUrl)) {
        v.banner.setImageUrl(item.imageUrl, DisplayUtils.imageLoader);
        fetchedImages.add(item.imageUrl);
      }

      v.description.setText(item.description);
      v.donor.setText(item.donorName);
      v.title.setText(item.name);
      v.price.setText("$" + Math.max(item.myBid, item.currentPrice));
      v.bidQty.setText(item.bidQty + " bids");

      if (initializing) {
        initializing = false;
        v.top.setAlpha(0);
        v.bottom.setAlpha(0);
        v.loader.setAlpha(0);

       animateCardEntry(v, position);
      }
      else {
        v.contentContainer.setTranslationY(DisplayUtils.toPx(42));
        v.contentContainer.setAlpha(1);
        v.contentContainer.setScaleY(1);
        v.topSeparator.setAlpha(1);
      }

      v.plusOne.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bidOnItem(item, 1);
        }
      });

      v.plusFive.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bidOnItem(item, 5);
        }
      });

      v.plusTen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bidOnItem(item, 10);
        }
      });

      if (item.myBid > -1) {
        if (item.myBid > item.currentPrice) {
          v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_top));
          v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_top));
          v.price.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
          v.messageContainer.setVisibility(View.VISIBLE);
          v.messageContainer.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
          v.message.setText("Your $" + item.myBid + " bid is winning!");
        }
        else {
          v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_top));
          v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_top));
          v.price.setTextColor(getResources().getColor(R.color.primary_orange));
          v.messageContainer.setVisibility(View.VISIBLE);
          v.messageContainer.setBackgroundColor(getResources().getColor(R.color.primary_orange));
          v.message.setText("Your $" + item.myBid + " bid isn't good enough anymore.");
        }
      }
      else {
        v.price.setTextColor(Color.LTGRAY);
        v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_top));
        v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_top));
        v.messageContainer.setVisibility(View.GONE);
      }

      return convertView;
    }

    public void animateCardEntry(CardViewHolder v, int pos) {
      int baseStartDelay = 250;

      if (false)
        baseStartDelay += pos * 250;

      v.top.setVisibility(View.VISIBLE);
      v.top.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250).setStartDelay(baseStartDelay + 150)
              .alpha(1);

      v.contentContainer.setScaleY(0.9f);
      v.contentContainer.setTranslationY(DisplayUtils.toPx(92));
      v.contentContainer.setAlpha(0);

      v.contentContainer.animate().setInterpolator(new OvershootInterpolator()).setDuration(350).setStartDelay(baseStartDelay)
              .translationY(DisplayUtils.toPx(42)).alpha(1).scaleY(1);

      v.bottom.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(150).setStartDelay(baseStartDelay)
              .alpha(1);//.translationY(DisplayUtils.toPx(50));

      v.topSeparator.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(350).setStartDelay(baseStartDelay)
              .alpha(1);
    }
  }
}
