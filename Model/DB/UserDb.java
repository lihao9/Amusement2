package com.lihao.amusement.Model.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lihao.amusement.Model.Dao.UserAccountTable;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

public class UserDb extends SQLiteOpenHelper {
    private static int version = 1;
    public UserDb(Context context) {
        super(context, "account.db", null, version);
    }

    //创建数据库时调用--建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
