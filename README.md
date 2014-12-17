# BidHub Android
Android client for HubSpot's open-source silent auction app. For an overview of the auction app project, [check out our blog post about it](http://dev.hubspot.com/blog/building-an-auction-app-in-a-weekend)!

![](http://i.imgur.com/qIud2uSl.png)

## Getting started
If you haven't yet, you're going to want to set up Parse by following the instructions in the [BidHub Cloud Code repository](https://github.com/HubSpot/BidHub-CloudCode). All set? `git clone` this repository and import it into Android Studio. Grab your application ID and client key from Parse (Parse > Settings > Keys). Set `APP_ID` and `CLIENT_KEY` in *AuctionApplication.java* to these values. 

Then just run the app, enter your name and email address, and you should see Test Object 7! Bid on it and see what happens. To keep an eye on the action, check out the [Web Panel](https://github.com/HubSpot/BidHub-WebAdmin) where you can see all your items and bids.

## Customization
Here's a list of the HubSpot-specific assets in the app, which you can change to whatever you want:
* `drawable/notificationicon.png` status bar icon for push notifications
* `drawable/appicon.png` app icon for the app drawer and for push notifications
* `drawable/bg.png` background for the login screen and for the hamburger menu

## Push
If you change the package name from the default `com.hsdemo.auction`, make sure to change the following manifest tags as well, or push notifications won't work and you will be very frustrated:
* `<category android:name="com.hsdemo.auction" />`
* `<permission android:protectionLevel="signature" android:name="com.hsdemo.auction.permission.C2D_MESSAGE" />`
* `<uses-permission android:name="com.hsdemo.auction.permission.C2D_MESSAGE" />`
