package com.lihao.amusement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.Activity.PickContactsActivity;
import com.lihao.amusement.Adapter.GroupMemGVAdapter;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Tools.SingleTool;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private static final String TAG = "GroupDetailActivity";
    private GridView mGv;
    private Button mBtnOut;
    private EMGroup EMGroup;
    private LocalBroadcastManager mLBM;
    private GroupMemGVAdapter adapter;
    private List<UserInfo> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        initView();
        initDate();
        initListener();
    }

    private void initListener() {
        mGv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        // 判断当前是否是删除模式,如果是删除模式
                        if(adapter.ismIsDeleteModel()) {
                            // 切换为非删除模式
                            adapter.setmIsDeleteModel(false);

                            // 刷新页面
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }
        });
    }

    //初始化数据
    private void initDate() {
        mLBM = LocalBroadcastManager.getInstance(this);
        //群的hxId
        final String  groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId==null){
            finish();
            return;
        }else {
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    EMGroup = EMClient.getInstance().groupManager().getGroup(groupId);
                }
            });
        }
        //判断是否显示显示按钮
        initButton();
        getAndSetDate();
    }


    //获取并且为控件设置数据
    private void getAndSetDate() {
        boolean isCanModify = EMClient.getInstance().getCurrentUser()
                .equals(EMGroup.getOwner()) || EMGroup.isPublic();
        adapter = new GroupMemGVAdapter(this,isCanModify,mOnGroupDetailListener);
        mGv.setAdapter(adapter);
        getGroupMemFromServer();
    }

    //获取群成员
    private void getGroupMemFromServer() {
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup group = EMClient.getInstance()
                            .groupManager().getGroupFromServer(EMGroup.getGroupId());

                    List<String> memberList = new ArrayList<String>();
                    memberList = group.getMembers();
                    if (memberList==null&&memberList.size()<=0) {
                        EMCursorResult<String> result = null;
                        final int pageSize = 20;
                        do {
                            result = EMClient.getInstance().groupManager().fetchGroupMembers(group.getGroupId(), result != null ? result.getCursor() : "", pageSize);

                            memberList.addAll(result.getData());
                        } while (result.getData().size() == pageSize);
                    }
                    if (memberList!=null&&memberList.size()>0){
                        users = new ArrayList<UserInfo>();
                        for (String hxid:
                                memberList) {
                            users.add(new UserInfo(hxid));
                        }
                    }
                    //更新界面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.refresh(users);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    //判断是否显示显示按钮
    private void initButton() {
        //当前用户是群主--可以添加和减少用户
        if (EMClient.getInstance().getCurrentUser().equals(EMGroup.getOwner())){
            mBtnOut.setText("解散群");
            mBtnOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //解散群
                                Log.i(TAG, "群主解散群");
                                EMClient.getInstance().groupManager().destroyGroup(EMGroup
                                        .getGroupId());
                                //发送解散群广播
                                exitGroup();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新界面
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast
                                                .LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新界面
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败", Toast
                                                .LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else {
            //群成员
            mBtnOut.setText("退出群");
            mBtnOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/6/7 -- 退出群
                    SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //退出群
                                EMClient.getInstance().groupManager().leaveGroup(EMGroup
                                        .getGroupId());
                                exitGroup();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast
                                                .LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败", Toast
                                                .LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }

    }

    private void exitGroup() {
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID,EMGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    private void initView() {
        mBtnOut = (Button) findViewById(R.id.btn_group_detail_out);
        mGv = (GridView) findViewById(R.id.gv_group_detail);
    }

    private GroupMemGVAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupMemGVAdapter.OnGroupDetailListener() {
        // 添加群成员
        @Override
        public void onAddMembers() {
            // 跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactsActivity.class);

            // 传递群id
            intent.putExtra(Constant.GROUP_ID, EMGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        // 删除群成员方法
        @Override
        public void onDeleteMember(final UserInfo user) {
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 从环信服务器中删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(EMGroup.getGroupId(), user.getHxId());

                        // 更新页面
                        getAndSetDate();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 获取返回的准备邀请的群成员信息
            final String[] memberses = data.getStringArrayExtra("members");

            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 去环信服务器，发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(EMGroup.getGroupId(), memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
