package com.lihao.amusement.Activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.lihao.amusement.Fragment.ContactFragment;
import com.lihao.amusement.Fragment.RecordFragment;
import com.lihao.amusement.Fragment.SettingFragment;
import com.lihao.amusement.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RadioGroup mRg;
    private FrameLayout mFl;
    private Fragment mFragment;
    private ArrayList<Fragment> mFragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        init();
    }

    private void setListener() {
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.btn_video:
                        mFragment = new RecordFragment();
                        break;
                    case R.id.btn_music:
                        mFragment = new ContactFragment();
                        break;
                    case R.id.btn_chat:
                        mFragment = new SettingFragment();
                        break;
                }
                showAndHindFragment(mFragment);
//                for(int i = 0;i<mFragmentArrary.size();i++){
//                    int key = mFragmentArrary.keyAt(i);
//                    if (key==mFragment.getId()){
//                        getSupportFragmentManager().beginTransaction().
//                                show(mFragmentArrary.get(key)).commit();
//                    }else {
//                        mFragmentArrary.put(mFragment.getId(),mFragment);
//                        getSupportFragmentManager().beginTransaction().
//                                add(R.id.main_fl,mFragment).commit();
//                        mFragmentArrary.notify();
//                    }
//                }
//                mFragmentArrary.put(mFragment.getId(),mFragment);
////                mFragmentArrary.notify();
//                getSupportFragmentManager().beginTransaction().
//                        add(R.id.main_fl,mFragment,"1").commit();
            }
        });
    }

    private void showAndHindFragment( Fragment fragment) {

        if (mFragmentList.contains(fragment)) {
            for (int i = 0; i < mFragmentList.size();i++){
                getSupportFragmentManager().beginTransaction().
                        hide(mFragmentList.get(i)).commit();
            }
            getSupportFragmentManager().beginTransaction().
                    show(fragment).commit();
        } else {
            mFragmentList.add(fragment);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.main_fl,fragment).commit();
            for (int i = 0; i < mFragmentList.size();i++){
                getSupportFragmentManager().beginTransaction().
                        hide(mFragmentList.get(i)).commit();
            }
            getSupportFragmentManager().beginTransaction().
                    show(fragment).commit();
        }
    }

    private void init() {
        mFragmentList = new ArrayList<Fragment>();
        mRg.check(R.id.btn_video);
    }

    private void initView() {
        mRg = (RadioGroup) findViewById(R.id.main_rg);
        mFl = (FrameLayout) findViewById(R.id.main_fl);
    }
}
