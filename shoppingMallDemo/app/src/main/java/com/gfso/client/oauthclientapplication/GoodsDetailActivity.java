package com.gfso.client.oauthclientapplication;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.fragment.GoodsCommentsFragment;
import com.gfso.client.oauthclientapplication.fragment.GoodsDetailFragment;
import com.gfso.client.oauthclientapplication.fragment.GoodsOverviewFragment;
import com.gfso.client.oauthclientapplication.fragment.recycleview.GoodsDetailTitleAdapter;
import com.gfso.client.oauthclientapplication.fragment.widget.CustomViewPager;
import com.gxz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsDetailActivity extends AppCompatActivity {
    @BindView(R.id.goods_detail_title_tab)
    public PagerSlidingTabStrip psts_tabs;
    @BindView(R.id.goods_detail_view_pager)
    public CustomViewPager vp_content;
    @BindView(R.id.goods_detail_title)
    public TextView tv_title;
    @BindView(R.id.ll_back)
    LinearLayout ll_back;

    private List<Fragment> fragmentList = new ArrayList<>();
    private GoodsOverviewFragment goodsOverviewFragment;
    private GoodsDetailFragment goodsDetailFragment;
    private GoodsCommentsFragment goodsCommentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);

        fragmentList.add(goodsOverviewFragment = new GoodsOverviewFragment());
        fragmentList.add(goodsDetailFragment = new GoodsDetailFragment());
        fragmentList.add(goodsCommentsFragment = new GoodsCommentsFragment());
        vp_content.setAdapter(new GoodsDetailTitleAdapter(getSupportFragmentManager(),
                fragmentList, new String[]{"商品", "详情", "评价"}));
        vp_content.setOffscreenPageLimit(3);
        vp_content.setPagingEnabled(true);
        psts_tabs.setViewPager(vp_content);

        ll_back.setOnClickListener((v)->{
            finish();
        });
    }
}
