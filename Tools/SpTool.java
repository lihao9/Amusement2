package com.lihao.amusement.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.lihao.amusement.MIApplication;

/**
 * Created by hbm on 2017/6/1.
 * 作者：李浩
 * 时间：2017/6/1
 * 类的作用：Sp存储数据工具
 */

public class SpTool {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpTool mSptool = new SpTool();
    private static SharedPreferences mSp = null;

    public static SpTool getmSptool(){
        if (mSp==null){
            mSp = MIApplication.getGlobalContext().getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return mSptool;
    }
    //保存数据
    public void putDate(String key,Object value){
        if(value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        }else if(value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }else if(value instanceof  Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    //读取String数据
    public String getString(String key,String defual){
        return mSp.getString(key,defual);
    }

    //获取int数据
    public int getInt(String key,int defual){
        return mSp.getInt(key,defual);
    }

    //获取boolean数据
    public boolean getBoolean(String key,boolean defual){
        return mSp.getBoolean(key,defual);
    }
}
