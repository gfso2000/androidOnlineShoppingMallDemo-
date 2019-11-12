package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gfso.client.oauthclientapplication.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private int selectItem = 0;
    private List<String> list;

    public CategoryAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder holder = null;
        if (arg1 == null) {
            holder = new ViewHolder();
            arg1 = View.inflate(context, R.layout.item_leftside_category, null);
            holder.tv_name = (TextView) arg1.findViewById(R.id.category_name);
            holder.indicator = (TextView) arg1.findViewById(R.id.category_indicator);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        if (arg0 == selectItem) {
            holder.tv_name.setBackgroundColor(Color.WHITE);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.indicator.setBackgroundColor(Color.RED);
        } else {
            holder.tv_name.setBackgroundColor(ContextCompat.getColor(context, R.color.windowBackground));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.indicator.setBackgroundColor(Color.WHITE);
        }
        holder.tv_name.setText(list.get(arg0));
        return arg1;
    }

    static class ViewHolder {
        private TextView tv_name;
        private TextView indicator;
    }
}
