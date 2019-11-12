package com.gfso.client.oauthclientapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gfso.client.oauthclientapplication.bean.MultiTypeItemBean;
import com.gfso.client.oauthclientapplication.fragment.FilterDrawerFragment;
import com.gfso.client.oauthclientapplication.fragment.recycleview.MultiTypeItemAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_content)
    FrameLayout mDrawerContent;
    @BindView(R.id.ll_back)
    LinearLayout backLayout;
    @BindView(R.id.sort_button_filter)
    TextView filterBtn;
    @BindView(R.id.search_result_search_layout)
    RelativeLayout searchLayout;
    @BindView(R.id.search_result_btn_search)
    Button searchBtn;
    @BindView(R.id.shopping_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.shopping_swipe_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.fab_up_slide)
    FloatingActionButton backToTopButton;
    @BindView(R.id.search_result_layout)
    RelativeLayout searchResultLayout;
    @BindView(R.id.search_result_empty_layout)
    RelativeLayout searchResultEmptyLayout;

    String mSearchkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        initDrawer();
        backLayout.setOnClickListener((v)->{
            finish();
        });
        filterBtn.setOnClickListener((v)->{
            mDrawerLayout.openDrawer(mDrawerContent);
        });
        searchLayout.setOnClickListener((v)->{
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("searchKey", mSearchkey);
            startActivity(intent);
        });
        searchBtn.setOnClickListener((v)->{
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("searchKey", mSearchkey);
            startActivity(intent);
        });
        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra("searchKey") ){
            mSearchkey = intent.getStringExtra("searchKey");
            searchBtn.setText(mSearchkey);
        }
        if("NoData".equals(mSearchkey)){
            searchResultLayout.setVisibility(View.GONE);
            searchResultEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            searchResultLayout.setVisibility(View.VISIBLE);
            searchResultEmptyLayout.setVisibility(View.GONE);
            loadData();
        }
    }

    private void loadData() {
        final List<MultiTypeItemBean> data = getMultiTypeItemBeanData();
        final MultiTypeItemAdapter multipleItemAdapter = new MultiTypeItemAdapter(data, this);
        final GridLayoutManager manager = new GridLayoutManager(this, MultiTypeItemBean.TYPE_SPAN_SIZE_20);
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
                        Toast.makeText(SearchResultActivity.this, "loaded", Toast.LENGTH_SHORT).show();
                        multipleItemAdapter.addData(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
                        multipleItemAdapter.addData(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
                        multipleItemAdapter.loadMoreComplete();

                        //if no more data, then call loadMoreEnd(), it means, the loadMore will never be triggered
                        //multipleItemAdapter.notifyDataSetChanged();
                        if(multipleItemAdapter.getData().size()>=16){
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
                Intent intent = new Intent(SearchResultActivity.this, GoodsDetailActivity.class);
                SearchResultActivity.this.startActivity(intent);
                Toast.makeText(SearchResultActivity.this, "position:" + Integer.toString(position), Toast.LENGTH_SHORT).show();
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
                if (offset > 1 * extent) {
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

        refreshLayout.setOnRefreshListener(SearchResultActivity.this);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(SearchResultActivity.this, android.R.color.holo_green_dark),
                ContextCompat.getColor(SearchResultActivity.this, android.R.color.holo_red_dark),
                ContextCompat.getColor(SearchResultActivity.this, android.R.color.holo_blue_dark),
                ContextCompat.getColor(SearchResultActivity.this, android.R.color.holo_orange_dark));
    }

    private List<MultiTypeItemBean> getMultiTypeItemBeanData() {
        List<MultiTypeItemBean> data = new ArrayList<>();

        //推荐商品
        for (int i = 1; i <= 10; i++) {
            data.add(new MultiTypeItemBean(MultiTypeItemBean.TYPE_9, MultiTypeItemBean.TYPE_SPAN_SIZE_10));
        }

        return data;
    }

    private void initDrawer() {
        Fragment fragment = new FilterDrawerFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("departmentName","");
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).commit();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }
}
