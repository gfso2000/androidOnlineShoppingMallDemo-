package com.gfso.client.oauthclientapplication.okhttp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.fragment.activity.LoginActivity;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HttpLoadingDialog<T> extends BaseCallback<T> {

    private SpotsDialog spotsDialog ;
    private Context mContext ;

    public HttpLoadingDialog(Context context){

        spotsDialog = new SpotsDialog(context) ;
        mContext = context ;
    }

    public void showSpotsDialog(){
        spotsDialog.show();
    }

    public void closeSpotsDialog(){
        spotsDialog.cancel();
    }

    @Override
    public void onRequestBefore() {
        showSpotsDialog() ;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        closeSpotsDialog();
    }

    @Override
    public void onTokenError(Response response, int responseCode) {

        Toast.makeText(mContext , "TokenError" , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext , LoginActivity.class);
        mContext.startActivity(intent);

        MyApplication.getInstance().clearUser();
    }
}
