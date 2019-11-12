package com.gfso.client.oauthclientapplication.bean;

import java.io.Serializable;

public class ShoppingCartItem extends Ware implements Serializable {

    private int count ;
    private Boolean isChecked = true ;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
