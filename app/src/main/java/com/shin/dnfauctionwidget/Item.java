package com.shin.dnfauctionwidget;

public class Item {
    private String imageUrl;
    private String text;

    public Item(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }
}
