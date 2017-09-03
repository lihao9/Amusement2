package com.lihao.amusement.Model.DB;

import android.content.Context;

import com.lihao.amusement.Model.Dao.ContactDao;
import com.lihao.amusement.Model.Dao.InvitationDao;

/**
 * Created by hbm on 2017/6/1.
 * 作者：李浩
 * 时间：2017/6/1
 * 类的作用：数据库管理类
 */

public class DBManager {
    private final DbHelper dbHelper;
    private final ContactDao contactTableDao;
    private final InvitationDao inviteTableDao;

    public DBManager(Context context, String name) {
        dbHelper = new DbHelper(context, name);

        // 创建联系人操作类
        contactTableDao = new ContactDao(dbHelper);

        // 创建邀请信息操作类
        inviteTableDao = new InvitationDao(dbHelper);
    }

    public InvitationDao getInviteTableDao(){
        return inviteTableDao;
    }

    public ContactDao getContactTableDao(){
        return contactTableDao;
    }

    public void close() {
        dbHelper.close();
    }


}
