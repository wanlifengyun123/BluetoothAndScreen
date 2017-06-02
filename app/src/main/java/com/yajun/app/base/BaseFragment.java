package com.yajun.app.base;

/**
 * Created by yajun on 2017/5/31.
 *
 */
public class BaseFragment extends BaseLazyFragment {

    @Override
    protected int getContentViewLayoutID() {
        return 0;
    }

    @Override
    protected boolean isBindEventBusHere() {
        return false;
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }
}
