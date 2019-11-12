package com.gfso.client.oauthclientapplication.util;

import android.content.Context;
import android.util.Log;

import com.gfso.client.oauthclientapplication.bean.ConsigneeBean;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddressProvider {

    private Map<String, ConsigneeBean> map = new HashMap<>() ;
    private Context context = null ;
    private static AddressProvider addressprovider;

    private AddressProvider(Context context) {
        this.context = context ;
        List<ConsigneeBean> addresses = getFromLocal();
        if ( addresses != null && addresses.size() > 0 ){
            for (ConsigneeBean address : addresses){
                map.put(address.getId(), address);
            }
        }
    }

    public static AddressProvider getAddressProvider(Context context){
        if(addressprovider == null){
            addressprovider = new AddressProvider(context) ;
        }
        return addressprovider;
    }

    public String update(ConsigneeBean address){
        if(address.getId() == null){
            address.setId(UUID.randomUUID().toString());
        }
        map.put(address.getId(), address);
        saveToLocal() ;
        return address.getId();
    }

    public void delete(ConsigneeBean address){
        map.remove(address.getId());
        saveToLocal() ;
    }

    public List<ConsigneeBean> getAll(){
        return getFromLocal() ;
    }

    private void saveToLocal(){
        List<ConsigneeBean> carts = new ArrayList<ConsigneeBean>();
        carts.addAll(map.values());
        PreferenceUtil.putString(context ,PreferenceUtil.ADDRESSES, JsonUtil.toJSON(carts));
    }

    private List<ConsigneeBean> getFromLocal(){
        String json = PreferenceUtil.getString(context ,PreferenceUtil.ADDRESSES, null ) ;
        Log.d("----" , "---"+json) ;
        List<ConsigneeBean> addresses = new ArrayList<>() ;
        if (json != null && json.length() > 0 ){
            addresses = JsonUtil.fromJson(json , new TypeToken<List<ConsigneeBean>>(){}.getType()) ;
        }
        return addresses ;
    }
}
