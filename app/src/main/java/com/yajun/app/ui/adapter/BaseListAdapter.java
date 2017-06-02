package com.yajun.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<E> extends BaseAdapter {

    protected Context mContext;
    private List<E> mList = new ArrayList<E>();
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public BaseListAdapter(Context context, List<E> list) {
        this(context);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public E getItem(int position) {
        return (E) mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final BaseViewHolder holder;
        if (convertView != null) {
            holder = (BaseViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(getLayoutID(), null);
            holder = new BaseViewHolder(convertView);
            convertView.setTag(holder);
        }
        updateView(holder,getItem(i));
        return convertView;
    }

    protected abstract int getLayoutID();

    protected abstract void updateView(BaseViewHolder holder,E module);

    public void clearAll() {
        mList.clear();
    }

    public List<E> getData() {
        return mList;
    }

    public void addALL(List<E> list){
        if(list==null||list.size()==0) return;
        mList.addAll(list);
        notifyDataSetChanged();
    }
    public void add(E item){
        mList.add(item);
    }

    public void removeEntity(E e){
        mList.remove(e);
    }

}
