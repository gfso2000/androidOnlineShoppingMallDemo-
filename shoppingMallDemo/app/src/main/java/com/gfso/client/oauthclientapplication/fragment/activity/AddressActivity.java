package com.gfso.client.oauthclientapplication.fragment.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ConsigneeBean;
import com.gfso.client.oauthclientapplication.fragment.recycleview.AddressAdapter;
import com.gfso.client.oauthclientapplication.fragment.widget.MyDivider;
import com.gfso.client.oauthclientapplication.util.AddressProvider;
import com.gfso.client.oauthclientapplication.util.Contents;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.OnItemChangeListener{
    @BindView(R.id.address_toolbar)
    Toolbar toolbar;
    @BindView(R.id.address_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.address_add)
    TextView addBtn;

    AddressAdapter addressAdapter;
    List<ConsigneeBean> items = null;
    AddressProvider addressProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        addressProvider = AddressProvider.getAddressProvider(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        //config recyclerView
        items = getData();
        addressAdapter = new AddressAdapter(this);
        addressAdapter.setOnItemChangeListener(this);
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDivider());

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this , EditAddressActivity.class) ;
                intent.putExtra(Contents.ADDRESS_OPERATION,Contents.ADDRESS_OPERATION_ADD);
                startActivityForResult(intent , Contents.EDIT_ADDRESS_REQUEST);
            }
        });
    }

    /**
     * support back on toolbar
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private List<ConsigneeBean> getData(){
        List<ConsigneeBean> items = addressProvider.getAll();
        if(items == null || items.size() == 0){
            for(int i=0;i<5;i++){
                ConsigneeBean bean = new ConsigneeBean();
                bean.setId(i+"");
                bean.setName("张三"+i);
                bean.setPhone("123456");
                bean.setAddress("浦东新区 软件园");
                bean.setZipcode("100001");
                bean.setDefault(i==1?true:false);
                addressProvider.update(bean);
            }
        }
        return addressProvider.getAll();
    }

    @Override
    public void onItemChange(AddressAdapter.Action action, ConsigneeBean item) {
        if(action == AddressAdapter.Action.SETDEFAULT){
            //set other items default = false
            List<ConsigneeBean> items = addressProvider.getAll();
            for(ConsigneeBean bean : items){
                bean.setDefault(false);
                addressProvider.update(bean);
            }
            //set this item default = true
            item.setDefault(true);
            addressProvider.update(item);
        } else if(action == AddressAdapter.Action.DELETE){
            addressProvider.delete(item);
        } else if(action == AddressAdapter.Action.EDIT){
            Intent intent = new Intent(AddressActivity.this , EditAddressActivity.class) ;
            intent.putExtra(Contents.ADDRESS_OPERATION,Contents.ADDRESS_OPERATION_EDIT);
            intent.putExtra(Contents.ADDRESS_ID,item.getId());
            intent.putExtra(Contents.ADDRESS_NAME,item.getName());
            intent.putExtra(Contents.ADDRESS_PHONE,item.getPhone());
            intent.putExtra(Contents.ADDRESS_ADDRESS,item.getAddress());
            intent.putExtra(Contents.ADDRESS_DEFAULT,item.isDefault());
            startActivityForResult(intent , Contents.EDIT_ADDRESS_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Contents.EDIT_ADDRESS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String id = data.getStringExtra(Contents.ADDRESS_ID);
                addressAdapter.reloadData();
                recyclerView.scrollToPosition(addressAdapter.getPosition(id));
            }
        }
    }
}
