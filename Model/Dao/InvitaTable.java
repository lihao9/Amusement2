package com.lihao.amusement.Model.Dao;

/**
 * Created by hbm on 2017/5/31.
 * 作者：李浩
 * 时间：2017/5/31
 * 类的作用：xxxxxx
 */

public class InvitaTable {

    // 邀请信息的建表类
        public static final String TAB_NAME = "tab_invite";//邀请信息的表名

        public static final String COL_USER_NAME = "user_name"; // 用户名称
        public static final String COL_USER_HXID = "user_hxid"; // 用户id

        public static final String COL_GROUP_NAME = "group_name";   // 组名
        public static final String COL_GROUP_HXID = "group_hxid";   // 组id

        public static final String COL_REASON = "reason";   // 邀请原因
        public static final String COL_STATUS = "status";   // 邀请状态


        public static final String CREATE_TAB = "create table "
                + TAB_NAME + "("
                + COL_USER_HXID + " text primary key, "
                + COL_USER_NAME + " text, "
                + COL_GROUP_NAME + " text, "
                + COL_GROUP_HXID + " text, "
                + COL_REASON + " text, "
                + COL_STATUS + " integer);";

}
