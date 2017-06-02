package com.yajun.app.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yajun.app.R;

/**
 * Created by yajun on 2017/5/22.
 *
 */
public class AnimationFirst extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener{

    ListView mListView;
    ImageView mImageView;

    public int id[] = {
            R.mipmap.butterfly,
            R.mipmap.city,
            R.mipmap.desert,
            R.mipmap.flower,
            R.mipmap.flowers,
            R.mipmap.butterfly
    };

    public String list[] = { "一", "二", "三", "四", "五", "六" };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mListView = (ListView) findViewById(R.id.list);
        mImageView = (ImageView) findViewById(R.id.image);
        mImageView.setOnClickListener(this);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mListView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        animation(position);
    }

    private void animation(final int position){
        final View vis;
        final View inVis;
        if (mListView.getVisibility() == View.VISIBLE) {
            vis = mListView;
            inVis = mImageView;
        } else {
            vis = mImageView;
            inVis = mListView;
        }
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(vis, "RotationY", 0f, 90f);
        final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(inVis, "RotationY", -90f, 0f);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                objectAnimator2.start();
                vis.setVisibility(View.GONE);
                mImageView.setImageResource(id[position]);
                inVis.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.setDuration(500);
        animatorSet.play(objectAnimator).before(objectAnimator2);//两个动画同时开始
        animatorSet.start();
    }

    @Override
    public void onClick(View v) {
        animation(0);
    }
}
