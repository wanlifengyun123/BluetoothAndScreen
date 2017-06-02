package com.yajun.app.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;

/**
 * Created by yajun on 2017/5/27.
 *
 */
public class BaseViewHolder {

    private View convertView;

    protected BaseViewHolder (View convertView){
        this.convertView = convertView;
        ButterKnife.bind(convertView);
    }

    public TextView getText(int id){
        return ButterKnife.findById(convertView,id);
    }

    public ImageView getImage(int id){
        return ButterKnife.findById(convertView,id);
    }

}
