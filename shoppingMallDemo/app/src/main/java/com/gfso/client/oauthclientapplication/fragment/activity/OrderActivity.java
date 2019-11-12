package com.gfso.client.oauthclientapplication.fragment.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ConsigneeBean;
import com.gfso.client.oauthclientapplication.bean.OrderBean;
import com.gfso.client.oauthclientapplication.bean.OrderItems;
import com.gfso.client.oauthclientapplication.bean.Ware;
import com.gfso.client.oauthclientapplication.fragment.recycleview.MyOrderAdapter;
import com.gfso.client.oauthclientapplication.fragment.widget.MyDivider;
import com.gfso.client.oauthclientapplication.util.Contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity {
    @BindView(R.id.recycleView)
    RecyclerView recyclerView ;
    @BindView(R.id.show_order_tab)
    TabLayout show_order_tab ;

    private TabLayout.Tab showAllTab ;
    private TabLayout.Tab showSuccessTab ;
    private TabLayout.Tab showFailTab ;
    private TabLayout.Tab showWaitPayTab ;

    Toolbar toolbar = null;
    private MyOrderAdapter myAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

        initView();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar != null) {
            toolbar.setTitle("");
        }
    }

    private void initView() {
        showAllTab = show_order_tab.newTab().setText("全部订单").setTag(Contents.PAY_ALL) ;
        showSuccessTab = show_order_tab.newTab().setText("支付成功").setTag(Contents.PAY_SUCCESS) ;
        showWaitPayTab = show_order_tab.newTab().setText("等待支付").setTag(Contents.PAY_PENDING) ;
        showFailTab = show_order_tab.newTab().setText("支付失败").setTag(Contents.PAY_FAIL) ;

        show_order_tab.addTab(showAllTab);
        show_order_tab.addTab(showSuccessTab);
        show_order_tab.addTab(showWaitPayTab);
        show_order_tab.addTab(showFailTab);

        show_order_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getData((int)tab.getTag()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        show_order_tab.getTabAt(0).select();

        getData(Contents.PAY_ALL) ;
    }

    private void getData(int status) {
        Map<String , String> params = new HashMap<String , String>() ;
        params.put("user_id" , MyApplication.getInstance().getUser().getId()+"") ;
        params.put("status" , status + "" ) ;

//        okhttp.doGet(Contents.API.GET_ORDER_LIST, new loadingSpotsDialog<List<OrderMsg>>(this) {
//            @Override
//            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
//                this.closeSpotsDialog();
//            }
//
//            @Override
//            public void callBackSucces(Response response, List<OrderMsg> orderMsgs) throws IOException {
//                this.closeSpotsDialog();
//
//                showOrderData(orderMsgs) ;
//
//
//            }
//
//        }, params);

        List<OrderBean> orderList = new ArrayList<>();
        //add order bean
        OrderBean orderItem = new OrderBean();
        orderItem.setAmount(1);
        orderItem.setCreatedTime("2018-07-27");
        orderItem.setId(1l);
        orderItem.setOrderNum("123");
        orderItem.setStatus(status);

        ConsigneeBean consignee = new ConsigneeBean();
        consignee.setId("1");
        consignee.setAddress("address");
        consignee.setZipcode("202020");
        consignee.setPhone("12345678");
        consignee.setName("name");
        orderItem.setConsigneeMsg(consignee);

        List<OrderItems> itemsList = new ArrayList<>();
        OrderItems items = new OrderItems();
        items.setAmount(123);
        items.setId(1);
        items.setOrderId(1);
        items.setWare_id(1);
        Ware ware = new Ware();
        ware.setCampaignId(1);
        ware.setCategoryId(1);
        ware.setId("1");
        ware.setImgUrl("http://img4.imgtn.bdimg.com/it/u=335554504,46277580&fm=23&gp=0.jpg");
        ware.setName("name");
        ware.setPrice("1.1");
        ware.setSale(100);
        items.setWares(ware);
        itemsList.add(items);
        orderItem.setItems(itemsList);
        orderList.add(orderItem);
        showOrderData(orderList) ;
    }

    private void showOrderData(List<OrderBean> orderList) {
        if (orderList != null && orderList.size() > 0 ){
            if (myAdapter == null ){
                myAdapter = new MyOrderAdapter(this , orderList ) ;
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new MyDivider());
            }else{
                myAdapter.cleanData();
                myAdapter.addData(orderList);
            }
        }else if (myAdapter != null){
            myAdapter.cleanData();
        }
    }
}
