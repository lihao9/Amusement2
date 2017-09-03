package com.lihao.amusement.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.Activity.ChatActivity;
import com.lihao.amusement.Activity.SearchFriendActivity;
import com.lihao.amusement.Activity.ShowGroupListActivity;
import com.lihao.amusement.Activity.ShowInvitationActivity;
import com.lihao.amusement.Constant;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;
import com.lihao.amusement.Tools.SingleTool;
import com.lihao.amusement.Tools.SpTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

public class ContactFragment extends EaseContactListFragment {
    private ImageView mIvRed;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver contactInvitationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到了邀请信息状态变化
            //更新红点的变化
            mIvRed.setVisibility(View.VISIBLE);
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE,true);

        }
    };

    private BroadcastReceiver contactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新页面
            refreshContact();
        }
    };

    private BroadcastReceiver groupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点的变化
            mIvRed.setVisibility(View.VISIBLE);
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE,true);
        }
    };

    private LinearLayout llFriend;
    private LinearLayout llGroup;


    @Override
    protected void initView() {
        super.initView();
        //添加查找人按钮
        titleBar.setRightImageResource(R.drawable.ease_blue_add);
        //添加头布局
        View heard = View.inflate(getActivity(),R.layout.heard_layout,null);
        mIvRed = (ImageView) heard.findViewById(R.id.iv_red_dot);
        llFriend = (LinearLayout) heard.findViewById(R.id.ll_invite_friend);
        llGroup = (LinearLayout) heard.findViewById(R.id.ll_invite_group);
        listView.addHeaderView(heard);

        setContactListItemClickListener(new EaseContactListItemClickListener() {
            //点击了联系人列表
            @Override
            public void onListItemClicked(EaseUser user) {
                //进入到聊天界面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
//                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,)
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });
        //初始化红点的显示
        boolean isNewInvite = SpTool.getmSptool().getBoolean(SpTool.IS_NEW_INVITE, false);
        mIvRed.setVisibility(isNewInvite?View.VISIBLE:View.GONE);

        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(contactInvitationReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(contactChangeReceiver,new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(groupChangeReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));


        //邀请信息条目的点击事件
        llFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //红点处理
                mIvRed.setVisibility(View.GONE);
                SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE,false);
                //跳转到邀请信息
                Intent intent = new Intent(getActivity(), ShowInvitationActivity.class);
                startActivity(intent);
            }
        });

        //查看群列表的条目的点击事件
        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入群列表页面
                Intent intent = new Intent(getActivity(), ShowGroupListActivity.class);
                startActivity(intent);
            }
        });

        //获取联系人列表--把联系人绑定到listView上
        getAllContact();

        // 绑定listview和contextmenu
        registerForContextMenu(listView);
    }

    private String mHxid;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //获取环信Id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        //获取点击的用户
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();
        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delect,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.contact_delete){
            delete();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    //删除联系人
    private void delete() {
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    //本地删除联系人
                    SingleTool.getInstence().getDbManager().getContactTableDao()
                            .deleteContactByHxId(mHxid);

                    //删除和本人相同的邀请信息
                    SingleTool.getInstence().getDbManager().getInviteTableDao()
                            .delectInvatation(mHxid);
                    //刷新页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshContact();
                            Toast.makeText(getActivity(), "删除联系人"+mHxid+"成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除联系人失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

    //获取
    private void getAllContact() {
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> hxids = EMClient.getInstance().contactManager()
                            .getAllContactsFromServer();
                    if (hxids!=null&&hxids.size()>0){
                        //有联系人
                        List<UserInfo> users = new ArrayList<UserInfo>();
                        for (String hxid: hxids) {
                            UserInfo user = new UserInfo(hxid);
                            users.add(user);
                        }
                        //保存联系人数据到本地
                        SingleTool.getInstence().getDbManager().getContactTableDao()
                                .saveContacts(users,true);
                        //刷新界面
                        if (getActivity()==null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContact();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //刷新联系人界面
    private void refreshContact() {
        List<UserInfo> users = SingleTool.getInstence().getDbManager()
                                         .getContactTableDao().getAllContaxt();
        if (users!=null&&users.size()>=0){
            Map<String, EaseUser> map = new HashMap<>();
            for(UserInfo user:users){
                EaseUser easeUser = new EaseUser(user.getHxId());
                map.put(user.getHxId(),easeUser);
            }
            setContactsMap(map);
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(contactInvitationReceiver);
    }
}
