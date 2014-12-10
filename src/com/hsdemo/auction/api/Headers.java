package com.hsdemo.auction.api;

import org.apache.http.Header;
import org.apache.http.HeaderElement;

/**
 * Created by jtsuji on 11/18/14.
 */
public class Headers {
  public static Header APP_NAME = new Header() {
    @Override
    public String getName() {
      return "X-Parse-Application-Id";
    }

    @Override
    public String getValue() {
      return "JPa6xJnjNpZr7wkueoaw4IXb9YEwY0XUyiAP818I";
    }

    @Override
    public HeaderElement[] getElements() throws org.apache.http.ParseException {
      return new HeaderElement[0];
    }
  };

  public static Header APP_KEY = new Header() {
    @Override
    public String getName() {
      return "X-Parse-REST-API-Key";
    }

    @Override
    public String getValue() {
      return "hR4dxJwHiX4N9Y5aGF9M35uuSTT13UiN4E2edjK8";
    }

    @Override
    public HeaderElement[] getElements() throws org.apache.http.ParseException {
      return new HeaderElement[0];
    }
  };
}
