package com.lihao.amusement;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.lihao.amusement.Model.Bean.GroupInfo;
import com.lihao.amusement.Model.Bean.InviteInfo;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Tools.SingleTool;
import com.lihao.amusement.Tools.SpTool;

import java.util.List;

/**
 * Created by hbm on 2017/6/1.
 * 作者：李浩
 * 时间：2017/6/1
 * 类的作用：全局的监听
 */

public class EventListener {
    private static final String TAG = "EventListener";

    private Context context;
    private final LocalBroadcastManager mLBM;
    public EventListener(Context context){
        this.context = context;
        mLBM = LocalBroadcastManager.getInstance(context);
        //设置联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //设置群变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGrouphangeListener);
    }

    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人增加后执行
        @Override
        public void onContactAdded(String HXid) {
            //添加一个联系人
            SingleTool.getInstence().getDbManager().
                    getContactTableDao().
                    saveContact(new UserInfo(HXid),true);
            //发送广播到告诉其他模块
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //联系人被删除后执行
        @Override
        public void onContactDeleted(String hxid) {
            //删除一个联系人
            SingleTool.getInstence().getDbManager().
                    getContactTableDao().
                    deleteContactByHxId(hxid);
            //删除和这个用户有关的邀请信息
            SingleTool.getInstence().getDbManager().getInviteTableDao()
                    .delectInvatation(hxid);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接收到新邀请后执行
        @Override
        public void onContactInvited(String hxid, String reason) {
            //添加一条邀请信息
            InviteInfo invateInfo = new InviteInfo();
            invateInfo.setUser(new UserInfo(hxid));
            invateInfo.setReason(reason);
            invateInfo.setStatus(InviteInfo.InvitationStatus.NEW_INVITE);
            SingleTool.getInstence().getDbManager().
                    getInviteTableDao().
                    addInvitationInfo(invateInfo);
            //视图处理--红点显示
            Log.i(TAG, "接收到邀请信息");
            //处理红点
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE,true);

            //通知其他模块数据变化
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //好友接受邀请后执行
        @Override
        public void onFriendRequestAccepted(String hxid) {
            //邀请状态变化--添加邀请信息
            //创建邀请信息
            InviteInfo inviteInfo = new InviteInfo();
            inviteInfo.setUser(new UserInfo(hxid));
            inviteInfo.setStatus(InviteInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            SingleTool.getInstence()
                    .getDbManager()
                    .getInviteTableDao()
                    .addInvitationInfo(inviteInfo);
            //视图的处理

            //发送广播通知
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //好友拒绝邀请后执行
        @Override
        public void onFriendRequestDeclined(String s) {
            // 红点的处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE,true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

    private final EMGroupChangeListener emGrouphangeListener = new EMGroupChangeListener() {
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            // 数据更新
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.NEW_GROUP_INVITE);
            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 请求加入
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            // 数据更新
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 请求加入被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
// 更新数据
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);

            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            // 更新数据
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);

            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //邀请被接受
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            // 更新数据
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
// 更新数据
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //用户移除
        @Override
        public void onUserRemoved(String s, String s1) {

        }

        //群解散
        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        //自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // 更新数据
            InviteInfo invitationInfo = new InviteInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InviteInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            SingleTool.getInstence().getDbManager().getInviteTableDao().addInvitationInfo(invitationInfo);

            // 红点处理
            SpTool.getmSptool().putDate(SpTool.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };

}
