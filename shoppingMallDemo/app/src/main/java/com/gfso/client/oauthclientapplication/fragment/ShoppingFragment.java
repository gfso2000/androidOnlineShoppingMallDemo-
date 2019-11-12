package com.gfso.client.oauthclientapplication.fragment;

import android.app.SearchableInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gfso.client.oauthclientapplication.CaptureActivityPortrait;
import com.gfso.client.oauthclientapplication.GoodsDetailActivity;
import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.SearchActivity;
import com.gfso.client.oauthclientapplication.bean.MultiTypeItemBean;
import com.gfso.client.oauthclientapplication.fragment.task.ScanLoginTask;
import com.gfso.client.oauthclientapplication.fragment.recycleview.MultiTypeItemAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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
    @BindView(R.id.shopping_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.shopping_swipe_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.shopping_return_top)
    FloatingActionButton backToTopButton;
    AppCompatActivity activity = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, null);
        activity = (AppCompatActivity)this.getActivity();
        Fresco.initialize(activity);
        ButterKnife.bind(this, view);

        blackScanSearchNotificationButton();
        initHeader();
        Init();
        return view;
    }

    private void Init() {
        final List<MultiTypeItemBean> data = getMultiTypeItemBeanData();
        final MultiTypeItemAdapter multipleItemAdapter = new MultiTypeItemAdapter(data, activity);
        final GridLayoutManager manager = new GridLayoutManager(activity, MultiTypeItemBean.TYPE_SPAN_SIZE_20);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(multipleItemAdapter);
        multipleItemAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return data.get(position).getSpanSize();
            }
        });
        multipleItemAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener(){
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "loaded", Toast.LENGTH_SHORT).show();
                        multipleItemAdapter.addData(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
                        multipleItemAdapter.addData(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
                        multipleItemAdapter.loadMoreComplete();

                        //if no more data, then call loadMoreEnd(), it means, the loadMore will never be triggered
                        //multipleItemAdapter.notifyDataSetChanged();
                        if(multipleItemAdapter.getData().size()>=36){
                            multipleItemAdapter.loadMoreEnd(true);
                            data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_10, MultiTypeItemBean.TYPE_SPAN_SIZE_20));
                        }

                        recyclerView.scrollToPosition(multipleItemAdapter.getData().size() - 1);
                    }
                }, 2000);
            }
        }, recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemClickListener() {

            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(activity, GoodsDetailActivity.class);
                activity.startActivity(intent);
                Toast.makeText(activity, "position:" + Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                int offset = recyclerView.computeVerticalScrollOffset();
                int extent = recyclerView.computeVerticalScrollExtent();
//                int range = recyclerView.computeVerticalScrollRange();
//
//                int percentage = (int)(100.0 * offset / (float)(range - extent));
//                Log.e("offset", offset+":"+extent+":"+range);
//                Log.e("RecyclerView", "scroll percentage: "+ percentage + "%");
                //extend is always the screen height, if scrolled 3 pages height, then show fab
                if (offset > 3 * extent) {
                    backToTopButton.show();
                } else {
                    backToTopButton.hide();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        backToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                backToTopButton.hide();
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, android.R.color.holo_green_dark),
                ContextCompat.getColor(activity, android.R.color.holo_red_dark),
                ContextCompat.getColor(activity, android.R.color.holo_blue_dark),
                ContextCompat.getColor(activity, android.R.color.holo_orange_dark));
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
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
                //add filter demo here
                Intent intent = new Intent(activity, SearchActivity.class);
                startActivity(intent);
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

    private List<MultiTypeItemBean> getMultiTypeItemBeanData() {
        List<MultiTypeItemBean> data = new ArrayList<>();

        //头信息
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_0,InitData(), MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //横分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_2, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //导购栏
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_1, R.mipmap.chaoshi, R.mipmap.chaoshidi, "超市", MultiTypeItemBean.TYPE_SPAN_SIZE_4));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_1, R.mipmap.fushi, R.mipmap.fushidi, "服饰", MultiTypeItemBean.TYPE_SPAN_SIZE_4));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_1, R.mipmap.jiushui, R.mipmap.jiushuidi, "酒水", MultiTypeItemBean.TYPE_SPAN_SIZE_4));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_1, R.mipmap.xinsou, R.mipmap.xinsoudi, "新手", MultiTypeItemBean.TYPE_SPAN_SIZE_4));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_1, R.mipmap.gengduo, R.mipmap.quanbudi, "全部", MultiTypeItemBean.TYPE_SPAN_SIZE_4));

        //横分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_2, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //横幅广告
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_3, R.mipmap.banner, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //头条、新手
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_4, "新手返福利，专享188元大礼包", MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //横分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_2, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //限购、精选、特价
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_5, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //横分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_2, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //今日更新、一元抢购
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_6, R.mipmap.shangpin4, R.mipmap.shangpin4di, "上线抢先看", MultiTypeItemBean.TYPE_SPAN_SIZE_5, "今日更新"));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_6, R.mipmap.shangpin5, R.mipmap.shangpin5di, "一元购电视", MultiTypeItemBean.TYPE_SPAN_SIZE_5, "一元购物"));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_6, R.mipmap.shangpin6, R.mipmap.shangpin6di, "不将就 要好用", MultiTypeItemBean.TYPE_SPAN_SIZE_5, "每日十件"));
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_6, R.mipmap.shangpin7, R.mipmap.shangpin7di, "券购赢豪礼", MultiTypeItemBean.TYPE_SPAN_SIZE_5, "代金券购"));

        //宽分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_7, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //猜你喜欢
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_8, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //横分割线
        data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_2, MultiTypeItemBean.TYPE_SPAN_SIZE_20));

        //推荐商品
        for (int i = 1; i <= 10; i++) {
            data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
        }

        return data;
    }

    private List<String> InitData() {
        List<String> urls = new ArrayList<>();
        urls.add("http://img4.imgtn.bdimg.com/it/u=335554504,46277580&fm=23&gp=0.jpg");
        urls.add("http://img2.imgtn.bdimg.com/it/u=3881482301,3131576041&fm=23&gp=0.jpg");
        urls.add("http://image.tianjimedia.com/uploadImages/2011/264/31GX4T655Q6D.jpg");
        return urls;
    }

}
