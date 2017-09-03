package com.lihao.amusement.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.lihao.amusement.Adapter.GroupListAdapter;
import com.lihao.amusement.R;
import com.lihao.amusement.Tools.SingleTool;

import java.util.List;

public class ShowGroupListActivity extends AppCompatActivity {

    private static final String TAG = "ShowGroupListActivity";
    private ListView mLv;
    private LinearLayout header;
    private GroupListAdapter adapter;
    private List<EMGroup> allGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_list);
        initView();
        initDate();
        setDateAndListener();
    }

    //绑定数据
    private void setDateAndListener() {
        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    //新建群
                    // TODO: 2017/6/5
                    Log.i(TAG, "新建群");
                    Intent intent = new Intent(ShowGroupListActivity.this, CreateGroupActivity
                            .class);
                    startActivity(intent);
                }else {
                    //进入群聊天界面
                    // TODO: 2017/6/5
                    Log.i(TAG, "进入群聊天界面");
                    Intent intent = new Intent(ShowGroupListActivity.this, ChatActivity.class);
                    String groupId =  EMClient.getInstance().groupManager()
                            .getAllGroups().get(position-1).getGroupId();
                    //传入群的HXid
                    intent.putExtra(EaseConstant.EXTRA_USER_ID,groupId);
                    //传入聊天的类型
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                    startActivity(intent);

                }
            }
        });
    }

    //初始化数据
    private void  initDate() {
        adapter = new GroupListAdapter(ShowGroupListActivity.this);
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
//                    List<EMGroup> allGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    allGroups = EMClient.getInstance().groupManager().getAllGroups();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowGroupListActivity.this, "加载数据成功", Toast.LENGTH_SHORT).show();
                        refreshDate();
                    }
                });
            }
        });

    }

    private void initView() {
        mLv = (ListView) findViewById(R.id.lv_group);
        header = (LinearLayout) View.inflate(this,R.layout.group_list_header,null);
        mLv.addHeaderView(header);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDate();
    }

    //刷新界面
    private void refreshDate() {
        allGroups = EMClient.getInstance().groupManager().getAllGroups();
        adapter.refresh(allGroups);
    }
}
