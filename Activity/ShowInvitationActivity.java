package com.lihao.amusement.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.Constant;
import com.lihao.amusement.InvitationAccAndRej;
import com.lihao.amusement.Model.Bean.InviteInfo;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;
import com.lihao.amusement.Tools.SingleTool;

import java.util.List;

public class ShowInvitationActivity extends AppCompatActivity {


    private static final String TAG = "ShowInvitationActivity";
    private RecyclerView mRv;
    private RecyclerView.Adapter myRecycleViewAdapter;
    private List<InviteInfo> invitations;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver invitationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到邀请信息变化时发出的广播
            Log.i(TAG, "接收到邀请信息变化时发出的广播");
            //联系人邀请信息变化  || 群邀请信息变化
            if (intent.getAction() == Constant.CONTACT_INVITE_CHANGED || intent.getAction() == Constant.GROUP_INVITE_CHANGED) {
                invitations = SingleTool.getInstence().getDbManager().getInviteTableDao().getAllInviteInfos();
                myRecycleViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_invitation);
        initView();
        initDate();
    }

    //初始化数据
    private void initDate() {
        //获取当前用户的邀请
//        invitations = new ArrayList<InviteInfo>();
        invitations = SingleTool.getInstence().getDbManager().getInviteTableDao()
                .getAllInviteInfos();
        myRecycleViewAdapter = new MyRecycleViewAdapter();
        mRv.setAdapter(myRecycleViewAdapter);
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(invitationBroadcastReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(invitationBroadcastReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.recycle);
        mRv.setAdapter(myRecycleViewAdapter);
        mRv.setLayoutManager(new LinearLayoutManager(this));
    }
    class MyRecycleViewAdapter extends RecyclerView.Adapter<ShowInvitationActivity.MyRecycleViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(ShowInvitationActivity.this,R.layout.item_invitation,null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final InviteInfo invitationInfo = invitations.get(position);
            UserInfo userInfo = invitationInfo.getUser();
            if (userInfo != null) {
                //用户邀请
                holder.tvName.setText(invitationInfo.getUser().getName());
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);

                if (invitationInfo.getStatus() == InviteInfo.InvitationStatus.NEW_INVITE) {
                    if (invitationInfo.getReason() == null) {
                        holder.tvInfo.setText("添加好友");
                    } else {
                        holder.tvInfo.setText(invitationInfo.getReason());
                    }
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnReject.setVisibility(View.VISIBLE);
                    // TODO: 2017/6/5 --用接口去实现
                } else if (invitationInfo.getStatus() == InviteInfo.InvitationStatus.INVITE_ACCEPT) {
                    // 接受邀请
                    if (invitationInfo.getReason() == null) {
                        holder.tvInfo.setText("接受邀请");
                    } else {
                        holder.tvInfo.setText(invitationInfo.getReason());
                    }
                } else if (invitationInfo.getStatus() == InviteInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {
                    // 邀请被接受
                    if (invitationInfo.getReason() == null) {
                        holder.tvInfo.setText("邀请被接受");
                    } else {
                        holder.tvInfo.setText(invitationInfo.getReason());
                    }
                }
                //用户--接收邀请
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invitationAccAndRej.onAccept(invitationInfo);
                    }
                });
                holder.btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invitationAccAndRej.onReject(invitationInfo);
                    }
                });
            }else {
                //群邀请
                holder.tvName.setText(invitationInfo.getGroup().getInvitePerson());
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);

                // 显示原因
                switch(invitationInfo.getStatus()){
                    // 您的群申请请已经被接受
                    case GROUP_APPLICATION_ACCEPTED:
                        holder.tvInfo.setText("您的群申请请已经被接受");
                        break;
                    //  您的群邀请已经被接收
                    case GROUP_INVITE_ACCEPTED:
                        holder.tvInfo.setText("您的群邀请已经被接收");
                        break;

                    // 你的群申请已经被拒绝
                    case GROUP_APPLICATION_DECLINED:
                        holder.tvInfo.setText("你的群申请已经被拒绝");
                        break;

                    // 您的群邀请已经被拒绝
                    case GROUP_INVITE_DECLINED:
                        holder.tvInfo.setText("您的群邀请已经被拒绝");
                        break;

                    // 您收到了群邀请
                    case NEW_GROUP_INVITE:
                        holder.btnAccept.setVisibility(View.VISIBLE);
                        holder.tvInfo.setVisibility(View.VISIBLE);

                        // 接受邀请
                        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invitationAccAndRej.onInviteAccept(invitationInfo);
                            }
                        });

                        // 拒绝邀请
                        holder.btnReject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invitationAccAndRej.onInviteReject(invitationInfo);
                            }
                        });

                        holder.tvInfo.setText("您收到了群邀请");
                        break;

                    // 您收到了群申请
                    case NEW_GROUP_APPLICATION:
                        holder.btnAccept.setVisibility(View.VISIBLE);
                        holder.btnReject.setVisibility(View.VISIBLE);

                        // 接受申请
                        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invitationAccAndRej.onApplicationAccept(invitationInfo);
                            }
                        });

                        // 拒绝申请
                        holder.btnReject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invitationAccAndRej.onApplicationReject(invitationInfo);
                            }
                        });

                        holder.tvInfo.setText("您收到了群申请");
                        break;

                    // 你接受了群邀请
                    case GROUP_ACCEPT_INVITE:
                        holder.tvInfo.setText("你接受了群邀请");
                        break;

                    // 您批准了群申请
                    case GROUP_ACCEPT_APPLICATION:
                        holder.tvInfo.setText("您批准了群申请");
                        break;

                    // 您拒绝了群邀请
                    case GROUP_REJECT_INVITE:
                        holder.tvInfo.setText("您拒绝了群邀请");
                        break;

                    // 您拒绝了群申请
                    case GROUP_REJECT_APPLICATION:
                        holder.tvInfo.setText("您拒绝了群申请");
                        break;
                }
            }
            
        }

        @Override
        public int getItemCount() {
            return invitations.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView iv;
            private TextView tvName,tvInfo;
            private Button btnAccept,btnReject;
            public ViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.iv_invitation);
                tvName = (TextView) itemView.findViewById(R.id.tv_invitation_name);
                tvInfo = (TextView) itemView.findViewById(R.id.tv_invitation_info);
                btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
                btnReject = (Button) itemView.findViewById(R.id.btn_reject);
            }
        }
    }

    //刷新数据方法
    public void refreshDate(){
        invitations =  SingleTool.getInstence().getDbManager().getInviteTableDao()
                .getAllInviteInfos();
        myRecycleViewAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(invitationBroadcastReceiver);
    }


    private InvitationAccAndRej invitationAccAndRej = new InvitationAccAndRej() {
        // 联系人接受按钮的点击事件
        @Override
        public void onAccept(final InviteInfo invationInfo) {
//            Log.i(TAG, "联系人接受按钮的点击事件");
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager()
                                .acceptInvitation(invationInfo
                                    .getUser().getHxId());
                        //更新数据库
                        SingleTool.getInstence().getDbManager().getInviteTableDao()
                                .upDateState(InviteInfo.InvitationStatus.INVITE_ACCEPT,
                                        invationInfo.getUser().getHxId());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowInvitationActivity.this, "您接收了对方邀请", Toast
                                        .LENGTH_SHORT).show();
                                refreshDate();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowInvitationActivity.this, "接收对方邀请失败", Toast
                                        .LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //联系人拒绝按钮的点击事件
        @Override
        public void onReject(final InviteInfo invationInfo) {
            Log.i(TAG, "联系人拒绝按钮的点击事件");
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo
                                    .getUser().getHxId());
                        //更新数据库
                        SingleTool.getInstence().getDbManager().getInviteTableDao()
                                .delectInvatation(invationInfo.getUser().getHxId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowInvitationActivity.this, "您拒绝了对方邀请", Toast
                                        .LENGTH_SHORT).show();
                                refreshDate();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowInvitationActivity.this, "拒绝邀请失败", Toast
                                        .LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        //接受邀请按钮处理
        @Override
        public void onInviteAccept(final InviteInfo invationInfo) {
            Log.i(TAG, "接受邀请按钮处理");
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager()
                                .acceptApplication(invationInfo.getGroup().getGroupId()
                                        ,invationInfo.getGroup().getInvitePerson());

                        invationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        SingleTool.getInstence().getDbManager().getInviteTableDao()
                                .addInvitationInfo(invationInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowInvitationActivity.this, "您接收了邀请", Toast
                                        .LENGTH_SHORT).show();
                                refreshDate();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

        //拒绝邀请按钮处理
        @Override
        public void onInviteReject(final InviteInfo invationInfo) {
            Log.i(TAG, "拒绝邀请按钮处理");
            SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //拒绝成功
                        EMClient.getInstance().groupManager()
                                .declineApplication(invationInfo.getGroup().getGroupId()
                                        ,invationInfo.getGroup().getInvitePerson(),"你太丑");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onApplicationAccept(InviteInfo invationInfo) {
            //接受申请按钮处理
            Log.i(TAG, "接受申请按钮处理");


        }

        @Override
        public void onApplicationReject(InviteInfo invationInfo) {
            //拒绝申请按钮处理
            Log.i(TAG, "拒绝申请按钮处理");

        }
    };
}
