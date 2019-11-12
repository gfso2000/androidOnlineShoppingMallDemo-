package com.gfso.client.oauthclientapplication.fragment.task;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gfso.client.oauthclientapplication.msg.ResponseMsg;
import com.gfso.client.oauthclientapplication.okhttp.HttpLoadingDialog;
import com.gfso.client.oauthclientapplication.okhttp.OkhttpHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class ScanLoginTask extends AsyncTask<String, Void, String> {
    AppCompatActivity activity = null;
    public ScanLoginTask(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            sendPost(params[0],params[1],params[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendPost(String url, String userId, String token) throws Exception {
        OkhttpHelper okhttpHelper = OkhttpHelper.getOkhttpHelper() ;
        Map< String , String > params = new HashMap<String, String>() ;
        params.put("userId" , userId ) ;
        params.put("token" , token ) ;
        okhttpHelper.doJSONPost(url, new HttpLoadingDialog<ResponseMsg>(activity){
            @Override
            public void onError(Response response, int responseCode, Exception e) throws IOException {
                System.out.println(response.toString());
                Toast.makeText(activity, "Response Message : " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void callBackSucces(Response response, ResponseMsg msg) throws IOException {
                System.out.println(response.toString());
                Toast.makeText(activity, "Response Message : " + response, Toast.LENGTH_LONG).show();
            }
        }, params);
    }
}