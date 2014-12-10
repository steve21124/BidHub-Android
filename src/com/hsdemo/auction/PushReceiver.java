package com.hsdemo.auction;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.hsdemo.auction.api.DataManager;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jtsuji on 11/14/14.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

  static ArrayList<OutbidNotification> allNotifications = new ArrayList<OutbidNotification>();
  static Notification currentNotification;

  static DataManager data = DataManager.getInstance();

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
  }

  public static void notifyBidOnItem(Context context, String itemId) {
    boolean notificationChanged = false;

    ArrayList<OutbidNotification> newNotifications = new ArrayList<OutbidNotification>();
    for (OutbidNotification notification : allNotifications) {
      if (notification.itemId.equals(itemId))
        notificationChanged = true;

      if (!notification.itemId.equals(itemId))
        newNotifications.add(notification);
    }

    if (notificationChanged) {
      allNotifications.clear();
      allNotifications.addAll(newNotifications);

      redisplayNotifications(context);
    }
  }

  @Override
  protected Notification getNotification(Context context, Intent intent) {
    getAndShowNotification(context, intent);
    return null;
  }

  protected static Notification getAndShowNotification(Context context, Intent intent) {
    try {
      JSONObject extras = new JSONObject(intent.getExtras().getString("com.parse.Data"));
      Log.i("TEST", "alert: " + extras.toString(1));
      // Generic alert
      if (!extras.has("itemname") && !extras.has("personname") && extras.has("alert")) {
        Log.i("TEST", "got push alert");

        Intent bidUp = new Intent(context, ItemListActivity.class);
        bidUp.putExtra("query", DataManager.QUERY_MINE);
        bidUp.putExtra("title", "My Items");
        PendingIntent bidUpPending = PendingIntent.getActivity(context, 0, bidUp, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notificationicon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.appicon))
                .setAutoCancel(true)
                .setTicker("Message from Auction App")
                .setContentTitle("Auction App")
                .setContentIntent(bidUpPending)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(extras.getString("alert")))
                .setContentText(extras.getString("alert"));



        android.support.v4.app.NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notificationAlert = mBuilder.build();
        notificationManager.notify(1, notificationAlert);
        return notificationAlert;
      }

      ArrayList<OutbidNotification> newNotifications = new ArrayList<OutbidNotification>();

      for (OutbidNotification notification : allNotifications) {
        if (!notification.itemId.equals(extras.getString("itemid")))
          newNotifications.add(notification);
      }

      OutbidNotification thisNotification = new OutbidNotification(extras);
      newNotifications.add(thisNotification);

      allNotifications.clear();
      allNotifications.addAll(newNotifications);

      redisplayNotifications(context);
      data.fetchAllItems();
    }
    catch (Exception e) {
      e.printStackTrace();

      android.support.v4.app.NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
              .setSmallIcon(R.drawable.notificationicon)
              .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.appicon))
              .setAutoCancel(true)
              .setTicker("Message from Auction App")
              .setContentTitle("Auction App")
              .setContentText(intent.getExtras().getString("com.parse.Data"));

      currentNotification = mBuilder.build();
      if (currentNotification != null) {
        notificationManager.notify(0, currentNotification);
      }
    }

    return null;
  }

  private static void redisplayNotifications(Context context) {
    android.support.v4.app.NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancel(0);

    Intent bidUp = new Intent(context, ItemListActivity.class);
    bidUp.putExtra("query", DataManager.QUERY_MINE);
    bidUp.putExtra("title", "My Items");
    PendingIntent bidUpPending = PendingIntent.getActivity(context, 0, bidUp, PendingIntent.FLAG_ONE_SHOT);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.notificationicon)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.appicon))
            .setAutoCancel(true)
            .setTicker("You've been outbid!");

    if (allNotifications.size() == 0) {
      return;
    }
    else if (allNotifications.size() == 1) {
      OutbidNotification singleNotification = allNotifications.get(0);
      String text = String.format("%s doesn't want you to have this, and bid $" + singleNotification.amt + ". Surely you won't stand for this.", singleNotification.personName);

      mBuilder.setNumber(1)
              .setContentTitle(singleNotification.itemName)
              .setContentText(text)
              .setStyle(new NotificationCompat.BigTextStyle().bigText(text));
      /*mBuilder.addAction(android.R.drawable.ic_menu_add, "$1", bidUpPending);
      mBuilder.addAction(android.R.drawable.ic_menu_add, "$5", bidUpPending);
      mBuilder.addAction(android.R.drawable.ic_menu_add, "$10", bidUpPending);*/
      mBuilder.setContentIntent(bidUpPending);
    }
    else {
      mBuilder.setNumber(allNotifications.size())
              .setContentTitle("Outbid on " + allNotifications.size() + " items")
              .setContentIntent(bidUpPending)
              .setContentText("Surely you won't stand for this.");
      NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

      inboxStyle.setBigContentTitle("Outbid on " + allNotifications.size() + " items");
      inboxStyle.setSummaryText("Auction");

      // Add each form submission to the inbox list
      for (int i = 0; i < allNotifications.size(); i++) {
        inboxStyle.addLine("$" + allNotifications.get(i).amt + " by " + allNotifications.get(i).personName + " on " + allNotifications.get(i).itemName);
      }

      mBuilder.setStyle(inboxStyle);

    }

    currentNotification = mBuilder.build();
    if (currentNotification != null) {
      notificationManager.notify(0, currentNotification);
    }
  }

  public Spanned getWhiteGrayTextLine(String white, String gray) {
    return Html.fromHtml("<font color='#ffffff'>" + white + "</font>  " + gray);
  }

  public static class OutbidNotification {
    public String email;
    public String itemName;
    public String itemId;
    public int amt;
    public String personName;

    public OutbidNotification(JSONObject object) {
      try {
        Log.i("TEST", object.toString());
        this.email = object.getString("email");
        this.itemName = object.getString("itemname");
        this.itemId = object.getString("itemid");
        this.amt = object.getInt("amt");
        this.personName = object.getString("personname");
        if (this.personName.length() == 0) {
          this.personName = this.email;
          if (this.email.contains("@"))
            this.personName = this.email.split("@")[0];
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        Log.i("TEST", "serialization error");
      }
    }
  }

  public static void clearAll() {
    if (allNotifications != null)
      allNotifications.clear();
  }
}
