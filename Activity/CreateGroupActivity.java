package com.lihao.amusement.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.R;
import com.lihao.amusement.Tools.SingleTool;

public class CreateGroupActivity extends AppCompatActivity {
    private Button btnCreateGroup;
    private EditText etGroupName,etGroupDesc;
    private CheckBox cbIsPublic,cbInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
    }

    //初始化View
    private void initView() {
        btnCreateGroup = (Button) findViewById(R.id.bt_newgroup_create);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroupActivity.this,PickContactsActivity.class);
                startActivityForResult(intent,1);
            }
        });
        etGroupName = (EditText) findViewById(R.id.et_newgroup_name);
        etGroupDesc = (EditText) findViewById(R.id.et_newgroup_desc);
        cbIsPublic = (CheckBox) findViewById(R.id.cb_newgroup_public);
        cbInvite = (CheckBox) findViewById(R.id.cb_newgroup_invite);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            //创建新群
            createNewGroup(data.getExtras().getStringArray("members"));
        }
    }

    //创建新群
    private void createNewGroup(final String[] members) {
        final String groupName = etGroupName.getText().toString();
        final String groupDesc = etGroupDesc.getText().toString();

        //联网创建新群
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String reason = "申请加入群";
                EMGroupManager.EMGroupStyle groupStyle = null;
                //判断群公开--群邀请公开
                if (cbIsPublic.isChecked()){
                    //群公开
                    if (cbInvite.isChecked()){
                        //群邀请公开
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else {
                    if (cbInvite.isChecked()){
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                //群参数设置
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                options.maxUsers = 200;
                options.style = groupStyle;

                //创建群
                try {
                    EMClient.getInstance().groupManager()
                            .createGroup(groupName,groupDesc,members,reason,options);
                    //创建群成功--更新界面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        });

    }
}
