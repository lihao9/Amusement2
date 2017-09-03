package com.lihao.amusement.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;

import com.lihao.amusement.Tools.SingleTool;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

public class SearchFriendActivity extends Activity{
    private TextView mTvSearch;
    private EditText mEtName;
    private TextView mTvName;
    private RelativeLayout mRl;
    private Button mBtnAdd;
    private UserInfo user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        initView();
        setListener();
    }

    private void setListener() {
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findFriend();
            }
        });
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    private void addFriend() {
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(user.getName(),"添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchFriendActivity.this, "发送添加好友成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    //发送添加好友
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchFriendActivity.this, "发送好友请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

            }
        });
    }

    private void findFriend() {
        //查询用户
        final String name = mEtName.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(SearchFriendActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }else {
            //查询用户
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    user = new UserInfo(name);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvName.setText(user.getName());
                            mRl.setVisibility(View.VISIBLE);
                            mBtnAdd.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

        }
    }

    private void initView() {
        mTvName = (TextView) findViewById(R.id.tv_friend_name);
        mTvSearch = (TextView) findViewById(R.id.tv_search);
        mEtName = (EditText) findViewById(R.id.et_friend_name);
        mRl = (RelativeLayout) findViewById(R.id.rl_show_friend);
        mBtnAdd = (Button) findViewById(R.id.btn_add_friend);
    }
}
