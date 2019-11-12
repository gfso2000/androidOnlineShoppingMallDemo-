package com.gfso.client.oauthclientapplication.fragment.recycleview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.CategoryBean;
import com.gfso.client.oauthclientapplication.fragment.widget.GridViewForScrollView;

import java.util.List;

public class SubcategoryGroupAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryBean.DataBean> subcategoryList;

    public SubcategoryGroupAdapter(Context context, List<CategoryBean.DataBean> subcategoryList) {
        this.context = context;
        this.subcategoryList = subcategoryList;
    }

    @Override
    public int getCount() {
        if (subcategoryList != null) {
            return subcategoryList.size();
        } else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return subcategoryList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryBean.DataBean dataBean = subcategoryList.get(position);
        List<CategoryBean.DataBean.DataListBean> dataList = dataBean.getDataList();
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_rightside_subcategory_group, null);
            viewHold = new ViewHold();
            viewHold.gridView = (GridViewForScrollView) convertView.findViewById(R.id.gridView);
            viewHold.blank = (TextView) convertView.findViewById(R.id.item_rightside_subcategory_group_title);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        SubcategoryAdapter adapter = new SubcategoryAdapter(context, dataList);
        viewHold.blank.setText(dataBean.getModuleTitle());
        viewHold.gridView.setAdapter(adapter);
        return convertView;
    }

    private static class ViewHold {
        private GridViewForScrollView gridView;
        private TextView blank;
    }

}
