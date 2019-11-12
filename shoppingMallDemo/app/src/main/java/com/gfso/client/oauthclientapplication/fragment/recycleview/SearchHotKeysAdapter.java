package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;

import java.util.List;

public class SearchHotKeysAdapter extends RecyclerView.Adapter<SearchHotKeysAdapter.MyViewHolder>{
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView hotkey;

        public MyViewHolder(View view) {
            super(view);
            hotkey = (TextView) view.findViewById(R.id.hotkey);
            hotkey.setOnClickListener((v)->{
                myClickListener.onItemClick(hotkey.getText().toString());
            });
        }
    }
    public interface MyClickListener {
        public void onItemClick(String hotKey);
    }

    private Context mContext;
    private List<String> keyList;
    private MyClickListener myClickListener;
    public SearchHotKeysAdapter(Context mContext, List<String> keyList) {
        this.mContext = mContext;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public SearchHotKeysAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_hotkeys, parent, false);

        return new SearchHotKeysAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchHotKeysAdapter.MyViewHolder holder, int position) {
        holder.hotkey.setText(keyList.get(position));
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}
