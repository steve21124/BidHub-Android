package com.hsdemo.auction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.hsdemo.auction.api.BiddingClient;
import com.hsdemo.auction.api.DataManager;
import com.hsdemo.auction.events.BidsRefreshedEvent;
import com.hsdemo.auction.models.AuctionItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class ItemListActivity extends ActionBarActivity {

  @InjectView(R.id.itemslist)
  ListView itemsList;

  List<AuctionItem> allItems = new ArrayList<AuctionItem>();
  BaseAdapter adapter;

  boolean gotFirstBids;

  @InjectView(R.id.toolbar)
  Toolbar toolbar;

  @InjectView(R.id.base_tint_darken)
  View tint;

  @InjectView(R.id.mainprogress)
  ProgressBar progress;

  Handler handler = new Handler();

  DataManager data = DataManager.getInstance();
  boolean isInitializing = true;
  boolean bidding = false;
  boolean cardsUntouched = true;

  @InjectView(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;
  ActionBarDrawerToggle mDrawerToggle;

  @InjectView(R.id.left_drawer)
  View drawer;

  @InjectView(R.id.menu_all)
  View all;

  @InjectView(R.id.menu_nobids)
  View noBids;

  @InjectView(R.id.menu_myitems)
  View myItems;

  @InjectView(R.id.menu_logout)
  View logout;

  @InjectView(R.id.menu_email)
  TextView userEmail;

  String listQuery = DataManager.QUERY_ALL;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    DisplayUtils.init(this);

    setContentView(R.layout.main);
    ButterKnife.inject(this);

    progress.setVisibility(View.VISIBLE);

    setSupportActionBar(toolbar);
    toolbar.setTitleTextAppearance(this, R.style.basefont_light);
    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    tint.setVisibility(View.VISIBLE);

    getSupportActionBar().setTitle("All Items");

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      if (extras.containsKey("title"))
        getSupportActionBar().setTitle(extras.getString("title"));

      if (extras.containsKey("query"))
        listQuery = extras.getString("query");
    }

    setupDrawer();
    setupMenu();
	}

  public void onEvent(BidsRefreshedEvent event) {
    Log.i("TEST", "Received refresh event");
    if (isInitializing) {
      Log.i("TEST", "Init...");
      isInitializing = false;
      setup();
    }

    if (!bidding) {
      allItems = data.getItemsMatchingQuery(listQuery, this);
      adapter.notifyDataSetChanged();
    }
  }

  public void setup() {
    allItems = data.getItemsMatchingQuery(listQuery, this);
    adapter = new AuctionItemAdapter();
    itemsList.setAdapter(adapter);

    itemsList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
          if (cardsUntouched)
            cardsUntouched = false;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
    });

    progress.setVisibility(View.GONE);
  }

  public class AuctionItemAdapter extends BaseAdapter {

    public class CardViewHolder {

      @InjectView(R.id.itemcard_shell)
      LinearLayout cardShell;

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

      @InjectView(R.id.avatar)
      NetworkImageView avatar;

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

      @InjectView(R.id.placebid)
      Button placeBid;

      @InjectView(R.id.itemcard_messagecontainer)
      View messageContainer;

      @InjectView(R.id.itemcard_message)
      TextView message;

      @InjectView(R.id.itemcard_bidqty)
      TextView bidQty;

      @InjectView(R.id.itemcard_other)
      Button other;

      @InjectView(R.id.itemcard_confirmcontainer)
      View confirmContainer;

      @InjectView(R.id.itemcard_buttons)
      View buttonsContainer;

      @InjectView(R.id.itemcard_confirm)
      Button confirm;

      @InjectView(R.id.itemcard_cancel)
      Button cancel;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      CardViewHolder viewHolder;

      if (convertView == null) {
        View itemView = getLayoutInflater().inflate(R.layout.item, parent, false);
        viewHolder = new CardViewHolder(itemView);
        itemView.setTag(viewHolder);
        convertView = itemView;
      }
      else {
        viewHolder = (CardViewHolder) convertView.getTag();
      }

      final CardViewHolder v = viewHolder;
      final AuctionItem item = getItem(position);

      if (v.banner.getTag() == null || !v.banner.getTag().equals(item.getImageUrl())) {
        v.banner.setImageUrl(item.getImageUrl(), DisplayUtils.imageLoader);
        v.banner.setTag(item.getImageUrl());
      }

      try {
        String[] fullName = item.getDonorName().split(" ");
        if (fullName.length > 1) {
          String first = fullName[0];
          String last = fullName[1];
          String firstInitial = first.substring(0, 1);

          String assumedEmail = firstInitial + last + "@hubspot.com";

          if (v.avatar.getTag() == null || !v.avatar.getTag().equals(item.getImageUrl())) {
            v.avatar.setImageUrl("https://api.hubapi.com/socialintel/v1/avatars?email=" + assumedEmail, DisplayUtils.imageLoader);
            v.avatar.setTag("https://api.hubapi.com/socialintel/v1/avatars?email=" + assumedEmail);
          }
        }
      }
      catch (Exception e) {
        v.avatar.setVisibility(View.GONE);
        // It's not worth crashing over;
      }

      String topBids = "";

      if (item.getQty() > 1) {
        List<Integer> highestBids = item.getAllBids();
        for (int i = 0; i < highestBids.size(); i++) {
          if (i != highestBids.size() - 1)
            topBids += "$" + highestBids.get(i) + ", ";
          else if (i == highestBids.size() - 1 && highestBids.size() == 1)
            topBids += "$" + highestBids.get(i);
          else
            topBids += "and $" + highestBids.get(i);
        }

        if (topBids.length() == 0)
          topBids = "(no bids, yet!)";

        v.description.setText(Html.fromHtml(item.getDescription() +
                String.format("<br><br><b>%d available! Highest %d bidders win.</b><br>The current highest bids are %s.", item.getQty(), item.getQty(), topBids) +
                "<br><br>Bidding closes: " + DateUtils.getRelativeTimeSpanString(item.getCloseTime().getTime()) + "."));
      }
      else
        v.description.setText(Html.fromHtml(item.getDescription() + "<br><br>Bidding closes: " + DateUtils.getRelativeTimeSpanString(item.getCloseTime().getTime()) + "."));

      v.donor.setText(item.getDonorName());
      v.title.setText(item.getName());
      if (item.getQty() > 1 && item.getNumberOfBids() > 1) {
        int[] lhBid = item.getLowHighWinningBid();
        v.price.setText(String.format("$%d-%d", lhBid[0], lhBid[1]));
      }
      else {
        v.price.setText("$" + item.getCurrentHighestBid());
      }

      v.bidQty.setText(data.getBidQuantityForItem(item.getObjectId()) + " bids");

      DisplayUtils.pictosifyTextView(v.other);

      if (cardsUntouched) {
        cardsUntouched = false;
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

      final String topBidsFinal = topBids;

      View.OnClickListener customBidListener = new View.OnClickListener() {
        @Override
        public void onClick(View vi) {
          bidding = true;
          AlertDialog dialog;
          AlertDialog.Builder alert = new AlertDialog.Builder(ItemListActivity.this);

          alert.setTitle("Place bid");
          alert.setMessage(item.getQty() > 1 ? "Enter the total amount of your bid.\n\nThe current highest bids are " + topBidsFinal + ".":
                  "Enter the total amount of your bid (current bid is $" + (item.getLowHighWinningBid()[0]) + ")");

          final EditText input = new EditText(ItemListActivity.this);
          input.setInputType(InputType.TYPE_CLASS_NUMBER);
          input.setPadding(DisplayUtils.toPx(20), DisplayUtils.toPx(10), DisplayUtils.toPx(10), DisplayUtils.toPx(10));
          input.setHint("bid amount");
          input.setBackgroundDrawable(null);
          alert.setView(input);

          alert.setPositiveButton("Bid", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              String value = input.getText().toString();
              try {
                int num = Integer.valueOf(value);

                if (item.getNumberOfBids() > 0 && num <= item.getLowHighWinningBid()[0]) {
                  Toast.makeText(getApplicationContext(), "You have to bid at least $" + (item.getLowHighWinningBid()[0] + 1), Toast.LENGTH_LONG).show();
                  return;
                }
                else if (item.getNumberOfBids() == 0 && num < item.getStartingPrice()) {
                  Toast.makeText(getApplicationContext(), "You have to bid at least $" + (item.getLowHighWinningBid()[0]), Toast.LENGTH_LONG).show();
                  return;
                }

                showConfirm(v, item, num);

              }
              catch (NumberFormatException e) {
                bidding = false;
                Toast.makeText(ItemListActivity.this, "Nice try, but that isn't a number.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
              }
            }
          });

          alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              bidding = false;
            }
          });

          dialog = alert.show();
        }
      };

      v.placeBid.setOnClickListener(customBidListener);
      v.plusOne.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View vi) {
          showConfirm(v, item, item.getLowHighWinningBid()[0] + 5);
        }
      });
      v.plusFive.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View vi) {
          showConfirm(v, item, item.getLowHighWinningBid()[0] + 10);
        }
      });
      v.plusTen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View vi) {
          showConfirm(v, item, item.getLowHighWinningBid()[0] + 25);
        }
      });
      v.other.setOnClickListener(customBidListener);

      if (item.hasUserBid(ItemListActivity.this)) {

        boolean winning = item.getMyBidWinningIdx(ItemListActivity.this) > 0;

        v.plusOne.setEnabled(item.getMyBidWinningIdx(ItemListActivity.this) != 1);
        v.plusFive.setEnabled(item.getMyBidWinningIdx(ItemListActivity.this) != 1);
        v.plusTen.setEnabled(item.getMyBidWinningIdx(ItemListActivity.this) != 1);
        v.other.setEnabled(item.getMyBidWinningIdx(ItemListActivity.this) != 1);

        if (v.messageContainer.getVisibility() == View.GONE) {
          v.messageContainer.setVisibility(View.VISIBLE);
          v.messageContainer.setTranslationY(DisplayUtils.toPx(30));
          v.messageContainer.setAlpha(0);
          v.messageContainer.animate().setDuration(300).setInterpolator(new OvershootInterpolator())
                  .translationY(DisplayUtils.toPx(0)).alpha(0.8f);
        }

        if (winning) {

          v.icon.setText("A");

          v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_top));
          v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_top));
          v.price.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

          v.messageContainer.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

          if (item.getQty() > 1)
            v.message.setText("Your bid is #" + item.getMyBidWinningIdx(ItemListActivity.this) + ".");
          else
            v.message.setText("Your bid is winning. Nice!");
        }

        else {

          v.icon.setText("J");

          v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_top));
          v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_top));
          v.price.setTextColor(getResources().getColor(R.color.primary_orange));
          v.messageContainer.setVisibility(View.VISIBLE);
          v.messageContainer.setBackgroundColor(getResources().getColor(R.color.primary_orange));
          v.message.setText("You've been outbid. Try harder.");
        }
      }
      else {

        v.icon.setText("8");

        v.plusOne.setEnabled(true);
        v.plusFive.setEnabled(true);
        v.plusTen.setEnabled(true);
        v.other.setEnabled(true);
        v.placeBid.setEnabled(true);

        v.price.setTextColor(Color.LTGRAY);
        v.top.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_top));
        v.bottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_top));
        v.messageContainer.setVisibility(View.GONE);
      }

      // Bidding is closed.
      if (!item.isOpenForBidding()) {

        v.messageContainer.setVisibility(View.VISIBLE);
        v.messageContainer.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        v.messageContainer.setAlpha(0.8f);

        if (item.getOpenTime().after(new Date())) {
          v.message.setText("BIDDING BEGINS " + DateUtils.getRelativeTimeSpanString(item.getOpenTime().getTime()) + ".");
        }
        else {
          v.message.setText("BIDDING IS CLOSED.");
        }

        v.plusOne.setEnabled(false);
        v.plusFive.setEnabled(false);
        v.plusTen.setEnabled(false);
        v.other.setEnabled(false);
        v.placeBid.setEnabled(false);
      }

      return convertView;
    }

    public void showConfirm(final CardViewHolder v, final AuctionItem item, final int amt) {
      handler.removeCallbacksAndMessages(null);

      bidding = true;
      v.confirmContainer.setVisibility(View.VISIBLE);
      v.confirmContainer.setAlpha(0);
      v.confirmContainer.setTranslationX(DisplayUtils.toPx(30));

      v.plusOne.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(0)
              .translationX(DisplayUtils.toPx(-60)).alpha(0);
      v.plusFive.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(100)
              .translationX(DisplayUtils.toPx(-60)).alpha(0);
      v.plusTen.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(200)
              .translationX(DisplayUtils.toPx(-60)).alpha(0);
      v.other.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(300)
              .translationX(DisplayUtils.toPx(-60)).alpha(0);

      v.confirmContainer.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(400)
              .alpha(1).translationX(0);

      v.confirm.setText("Yes, bid $" + amt);

      final Runnable reshowBidButtons = new Runnable() {
        @Override
        public void run() {
          v.plusOne.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(300)
                  .translationX(0).alpha(1);
          v.plusFive.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(200)
                  .translationX(0).alpha(1);
          v.plusTen.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(100)
                  .translationX(0).alpha(1);
          v.other.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(450).setStartDelay(0)
                  .translationX(0).alpha(1);

          v.confirmContainer.animate().setInterpolator(new AnticipateOvershootInterpolator()).setDuration(250).setStartDelay(0)
                  .alpha(0).translationX(DisplayUtils.toPx(30));

          Runnable reenableBidding = new Runnable() {
            @Override
            public void run() {
              bidding = false;
              v.confirmContainer.setVisibility(View.GONE);
            }
          };

          handler.removeCallbacks(reenableBidding);
          handler.postDelayed(reenableBidding, 1000);
        }
      };

      v.confirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          final ProgressDialog progressDialog = ProgressDialog.show(ItemListActivity.this, "Placing bid...",
                  "We're placing your bid and confirming that nobody outbid you while you took " +
                          "your sweet time deciding.", true);

          progressDialog.setCancelable(true);

          BiddingClient.placeBid(item, amt, new DataManager.BidCallback() {
            @Override
            public void bidResult(boolean placed) {
              progressDialog.dismiss();
              reshowBidButtons.run();
              allItems = data.getItemsMatchingQuery(listQuery, ItemListActivity.this);
              adapter.notifyDataSetChanged();
              PushReceiver.notifyBidOnItem(ItemListActivity.this, item.getObjectId());
              data.fetchAllItems();

              // Since it's about to not have no bids.
              if (listQuery.equals(DataManager.QUERY_NOBIDS))
                Toast.makeText(ItemListActivity.this, "Bid for $" + amt + " on " + item.getName() + " placed!", Toast.LENGTH_LONG).show();

              if (!placed)
                Toast.makeText(ItemListActivity.this, "Looks like you were outbid! Try again at the new price.", Toast.LENGTH_LONG).show();
            }
          }, ItemListActivity.this);
        }
      });

      v.cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View vi) {
          reshowBidButtons.run();
        }
      });
    }



    public void animateCardEntry(CardViewHolder v, int pos) {
      int baseStartDelay = 250;

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


  public void setupDrawer() {
    // ActionBarDrawerToggle is responsible for the menu indicator next to the icon, as well as making
    // the logo area open the drawer
    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.app_name, R.string.app_name) {
      @Override
      public void onDrawerClosed(View view) {}

      @Override
      public void onDrawerOpened(View drawerView) {}

      @Override
      public void onDrawerSlide(View v, float amt) {
        super.onDrawerSlide(v, amt);
      }
    };

    mDrawerLayout.setDrawerListener(mDrawerToggle);}

  public void setupMenu() {
    userEmail.setText(IdentityManager.getEmail(this));

    all.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        listActivity.putExtra("title", "All Items");
        listActivity.putExtra("query", DataManager.QUERY_ALL);
        startActivity(listActivity);
      }
    });

    myItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        listActivity.putExtra("title", "My Items");
        listActivity.putExtra("query", DataManager.QUERY_MINE);
        startActivity(listActivity);
      }
    });

    noBids.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        listActivity.putExtra("title", "Items with No Bids");
        listActivity.putExtra("query", DataManager.QUERY_NOBIDS);
        startActivity(listActivity);
      }
    });

    logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AlertDialog dialog;
        AlertDialog.Builder alert = new AlertDialog.Builder(ItemListActivity.this);

        alert.setTitle("Log Out");
        alert.setMessage("You sure?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            IdentityManager.setEmail("", ItemListActivity.this);
            IdentityManager.setName("", ItemListActivity.this);
            finish();
          }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {

          }
        });

        dialog = alert.show();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_search) {
      AlertDialog dialog;
      AlertDialog.Builder alert = new AlertDialog.Builder(ItemListActivity.this);

      alert.setTitle("Search");
      alert.setMessage("What are you looking for? (Searching in name, donor name, and item description)");

      final EditText input = new EditText(ItemListActivity.this);
      input.setPadding(DisplayUtils.toPx(20), DisplayUtils.toPx(20), DisplayUtils.toPx(20), DisplayUtils.toPx(20));
      input.setHint("search query");
      input.setBackgroundDrawable(null);
      alert.setView(input);

      alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
          listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          listActivity.putExtra("title", "Search: " + input.getText().toString());
          listActivity.putExtra("query", input.getText().toString());
          startActivity(listActivity);
        }
      });

      alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {

        }
      });

      dialog = alert.show();
    }
    else if (item.getItemId() == R.id.menu_refresh) {
      Toast.makeText(this, "Refreshing prices...", Toast.LENGTH_SHORT).show();
      data.fetchAllItems(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(getApplicationContext(), "Everything's up to date!", Toast.LENGTH_LONG).show();
          adapter.notifyDataSetChanged();
        }
      });
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.mainmenu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDrawerToggle.syncState();
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    data.beginBidCoverage(this);
    PushReceiver.clearAll();

    if (IdentityManager.getEmail(this).length() < 5) {
      startActivity(new Intent(this, LoginActivity.class));
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    data.endBidCoverage();
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }
}
