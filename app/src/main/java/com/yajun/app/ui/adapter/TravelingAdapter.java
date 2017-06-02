package com.yajun.app.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.yajun.app.module.BluetoothInfo;

import java.util.List;

/**
 * Created by yajun on 2017/5/27.
 *
 */
public class TravelingAdapter extends BaseListAdapter<BluetoothDevice> {


    public TravelingAdapter(Context context, List<BluetoothDevice> list) {
        super(context, list);
    }

    @Override
    protected int getLayoutID() {
        return android.R.layout.simple_list_item_2;
    }

    @Override
    protected void updateView(BaseViewHolder holder, BluetoothDevice module) {
        holder.getText(android.R.id.text1).setText(module.getName());
        holder.getText(android.R.id.text2).setText(module.getAddress());
    }


}
