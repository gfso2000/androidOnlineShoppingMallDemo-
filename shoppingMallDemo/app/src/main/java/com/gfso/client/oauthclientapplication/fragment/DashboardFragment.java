package com.gfso.client.oauthclientapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gfso.client.oauthclientapplication.CaptureActivityPortrait;
import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.CategoryBean;
import com.gfso.client.oauthclientapplication.fragment.task.ScanLoginTask;
import com.gfso.client.oauthclientapplication.fragment.recycleview.CategoryAdapter;
import com.gfso.client.oauthclientapplication.fragment.recycleview.SubcategoryGroupAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends Fragment {
    @BindView(R.id.head_scan_search_notification_layout_entire)
    View headView;
    @BindView(R.id.head_scan_search_notification_btn_search)
    Button searchButton;
    @BindView(R.id.scanbtn)
    TextView scanButton;
    @BindView(R.id.notificationbtn)
    TextView notificationButton;
    @BindView(R.id.head_scan_search_notification_layout_scan)
    LinearLayout scanLayout;

    AppCompatActivity activity = null;
    @BindView(R.id.lv_category)
    ListView lv_category;
    @BindView(R.id.lv_subcategory_group)
    ListView lv_subcategory_group;
    @BindView(R.id.tv_subcategory_group_title)
    TextView tv_subcategory_group_title;

    private List<String> categoryList = new ArrayList<>();
    private List<CategoryBean.DataBean> homeList = new ArrayList<>();
    private List<Integer> showTitle;

    private CategoryAdapter categoryAdapter;
    private SubcategoryGroupAdapter subcategoryGroupAdapter;
    private int currentItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        activity = (AppCompatActivity)this.getActivity();
        Fresco.initialize(activity);
        ButterKnife.bind(this, view);
        blackScanSearchNotificationButton();
        initHeader();
        initView();
        loadData();
        return view;
    }

    private void blackScanSearchNotificationButton() {
        headView.setBackgroundColor(Color.argb(255, 255, 255, 255));
        searchButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        scanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        notificationButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    private void initHeader(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Hello Dashboard", Toast.LENGTH_SHORT).show();
            }
        });

        final Fragment me = this;
        scanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(me);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    private void initView() {
        categoryAdapter = new CategoryAdapter(activity, categoryList);
        lv_category.setAdapter(categoryAdapter);

        subcategoryGroupAdapter = new SubcategoryGroupAdapter(activity, homeList);
        lv_subcategory_group.setAdapter(subcategoryGroupAdapter);

        lv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryAdapter.setSelectItem(position);
                categoryAdapter.notifyDataSetInvalidated();
                tv_subcategory_group_title.setText(categoryList.get(position));
                lv_subcategory_group.setSelection(showTitle.get(position));
            }
        });


        lv_subcategory_group.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    return;
                }
                int current = showTitle.indexOf(firstVisibleItem);
//				lv_subcategory_group.setSelection(current);
                if (currentItem != current && current >= 0) {
                    currentItem = current;
                    tv_subcategory_group_title.setText(categoryList.get(currentItem));
                    categoryAdapter.setSelectItem(currentItem);
                    categoryAdapter.notifyDataSetInvalidated();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String urlStr = result.getContents();
                try {
                    //have to call http in another thread, otherwise it will throw exception(networkOnMainThread)
                    ScanLoginTask task = new ScanLoginTask(activity);
                    String userId = MyApplication.getInstance().getUser().getUsername();
                    String token = MyApplication.getInstance().getToken();
                    task.execute(urlStr, userId, token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadData() {
        String json = getJson(activity, "category.json");
        CategoryBean categoryBean = JSONObject.parseObject(json, CategoryBean.class);
        showTitle = new ArrayList<>();
        for (int i = 0; i < categoryBean.getData().size(); i++) {
            CategoryBean.DataBean dataBean = categoryBean.getData().get(i);
            categoryList.add(dataBean.getModuleTitle());
            showTitle.add(i);
            homeList.add(dataBean);
        }
        tv_subcategory_group_title.setText(categoryBean.getData().get(0).getModuleTitle());

        categoryAdapter.notifyDataSetChanged();
        subcategoryGroupAdapter.notifyDataSetChanged();
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
