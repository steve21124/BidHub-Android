package com.hubspot.disruption.auction.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by jtsuji on 11/14/14.
 */

@ParseClassName("NewBid")
public class Bid extends ParseObject {

  public Bid() {}

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
