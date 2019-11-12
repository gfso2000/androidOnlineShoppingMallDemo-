package com.gfso.client.oauthclientapplication.bean;

import java.io.Serializable;

public class Ware implements Serializable {

    protected String id ;
    protected int categoryId ;
    protected int campaignId ;
    protected String name ;
    protected String imgUrl ;
    protected String price ;
    protected int sale ;

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ShoppingCartItem toShoppingCart(){

        ShoppingCartItem cart = new ShoppingCartItem() ;

        cart.setId(this.id);
        cart.setCount(1);
        cart.setChecked(true);
        cart.setImgUrl(this.imgUrl);
        cart.setName(this.name);
        cart.setPrice(this.price);

        return cart ;
    }

}
