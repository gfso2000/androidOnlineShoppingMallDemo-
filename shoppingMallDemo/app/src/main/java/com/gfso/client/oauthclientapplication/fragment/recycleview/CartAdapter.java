package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ShoppingCartItem;
import com.gfso.client.oauthclientapplication.fragment.widget.NumControllerView;
import com.gfso.client.oauthclientapplication.util.CartProvider;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{
    private Context mContext;
    private CartProvider cartProvider ;
    private List<ShoppingCartItem> shoppingCarts;
    private OnSelectChangedListener selectChangedListener;
    private OnNumChangedListener numChangedListener;

    public CartAdapter(Context context){
        this.mContext = context;
        cartProvider = CartProvider.getCartProvider(context) ;
        this.shoppingCarts = cartProvider.getAll();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cart, parent, false);

        return new CartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ShoppingCartItem item = shoppingCarts.get(position);
        holder.checkBox.setChecked(item.getChecked());
        holder.simpleDraweeView.setImageURI(item.getImgUrl());
        holder.wareDescription.setText(item.getName());
        holder.warePrice.setText(item.getPrice());
        holder.numControllerView.setValue(item.getCount());

        //notify fragment to refresh
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChecked(holder.checkBox.isChecked());
                selectChangedListener.onSelectChanged(item);
            }
        });

        //notify fragment to refresh
        holder.numControllerView.setValueChangeListener(new NumControllerView.OnNumChangedListener() {
            @Override
            public void addValueListener(View v, int value) {
                item.setCount(item.getCount()+1);
                numChangedListener.onNumChanged(item);
            }

            @Override
            public void subValueListener(View v, int value) {
                item.setCount(item.getCount()-1);
                numChangedListener.onNumChanged(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingCarts.size();
    }

    public void reloadData(){
        this.notifyItemRangeRemoved(0 , shoppingCarts.size());
        shoppingCarts = cartProvider.getAll();
        this.notifyItemRangeChanged(0 , shoppingCarts.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox ;
        private SimpleDraweeView simpleDraweeView ;
        private TextView wareDescription ;
        private TextView warePrice ;
        private NumControllerView numControllerView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cart_item_checkbox);
            simpleDraweeView = itemView.findViewById(R.id.cart_item_draweeview);
            wareDescription = itemView.findViewById(R.id.cart_item_wareDescription);
            warePrice = itemView.findViewById(R.id.cart_item_warePrice);
            numControllerView = itemView.findViewById(R.id.cart_item_num);
        }
    }

    public interface OnSelectChangedListener {
        void onSelectChanged(ShoppingCartItem item);
    }

    public void setOnSelectChangedListener(OnSelectChangedListener listener){
        this.selectChangedListener = listener;
    }

    public interface OnNumChangedListener{
        void onNumChanged(ShoppingCartItem item);
    }

    public void setOnNumChangedListener(OnNumChangedListener listener){
        this.numChangedListener = listener;
    }
}
