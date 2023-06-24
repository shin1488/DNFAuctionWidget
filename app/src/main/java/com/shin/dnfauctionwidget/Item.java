package com.shin.dnfauctionwidget;

public class Item {
    private String imageUrl;
    private String text;
    private String itemId;

    public Item(String imageUrl, String text, String itemId) {
        this.imageUrl = imageUrl;
        this.text = text;
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getItemId() { return itemId; }
}
