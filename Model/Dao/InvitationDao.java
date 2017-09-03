package com.lihao.amusement.Model.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihao.amusement.Model.Bean.GroupInfo;
import com.lihao.amusement.Model.Bean.InviteInfo;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Model.DB.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/5/31.
 * 作者：李浩
 * 时间：2017/5/31
 * 类的作用：邀请信息操作类
 */

public class InvitationDao {
    private DbHelper helper;
    public InvitationDao(DbHelper helper){
        this.helper = helper;
    }

    //添加一条邀请信息
    public void addInvitationInfo(InviteInfo inviteInfo){
        SQLiteDatabase writableDb = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //邀请原因
        values.put(InvitaTable.COL_REASON,inviteInfo.getReason());
        //邀请状态
        values.put(InvitaTable.COL_STATUS,inviteInfo.getStatus().ordinal());
        //判断是用户邀请还是群邀请
        UserInfo user = inviteInfo.getUser();
        if (user==null){
            //群邀请
            values.put(InvitaTable.COL_GROUP_HXID,inviteInfo.getGroup().getGroupId());
            values.put(InvitaTable.COL_GROUP_NAME,inviteInfo.getGroup().getGroupName());
            values.put(InvitaTable.COL_USER_HXID,inviteInfo.getGroup().getInvitePerson());
        }else {
            //用户邀请
            values.put(InvitaTable.COL_USER_HXID,inviteInfo.getUser().getHxId());
            values.put(InvitaTable.COL_USER_NAME,inviteInfo.getUser().getHxId());
        }
        writableDb.replace(InvitaTable.TAB_NAME,null,values);
    }

    // 将int类型状态转换为邀请的状态
    private InviteInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InviteInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InviteInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InviteInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InviteInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InviteInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InviteInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InviteInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InviteInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InviteInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InviteInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InviteInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InviteInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }
    
    //获取所有的邀请信息--设置到邀请信息展示页面
    public List<InviteInfo> getAllInviteInfos(){
        List<InviteInfo> inviteInfos = new ArrayList<>();
        // 获取数据库链接
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from "+InvitaTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            InviteInfo invitationInfo = new InviteInfo();

            // 原因  状态
            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_REASON)));
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InvitaTable.COL_STATUS))));

            // 个人还是群
            String groupId = cursor.getString(cursor.getColumnIndex(InvitaTable.COL_GROUP_HXID));

            if (groupId == null) {
                UserInfo userInfo = new UserInfo();

                userInfo.setHxId(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_USER_NAME)));

                // 个人
                invitationInfo.setUser(userInfo);
            } else {
                // 群
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(groupId);
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InvitaTable.COL_USER_HXID)));

                invitationInfo.setGroup(groupInfo);
            }

            // 添加每一个对象
            inviteInfos.add(invitationInfo);
        }

        // 关闭cursor
        cursor.close();

        // 返回数据
        return inviteInfos;
        }

    //删除邀请新信息
    public void delectInvatation(String Hxid){
        SQLiteDatabase writableDb = helper.getWritableDatabase();
        writableDb.delete(InvitaTable.TAB_NAME,InvitaTable.COL_USER_HXID+" =?"
                ,new String[]{Hxid});
    }

    //更新邀请状态--根据邀请id更新邀请状态
    public void upDateState(InviteInfo.InvitationStatus invitationStatus, String hxId){
        // 获取数据库链接
        SQLiteDatabase db = helper.getWritableDatabase();

        // 执行更新操作
        ContentValues values = new ContentValues();
        values.put(InvitaTable.COL_STATUS, invitationStatus.ordinal());

        db.update(InvitaTable.TAB_NAME, values, InvitaTable.COL_USER_HXID + "=?", new String[]{hxId});
    }
}
