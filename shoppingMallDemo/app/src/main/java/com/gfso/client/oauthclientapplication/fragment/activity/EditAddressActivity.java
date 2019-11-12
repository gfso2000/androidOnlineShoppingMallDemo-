package com.gfso.client.oauthclientapplication.fragment.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ConsigneeBean;
import com.gfso.client.oauthclientapplication.fragment.widget.MyEditText;
import com.gfso.client.oauthclientapplication.util.AddressProvider;
import com.gfso.client.oauthclientapplication.util.Contents;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditAddressActivity extends AppCompatActivity {
    @BindView(R.id.address_save)
    TextView saveBtn;
    @BindView(R.id.consignee_name)
    MyEditText name;
    @BindView(R.id.consignee_phone)
    MyEditText phone;
    @BindView(R.id.consignee_zipcode)
    MyEditText zipcode;
    @BindView(R.id.consignee_address)
    MyEditText address;

    AddressProvider addressProvider;
    String id = null;
    boolean isDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);
        addressProvider = AddressProvider.getAddressProvider(this);

        //config save button
        saveBtn.setOnClickListener((v)->{
            //the id is either existing one, or a new one
            String id = saveAddress();
            Intent intent = new Intent();
            intent.putExtra(Contents.ADDRESS_ID,id);
            setResult(RESULT_OK, intent);
            finish();
        });

        //depends on intent value, show view
        Intent intent = getIntent();
        String operation = intent.getStringExtra(Contents.ADDRESS_OPERATION);
        if(Contents.ADDRESS_OPERATION_ADD.equals(operation)){

        } else {
            id = intent.getStringExtra(Contents.ADDRESS_ID);
            isDefault = intent.getBooleanExtra(Contents.ADDRESS_DEFAULT, false);
            name.setText(intent.getStringExtra(Contents.ADDRESS_NAME));
            phone.setText(intent.getStringExtra(Contents.ADDRESS_PHONE));
            zipcode.setText(intent.getStringExtra(Contents.ADDRESS_ZIPCODE));
            address.setText(intent.getStringExtra(Contents.ADDRESS_ADDRESS));
        }
    }

    private String saveAddress(){
        ConsigneeBean bean = new ConsigneeBean();
        bean.setName(name.getText().toString());
        bean.setPhone(phone.getText().toString());
        bean.setZipcode(zipcode.getText().toString());
        bean.setAddress(address.getText().toString());
        bean.setId(id);
        bean.setDefault(isDefault);
        return addressProvider.update(bean);
    }
}
