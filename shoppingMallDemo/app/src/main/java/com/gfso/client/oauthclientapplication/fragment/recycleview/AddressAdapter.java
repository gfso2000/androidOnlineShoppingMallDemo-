package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.ConsigneeBean;
import com.gfso.client.oauthclientapplication.util.AddressProvider;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder>{
    private Context mContext;
    private List<ConsigneeBean> items;
    private OnItemChangeListener listener;
    AddressProvider addressProvider;

    public AddressAdapter(Context context){
        this.mContext = context;
        addressProvider = AddressProvider.getAddressProvider(context);
        this.items = addressProvider.getAll();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_address_item, parent, false);

        return new AddressAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ConsigneeBean item = items.get(position);
        holder.consigneeName.setText(item.getName());
        holder.consigneePhone.setText(item.getPhone());
        holder.consigneeAddress.setText(item.getZipcode()+" "+ item.getAddress());
        holder.setDefaultText.setText(item.isDefault()?"默认":"设为默认");
        holder.setDefaultRadio.setChecked(item.isDefault());

        holder.setDefaultRadio.setOnClickListener((v)->{
            RadioButton radioButton = (RadioButton)v;
            if(radioButton.isChecked()){
                for(ConsigneeBean bean : items){
                    bean.setDefault(false);
                }
                item.setDefault(true);
                listener.onItemChange(Action.SETDEFAULT, item);
                refreshUI();
            }
        });
        holder.deleteBtn.setOnClickListener((v)->{deleteItem(position, item);});
        holder.editBtn.setOnClickListener((v)->{listener.onItemChange(Action.EDIT, item);});
    }

    private void deleteItem(int position, ConsigneeBean item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                items.remove(position);
                //set another as default
                if(item.isDefault() && items.size()>=1){
                    items.get(0).setDefault(true);
                }
                listener.onItemChange(Action.DELETE, item);
                refreshUI();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void refreshUI(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void reloadData(){
        this.notifyItemRangeRemoved(0 , items.size());
        items = addressProvider.getAll();
        this.notifyItemRangeChanged(0 , items.size());
    }

    public int getPosition(String targetId){
        for(int i=0;i<items.size();i++){
            if(items.get(i).getId().equals(targetId)){
                return i;
            }
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView consigneeName ;
        private TextView consigneePhone ;
        private TextView consigneeAddress ;
        private TextView setDefaultText ;
        private RadioButton setDefaultRadio ;
        private TextView editBtn ;
        private TextView deleteBtn ;

        public MyViewHolder(View itemView) {
            super(itemView);
            consigneeName = itemView.findViewById(R.id.consignee_name);
            consigneePhone = itemView.findViewById(R.id.consignee_phone);
            consigneeAddress = itemView.findViewById(R.id.consignee_address);
            setDefaultText = itemView.findViewById(R.id.set_default_text);
            setDefaultRadio = itemView.findViewById(R.id.set_default_radio);
            editBtn = itemView.findViewById(R.id.edit_address);
            deleteBtn = itemView.findViewById(R.id.delete_address);
        }
    }

    public interface OnItemChangeListener {
        public void onItemChange(Action action, ConsigneeBean item);
    }
    public void setOnItemChangeListener(OnItemChangeListener listener){
        this.listener = listener;
    }

    public enum Action{
        SETDEFAULT, EDIT, DELETE
    }
}
