package com.shin.dnfauctionwidget;

public class Item {
    private String imageUrl;
    private String itemName;
    private String itemId;

    public Item(String imageUrl, String itemName, String itemId) {
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemId() { return itemId; }
}
