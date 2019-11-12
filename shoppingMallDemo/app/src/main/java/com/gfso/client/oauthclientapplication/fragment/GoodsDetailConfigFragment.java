package com.gfso.client.oauthclientapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.GoodsDetailConfigBean;
import com.gfso.client.oauthclientapplication.fragment.recycleview.GoodsDetailConfigAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoodsDetailConfigFragment extends Fragment {
    @BindView(R.id.lv_config)
    ListView lv_config;

    AppCompatActivity activity = null;

    public GoodsDetailConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goods_detail_config, container, false);
        activity = (AppCompatActivity)this.getActivity();
        ButterKnife.bind(this, view);

        lv_config.setFocusable(false);

        List<GoodsDetailConfigBean> data = new ArrayList<>();
        data.add(new GoodsDetailConfigBean("品牌", "Letv/乐视"));
        data.add(new GoodsDetailConfigBean("型号", "LETV体感-超级枪王"));
        lv_config.setAdapter(new GoodsDetailConfigAdapter(activity, data));

        return view;
    }

}
