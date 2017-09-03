package com.lihao.amusement.Model.Dao;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

// 用户账号表
public class UserAccountTable {
        public static final String TAB_NAME = "tab_account";    // 表名
        public static final String COL_NAME = "name";
        public static final String COL_HXID = "hxid";
        public static final String COL_NICK = "nick";
        public static final String COL_PHOTO = "photo";

        public static final String CREATE_TAB = "create table "
                + TAB_NAME + " ("
                + COL_HXID + " text primary key, "
                + COL_NAME + " text,"
                + COL_NICK + " text,"
                + COL_PHOTO + " text);";
    }


