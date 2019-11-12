package com.gfso.client.oauthclientapplication.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtil {
    static Gson gson = new Gson() ;

    public static  <T> T fromJson(String json , Type type){
        return gson.fromJson(json , type) ;
    }

    public static String toJSON(Object obj){
        return gson.toJson(obj) ;
    }
}
