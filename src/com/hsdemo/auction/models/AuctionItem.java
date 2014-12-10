package com.hsdemo.auction.models;

import android.app.Activity;

import com.hsdemo.auction.IdentityManager;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by jtsuji on 11/14/14.
 */

@ParseClassName("Item")
public class AuctionItem extends ParseObject {

  public AuctionItem() {}

  public String getName() {
    return getString("name");
  }

  public void setName(String name) {
    put("name", name);
  }

  public String getDescription() {
    return getString("description");
  }

  public void setDescription(String description) {
    put("description", description);
  }

  public String getDonorName() {
    return getString("donorname");
  }

  public void setDonorName(String donorName) {
    put("donorname", donorName);
  }

  public String getImageUrl() {
    return getString("imageurl");
  }

  public void setImageUrl(String imageUrl) {
    put("imageurl", imageUrl);
  }

  public int getStartingPrice() {
    return getInt("price");
  }

  public void setStartingPrice(int startingPrice) {
    put("price", startingPrice);
  }

  public Date getOpenTime() {
    return getDate("opentime");
  }

  public Date getCloseTime() {
    return getDate("closetime");
  }

  public boolean isOpenForBidding() {
    return !(getOpenTime().after(new Date()) || getCloseTime().before(new Date()));
  }

  public List<Integer> getAllBids() {
    List<Integer> bids = getListOrEmptyList("currentPrice");
    Collections.sort(bids, new Comparator<Integer>() {
      @Override
      public int compare(Integer lhs, Integer rhs) {
        return rhs - lhs;
      }
    });

    return bids;
  }

  public List<String> getCurrentWinners() {
    return getListOrEmptyList("currentWinners");
  }

  public int getNumberOfBids() {
    return getInt("numberOfBids");
  }

  public int getQty() {
    return getInt("qty");
  }

  public int getCurrentHighestBid() {
    return getAllBids().size() > 0 ? getAllBids().get(0) : getStartingPrice();
  }

  public int[] getLowHighWinningBid() {
    List<Integer> allBids = getAllBids();
    if (allBids.size() > 0) {
      return new int[]{allBids.get(allBids.size() - 1), allBids.get(0)};
    }
    else {
      return new int[]{getStartingPrice()};
    }
  }

  public List<String> getAllBidders() {
    return getListOrEmptyList("allBidders");
  }

  public boolean hasUserBid(Activity context) {
    return getAllBidders().contains(IdentityManager.getEmail(context));
  }

  public boolean isWinning(Activity context) {
    return getCurrentWinners().contains(IdentityManager.getEmail(context));
  }

  public int getMyBidWinningIdx(Activity context) {
    return getCurrentWinners().indexOf(IdentityManager.getEmail(context)) + 1;
  }

  public List getListOrEmptyList(String key) {
    List list = getList(key);
    if (list == null)
      return new ArrayList();
    else
      return list;
  }
}
