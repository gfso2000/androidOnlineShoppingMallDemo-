package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.OrderBean;
import com.gfso.client.oauthclientapplication.util.Contents;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.NineGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I325639 on 1/15/2018.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView orderNum ;
        private TextView isSuccess ;
        private NineGridView waresImg ;
        private TextView waresPriceNum ;
        private Button buyAgain ;

        public MyViewHolder(View view) {
            super(view);
            orderNum = (TextView) view.findViewById(R.id.order_num);
            isSuccess = (TextView) view.findViewById(R.id.isSuccess);
            waresImg = (NineGridView) view.findViewById(R.id.waresImg);
            waresPriceNum = (TextView) view.findViewById(R.id.waresPriceNum);
            buyAgain = (Button)view.findViewById(R.id.buyAgain);
        }
    }

    private Context mContext;
    private List<OrderBean> orderList;

    public MyOrderAdapter(Context mContext, List<OrderBean> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_myorder_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        OrderBean order = orderList.get(position);
        holder.orderNum.setText(order.getOrderNum());
        holder.waresPriceNum.setText((float)order.getAmount() + "");
        switch (order.getStatus()){
            case Contents.PAY_SUCCESS :
                holder.isSuccess.setText("支付成功") ;
                holder.isSuccess.setTextColor(Color.GREEN);
                break;
            case Contents.PAY_FAIL :
                holder.isSuccess.setText("支付失败") ;
                holder.isSuccess.setTextColor(Color.RED);
                break;
            case Contents.PAY_PENDING :
                holder.isSuccess.setText("等待支付") ;
                holder.isSuccess.setTextColor(Color.BLUE);
                break;
        }
        initNineGridLayout(holder, order) ;
    }

    private void initNineGridLayout(MyViewHolder holder, OrderBean order) {
        List<ImageInfo> imageInfos = new ArrayList<>() ;
        for (int i = 0; i < order.getItems().size() ; i++) {
            ImageInfo im = new ImageInfo() ;
            im.setBigImageUrl(order.getItems().get(i).getWares().getImgUrl());
            im.setThumbnailUrl(order.getItems().get(i).getWares().getImgUrl());
            imageInfos.add(im) ;
        }

        NineGridViewAdapter nGridAdater = new NineGridViewAdapter(mContext , imageInfos){

        } ;
        holder.waresImg.setAdapter(nGridAdater);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void cleanData(){
        this.notifyItemRangeRemoved(0 , orderList.size());
        orderList.clear();
    }

    public void addData(List<OrderBean> newDateList){
        orderList.addAll(orderList.size() , newDateList ) ;
        this.notifyItemRangeChanged(0 , orderList.size());
    }
}
