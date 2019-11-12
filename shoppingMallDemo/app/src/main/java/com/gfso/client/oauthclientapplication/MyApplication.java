package com.gfso.client.oauthclientapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.gfso.client.oauthclientapplication.bean.User;
import com.gfso.client.oauthclientapplication.util.UserLocalData;
import com.lzy.ninegrid.NineGridView;
import com.mob.MobSDK;
import com.squareup.picasso.Picasso;

/**
 * Created by 博 on 2017/8/23.
 */

public class MyApplication extends Application {

    public static final int START_FOR_RESULT  = 0 ;
    public static final int START_NO_RESULT  = 1 ;

    private User user ;
    private String token ;
    private static Intent intent ;
    private static  int startIntentStype ;

//    public static int getStartIntentStype() {
//        return startIntentStype;
//    }
//
//    public static void setStartIntentStype(int startIntentStype) {
//        MyApplication.startIntentStype = startIntentStype;
//    }

//    public Intent getIntent() {
//        return intent;
//    }
//
//    public void setIntent(Intent intent) {
//        this.intent = intent;
//    }

    private static MyApplication myApplication;

    public static MyApplication getInstance(){
        return myApplication ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.myApplication = this ;
        initUser();
        Fresco.initialize(this);
        NineGridView.setImageLoader(new PicassoImageLoader());
        MobSDK.init(this);
    }

    public User getUser(){
        return user ;
    }

    public String getToken(){
        return token ;
    }

    public void putUser(User user , String token){

        this.token = token ;
        this.user = user ;
        UserLocalData.putUser(this , user);
        UserLocalData.putToken(this , token);
    }

    public void clearUser(){
        this.user = null ;
        this.token = null ;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
    }

    public void initUser(){
        this.user = UserLocalData.getUser(this );
        this.token = UserLocalData.getToken(this) ;
    }
//    public static void jumpToTargetoActivity(Activity activity){
//        activity.startActivity(intent);
//        intent = null ;
//    }


    /** Picasso 加载 */
    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Picasso.with(context).load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
}
