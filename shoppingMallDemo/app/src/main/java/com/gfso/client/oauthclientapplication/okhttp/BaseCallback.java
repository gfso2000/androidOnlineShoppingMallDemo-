package com.gfso.client.oauthclientapplication.okhttp;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 博 on 2017/8/22.
 */

public abstract class BaseCallback<T> {
    public Type type ;

    static Type getSuperclassTypeParameter(Class<?> subclass ){
        Type superClass = subclass.getGenericSuperclass() ;
        if (superClass instanceof Class){
            throw new RuntimeException("Missing type parameter") ;
        }
        ParameterizedType parameterizedType = (ParameterizedType) superClass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]) ;
    }



    public BaseCallback(){
        this.type = getSuperclassTypeParameter(this.getClass()) ;
    }

    public abstract void onRequestBefore();
    public abstract void onFailure(Request request, IOException e) ;
    public abstract void onError(Response response , int responseCode , Exception e) throws IOException;
    public abstract void callBackSucces(Response response , T t) throws IOException;
    public abstract void onTokenError(Response response , int responseCode );

}
