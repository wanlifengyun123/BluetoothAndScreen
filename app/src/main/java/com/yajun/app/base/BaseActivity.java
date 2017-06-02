package com.yajun.app.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yajun.app.R;

import butterknife.ButterKnife;

/**
 * Created by yajun on 2017/5/31.
 *
 */
public class BaseActivity extends BaseAppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return 0;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected boolean isBindEventBusHere() {
        return false;
    }

    @Override
    protected void initViewsAndEvents() {

    }
}
