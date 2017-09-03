package com.lihao.amusement.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.lihao.amusement.Adapter.ContactListAdapter;
import com.lihao.amusement.Constant;
import com.lihao.amusement.Model.Bean.PickUser;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;
import com.lihao.amusement.Tools.SingleTool;

import java.util.ArrayList;
import java.util.List;

public class PickContactsActivity extends AppCompatActivity {
    private ListView mLvContact;
    private TextView mTvSava;
    private ContactListAdapter adapter;
    private List<UserInfo> users;
    private List<PickUser> pickUsers;
    private List<String> exisMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);
        getDate();
        initView();
        initDate();
    }

    //获取传递来的数据
    private void getDate() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            // 获取群众已经存在的所有群成员
            exisMembers = group.getMembers();
        }

        if (exisMembers == null) {
            exisMembers = new ArrayList<>();
        }
    }

    private void initDate() {
        users = SingleTool.getInstence().getDbManager()
                .getContactTableDao().getAllContaxt();
        //将联系人转化为群对应的联系人
        pickUsers = new ArrayList<>();
        for (UserInfo user:
             users) {
            PickUser pickUser = new PickUser(user,false);
            pickUsers.add(pickUser);
        }
        //群已经有的用户
        //总是为空
//        exisMembers = new ArrayList<>();
        adapter = new ContactListAdapter(this,pickUsers,exisMembers);
        mLvContact.setAdapter(adapter);
        mLvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_pick);
                cb.setChecked(!cb.isChecked());
                // 更新数据
                PickUser pickuser = pickUsers.get(position);
                pickuser.setChecked(cb.isChecked());
                // 刷新列表数据
                adapter.notifyDataSetChanged();
            }
        });
        //保存的点击事件
        mTvSava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取已经选取好的联系人
                List<String> members = adapter.getexisMemBers();
                //进入创建新群页面
                Intent intent = new Intent();
                intent.putExtra("members", members.toArray(new String[0]));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initView() {
        mLvContact = (ListView) findViewById(R.id.lv_pick);
        mTvSava = (TextView) findViewById(R.id.tv_pick_save);
    }


}
