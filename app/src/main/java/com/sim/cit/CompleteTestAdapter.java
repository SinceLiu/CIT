package com.sim.cit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CompleteTestAdapter extends BaseAdapter {
    private Context context;
    private List<TestItem> list;

    public CompleteTestAdapter(Context context, List<TestItem> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.complete_list_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.ItemTitle);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TestItem testItem = list.get(position);
        viewHolder.title.setText(testItem.getTitle());
        viewHolder.title.setTextColor(testItem.getTextColor());
        return view;

    }

    public static class ViewHolder {
        TextView title;
    }
}
