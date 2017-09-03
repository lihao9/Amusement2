package com.lihao.amusement.Model.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Model.DB.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/5/31.
 * 作者：李浩
 * 时间：2017/5/31
 * 类的作用：联系人操作类
 */

public class ContactDao {
    private DbHelper helper;
    public ContactDao(DbHelper helper){
        this.helper = helper;
    }
    //获取所有的联系人
    public List<UserInfo> getAllContaxt(){
        List<UserInfo> contacts = new ArrayList<>();
        SQLiteDatabase readableDb = helper.getReadableDatabase();
        Cursor cursor = readableDb.query(ContactTable.TAB_NAME, null, null, null, null, null, null);
//        cursor.moveToFirst();
        while (cursor.moveToNext()){
            UserInfo contact = new UserInfo();
            contact.setHxId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
            contacts.add(contact);
        }
        return contacts;
    }

    //根据环信Id获取联系人
    public List<UserInfo> getContactWithHXID(List<String> HXids){
        if (HXids==null||HXids.size()<=0){
            return null;
        }
        List<UserInfo> contacts = new ArrayList<>();
        SQLiteDatabase readableDb = helper.getReadableDatabase();
        for (String hxid:HXids){
            //根据环信ID查询
            String sql = "select * from "+ContactTable.TAB_NAME+
                    " where "+ContactTable.COL_HXID+"=?";
            Cursor cursor = readableDb.rawQuery(sql, new String[]{hxid});
            if (cursor.moveToNext()){
                UserInfo contact = new UserInfo();

                contact.setHxId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
                contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
                contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
                contacts.add(contact);
            }
            //关闭游标
            cursor.close();
        }
        return contacts;

    }

    // 通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId) {
        // 校验
        if (hxId == null) {
            return null;
        }

        // 获取数据库
        SQLiteDatabase db = helper.getReadableDatabase();

        // 查询
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        UserInfo contact = null;

        if (cursor.moveToNext()) {
            contact = new UserInfo();

            contact.setHxId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            contact.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            contact.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

        }

        cursor.close();

        return contact;
    }

    //保存单个联系人
    public void saveContact(UserInfo userInfo,boolean isContact){
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_HXID, userInfo.getHxId());
        values.put(ContactTable.COL_NAME, userInfo.getName());
        values.put(ContactTable.COL_NICK, userInfo.getNick());
        values.put(ContactTable.COL_PHOTO, userInfo.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isContact ? 1 : 0);
        writableDatabase.replace(ContactTable.TAB_NAME,null,values);
    }

    // 保存联系人信息
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        // 校验
        if (contacts == null || contacts.size() <= 0) {
            return;
        }

        for (UserInfo contact : contacts) {
            saveContact(contact, isMyContact);
        }
    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId) {
        // 校验
        if (hxId == null) {
            return;
        }

        // 获取数据库
        SQLiteDatabase db = helper.getReadableDatabase();

        // 删除操作
        db.delete(ContactTable.TAB_NAME, ContactTable.COL_HXID + "=?", new String[]{hxId});
    }

}
