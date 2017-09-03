package com.lihao.amusement.Model.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Model.DB.UserDb;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：用户信息表的操作类
 */

public class UserInfoDao {
    private UserDb userDb;
    public UserInfoDao(Context context){
        userDb = new UserDb(context);
    }

    //插入用户
    public void addAccount(UserInfo user){
        SQLiteDatabase writableDb = userDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        // 操作数据库
        values.put(UserAccountTable.COL_HXID, user.getHxId());
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());
        writableDb.replace(UserAccountTable.TAB_NAME,null,values);
    }

    // 根据环信id获取所有用户信息
    public UserInfo getAccountByHxId(String hxId){

        // 获取数据库链接
        SQLiteDatabase db = userDb.getReadableDatabase();

        // 操作数据库
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " + UserAccountTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo account = null;

        if (cursor.moveToNext()) {
            account = new UserInfo();

            account.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            account.setHxId(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            account.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            account.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }

        // 关闭资源
        cursor.close();

        // 返回数据
        return account;
    }

}
