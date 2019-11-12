package com.gfso.client.oauthclientapplication.fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.fragment.activity.AddressActivity;
import com.gfso.client.oauthclientapplication.fragment.activity.ChooseCityActivity;
import com.gfso.client.oauthclientapplication.fragment.activity.LoginActivity;
import com.gfso.client.oauthclientapplication.fragment.activity.OrderActivity;
import com.gfso.client.oauthclientapplication.fragment.widget.MyCityPicker;
import com.gfso.client.oauthclientapplication.util.Contents;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class MeFragment extends Fragment {
    AppCompatActivity activity = null;
    @BindView(R.id.tb_user_photo)
    ImageView photoView;
    @BindView(R.id.tb_user_name)
    TextView userIdView;
    @BindView(R.id.my_address)
    TextView addressView;
    @BindView(R.id.my_city)
    TextView cityView;
    @BindView(R.id.my_list)
    TextView orderView;
    @BindView(R.id.my_favorite)
    TextView my_favorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        activity = (AppCompatActivity)this.getActivity();
        ButterKnife.bind(this, view);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity , LoginActivity.class) ;
                startActivityForResult(intent , Contents.LOGIN_REQUEST);
            }
        });
        addressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity , AddressActivity.class) ;
                startActivity(intent);
            }
        });
        orderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity , OrderActivity.class) ;
                startActivity(intent);
            }
        });
        cityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity , ChooseCityActivity.class) ;
                startActivityForResult(intent , Contents.CHOOSE_CITY_REQUEST);
            }
        });
        my_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMsg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", "微信",
                        "title", "text", 1, getUriFromDrawableRes(activity, R.drawable.album1));
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Contents.LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle res = data.getExtras();
                String result = res.getString(Contents.LOGIN_USERID);
                userIdView.setText(result);
                Toast.makeText(activity,"login success:"+result,Toast.LENGTH_LONG);
            }
        } else if (requestCode == Contents.CHOOSE_CITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle res = data.getExtras();
                String result = res.getString(Contents.CHOOSE_CITY_VALUE);
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareMsg(String packageName, String activityName,
                          String appname, String msgTitle, String msgText, int type,
                          Uri drawableUri) {
        if (!packageName.isEmpty() && !isAvailable(activity, packageName)) {// 判断APP是否存在
            Toast.makeText(activity, "请先安装" + appname, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent("android.intent.action.SEND");
        if (type == 0) {
            intent.setType("text/plain");
        } else if (type == 1) {
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, drawableUri);
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!packageName.isEmpty()) {
            intent.setComponent(new ComponentName(packageName, activityName));
            activity.startActivity(intent);
        } else {
            activity.startActivity(Intent.createChooser(intent, msgTitle));
        }
    }

    public boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public Uri getUriFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }
}
