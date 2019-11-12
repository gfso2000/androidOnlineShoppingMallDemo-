package com.gfso.client.oauthclientapplication.util;

import android.content.Context;
import android.util.Log;

import com.gfso.client.oauthclientapplication.bean.ShoppingCartItem;
import com.gfso.client.oauthclientapplication.bean.Ware;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartProvider {

    private Map<String, ShoppingCartItem> map = new HashMap<>() ;
    private Context context = null ;
    private static CartProvider cartProvider ;

    private CartProvider(Context context) {
        this.context = context ;
        List<ShoppingCartItem> carts = getFromLocal();
        if ( carts != null && carts.size() > 0 ){
            for (ShoppingCartItem cart : carts){
                map.put(cart.getId(), cart);
            }
        }
    }

    public static CartProvider getCartProvider(Context context){
        if(cartProvider == null){
            cartProvider = new CartProvider(context) ;
        }
        return cartProvider ;
    }

    public void add(ShoppingCartItem cart){
        ShoppingCartItem temp = map.get(cart.getId()) ;
        Log.d("------ ", " "+cart.getId()) ;

        if (temp != null){
            temp.setCount(temp.getCount() + 1);
        }else {
            temp = cart ;
        }

        map.put(cart.getId(), temp);
        saveToLocal() ;
    }

    public void add(Ware ware){
        ShoppingCartItem shoppingCart = ware.toShoppingCart() ;
        this.add(shoppingCart);
    }

    public void replace(ShoppingCartItem cart){
        map.put(cart.getId(), cart);
        saveToLocal() ;
    }

    public void delete(ShoppingCartItem cart){
        map.remove(cart.getId());
        saveToLocal() ;
    }

    public List<ShoppingCartItem> getAll(){
        return getFromLocal() ;
    }

    private void saveToLocal(){
        List<ShoppingCartItem> carts = new ArrayList<ShoppingCartItem>();
        carts.addAll(map.values());
        PreferenceUtil.putString(context ,PreferenceUtil.SHOPPING_CART, JsonUtil.toJSON(carts));
    }

    private List<ShoppingCartItem> getFromLocal(){
        String json = PreferenceUtil.getString(context ,PreferenceUtil.SHOPPING_CART, null ) ;
        Log.d("----" , "---"+json) ;
        List<ShoppingCartItem> carts = new ArrayList<>() ;
        if (json != null && json.length() > 0 ){
            carts = JsonUtil.fromJson(json , new TypeToken<List<ShoppingCartItem>>(){}.getType()) ;
        }
        return carts ;
    }
}
