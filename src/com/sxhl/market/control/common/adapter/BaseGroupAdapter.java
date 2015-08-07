package com.sxhl.market.control.common.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.model.entity.Group;

abstract class BaseGroupAdapter<T extends AutoType> extends BaseAdapter {
	
    public Group<T> group = null;

    public BaseGroupAdapter(Context context) {
    	super();
    }

    @Override
    public int getCount() {
        return (group == null) ? 0 : group.size();
    }

    @Override
    public Object getItem(int position) {
        return group.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return (group == null) ? true : group.isEmpty();
    }

    public void setGroup(Group<T> g) {
        group = g;
        notifyDataSetInvalidated();
    }
}
