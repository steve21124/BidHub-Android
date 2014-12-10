# HubSpot Charity Auction App
Somewhat similar to the competition, except it doesn't cost $6,000 and isn't terrible.

![screenshot_2014-11-16-23-06-29](https://git.hubteam.com/github-enterprise-assets/0000/0196/0000/6519/b175cc6c-6e0f-11e4-9310-b50965c45993.png)

## Data Models

### Item

Represents a thing or service for sale. Don't add these via the app, add them using Parse or the web interface.

 * `name` Item name (Drive JD's Tesla)
 * `description` Long-form description (You get to wreck JD's Tesla and he can't be mad)
 * `donorname` Who donated this item (JD Sherman)
 * `price` 150 (starting price - current price will be determined via the Bids table)
 * `imageurl` Direct link to a reasonably sized image

### Bid
Represents a single bid on an item. 

 * `item` objectId of item this bid is for
 * `name` Bidder's name
 * `email` Bidder's email (unique ID)
 * `amt` total dollar amount of bid
 
### Push
Pushes are sent by clients whenever they make a confirmed high bid. 
 * `objectId` of the bid
 * `amt` - total amount of the new high bid
 * `email` - email address
 * `name` - item name (for ease of notification rendering)
 * `item` - item objectId
 
## Usage
 
### Current Price
Find the current price by searching for `Bid`s where `objectID` == `bid.item` and finding the maximum `amt`.

### Bidding
Insert a `Bid` into the `NewBid` table. Before firing a bid, refresh the current price in case it has changed. After a bid insert returns successfully, check all `Bids` on that item before confirming the bid to the user - if someone else has an equal-amount bid with an earlier timestamp, message that to the user.
