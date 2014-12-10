package com.hubspot.disruption.auction.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jtsuji on 11/14/14.
 */

@ParseClassName("Item")
public class AuctionItem extends ParseObject {

  int currentHighestBid = 0;
  int myBid = 0;

  public int getCurrentHighestBid() {
    return currentHighestBid;
  }

  public void setCurrentHighestBid(int currentHighestBid) {
    this.currentHighestBid = currentHighestBid;
  }

  public int getMyBid() {
    return myBid;
  }

  public void setMyBid(int myBid) {
    this.myBid = myBid;
  }

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
}
