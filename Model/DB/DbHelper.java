package com.lihao.amusement.Model.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lihao.amusement.Model.Dao.ContactTable;
import com.lihao.amusement.Model.Dao.InvitaTable;

/**
 * Created by hbm on 2017/5/31.
 * 作者：李浩
 * 时间：2017/5/31
 * 类的作用：xxxxxx
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    //创建数据库
    public DbHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建联系人的表
        db.execSQL(ContactTable.CREATE_TAB);
        //创建邀请信息的表
        db.execSQL(InvitaTable.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
