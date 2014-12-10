package com.hsdemo.auction.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by jtsuji on 11/14/14.
 */

@ParseClassName("NewBid")
public class Bid extends ParseObject {

  public static final String INITIAL_BIDDER_EMAIL = "";
  public static final String INITIAL_BIDDER_NAME = "Starting Price";

  public long createdAt = 0;

  public Bid() {}

  public static Bid getInitialBid(int price) {
    Bid bid = new Bid();
    bid.setEmail(INITIAL_BIDDER_EMAIL);
    bid.setName(INITIAL_BIDDER_NAME);
    bid.setAmount(price);
    return bid;
  }

  public Date getCreatedAtDate() {
    return getCreatedAt() == null ? new Date(createdAt) : getCreatedAt();
  }

  public String getEmail() {
    return getString("email");
  }

  public void setEmail(String email) {
    put("email", email);
  }

  public int getAmount() {
    return getInt("amt");
  }

  public void setAmount(int amt) {
    put("amt", amt);
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String name) {
    put("name", name);
  }

  public String getRelatedItemId() {
    return getString("item");
  }

  public void setRelatedItemId(String id) {
    put("item", id);
  }
}
