package com.gfso.client.oauthclientapplication.fragment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.gfso.client.oauthclientapplication.fragment.widget.MyCityPicker;
import com.gfso.client.oauthclientapplication.fragment.widget.MyCityPickerDialogFragment;
import com.gfso.client.oauthclientapplication.util.Contents;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

public class ChooseCityActivity extends AppCompatActivity implements MyCityPickerDialogFragment.OnCancelListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        showCityPicker();
    }

    private void showCityPicker() {
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));

        MyCityPicker.getInstance()
                .setFragmentManager(this.getSupportFragmentManager())	//此方法必须调用
                .enableAnimation(true)	//启用动画效果
                //.setAnimationStyle(anim)	//自定义动画
                .setLocatedCity(new LocatedCity("杭州", "浙江", "101210101")) //APP自身已定位的城市，默认为null（定位失败）
                .setHotCities(hotCities)	//指定热门城市
                .setOnCancelListener(this)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        //Toast.makeText(ChooseCityActivity.this, data.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(Contents.CHOOSE_CITY_VALUE, data == null ? "" : data.getName());
                        ChooseCityActivity.this.setResult(RESULT_OK, intent);
                        ChooseCityActivity.this.finish();
                    }

                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //定位完成之后更新数据
                                CityPicker.getInstance()
                                        .locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
                            }
                        }, 2000);
                    }
                })
                .show();
    }

    @Override
    public void onCancel() {
        this.finish();
    }
}
