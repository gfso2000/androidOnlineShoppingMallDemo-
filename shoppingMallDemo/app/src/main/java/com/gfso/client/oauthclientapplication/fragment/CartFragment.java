package com.gfso.client.oauthclientapplication.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ShoppingCartItem;
import com.gfso.client.oauthclientapplication.fragment.recycleview.CartAdapter;
import com.gfso.client.oauthclientapplication.fragment.widget.MyDivider;
import com.gfso.client.oauthclientapplication.util.CartProvider;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartFragment extends Fragment {
    @BindView(R.id.cart_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.cart_total_textview)
    TextView sumPrice;
    @BindView(R.id.cart_selectall_checkbox)
    CheckBox allChooseCheckBox;
    @BindView(R.id.cart_edit)
    TextView editBtn;
    @BindView(R.id.cart_pay_button)
    Button payButton;

    AppCompatActivity activity = null;
    private CartProvider cartProvider ;
    private CartAdapter cartAdapter ;
    private float sumPrices ;
    private final static int STATUS_EDIT = 0 ;
    private final static int STATUS_NORNAL = 1 ;
    private static int status = STATUS_NORNAL ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, null);
        activity = (AppCompatActivity)this.getActivity();
        ButterKnife.bind(this, view);

        cartProvider = CartProvider.getCartProvider(getActivity());
        ShoppingCartItem item = new ShoppingCartItem();
        item.setChecked(true);
        item.setCount(1);
        item.setId(UUID.randomUUID().toString());
        item.setName("item 1");
        item.setPrice("1.1");
        item.setImgUrl("http://img4.imgtn.bdimg.com/it/u=335554504,46277580&fm=23&gp=0.jpg");
        cartProvider.add(item);

        initRecyclerView() ;

        allChooseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    doSelectAll() ;
                }else{
                    doSelectNone() ;
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == STATUS_NORNAL){
                    showEditView() ;
                }else if (status == STATUS_EDIT){
                    showNormalView() ;
                }
            }
        });
        return view;
    }

    private void showEditView(){
        editBtn.setText("完成");
        doSelectNone() ;
        sumPrice.setVisibility(View.GONE);
        payButton.setText("删除");
        status = STATUS_EDIT ;
    }

    private void showNormalView(){
        editBtn.setText("编辑");
        doSelectAll() ;
        sumPrice.setVisibility(View.VISIBLE);
        payButton.setText("去结算");
        status = STATUS_NORNAL ;
        refreshRecyclerView();
    }

    private void initRecyclerView(){
        List<ShoppingCartItem> shoppingCarts = cartProvider.getAll();
        if (shoppingCarts != null && shoppingCarts.size() > 0){
            cartAdapter = new CartAdapter(activity) ;
            recyclerView.setAdapter(cartAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.addItemDecoration(new MyDivider());
            setAdapterListener() ;
            calculateSum() ;
            setSelectAllCheckBoxState() ;
        }
    }

    private void setAdapterListener(){
        //cart item checkbox listener
        cartAdapter.setOnSelectChangedListener(new CartAdapter.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ShoppingCartItem item) {
                cartProvider.replace(item);
                setSelectAllCheckBoxState();
                calculateSum();
            }
        });

        //cart item num listener
        cartAdapter.setOnNumChangedListener(new CartAdapter.OnNumChangedListener() {
            @Override
            public void onNumChanged(ShoppingCartItem item) {
                cartProvider.replace(item);
                calculateSum();
            }
        });
    }

    //check select All
    private void doSelectAll(){
        List<ShoppingCartItem> shoppingCarts = cartProvider.getAll();
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            boolean isChanged = false ;

            for (int i = 0; i <shoppingCarts.size() ; i++) {
                if ( !shoppingCarts.get(i).getChecked() ){
                    shoppingCarts.get(i).setChecked(true);
                    cartProvider.replace(shoppingCarts.get(i));
                    isChanged = true ;
                }
            }
            if(isChanged){
                refreshRecyclerView() ;
            }
        }
    }

    //uncheck select all
    private void doSelectNone(){
        List<ShoppingCartItem> shoppingCarts = cartProvider.getAll();
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            boolean isChanged = false ;

            for (int i = 0; i <shoppingCarts.size() ; i++) {
                if ( shoppingCarts.get(i).getChecked() ){
                    shoppingCarts.get(i).setChecked(false);
                    cartProvider.replace(shoppingCarts.get(i));
                    isChanged = true ;
                }
            }
            if(isChanged){
                refreshRecyclerView() ;
            }
        }
    }

    private void refreshRecyclerView(){
        cartAdapter.reloadData();
        calculateSum() ;
        setSelectAllCheckBoxState() ;
    }

    //refresh select all state
    private void setSelectAllCheckBoxState(){
        List<ShoppingCartItem> shoppingCarts = cartProvider.getAll();
        if (shoppingCarts == null) {
            allChooseCheckBox.setChecked(false);
            return;
        }

        for (int i = 0; i < shoppingCarts.size() ; i++) {
            if( !shoppingCarts.get(i).getChecked() ){
                allChooseCheckBox.setChecked(false);
                return;
            }
        }
        allChooseCheckBox.setChecked(true);
    }

    //refresh total price
    private void calculateSum(){
        List<ShoppingCartItem> shoppingCarts = cartProvider.getAll();
        float sumPriceData = (float) 0.0;
        for (int i = 0; i < shoppingCarts.size() ; i++) {
            if (shoppingCarts.get(i).getChecked()){
                sumPriceData  += Double.valueOf( shoppingCarts.get(i).getPrice() ) *  shoppingCarts.get(i).getCount() ;
            }
        }
        sumPrices = sumPriceData ;
        sumPrice.setText("合计 ￥"+ sumPriceData) ;
    }
}
