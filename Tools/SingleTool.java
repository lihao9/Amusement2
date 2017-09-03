package com.lihao.amusement.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.lihao.amusement.EventListener;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.Model.DB.DBManager;
import com.lihao.amusement.Model.Dao.UserInfoDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

public class SingleTool {
    private EventListener eventListener;
    private DBManager dbManager;
    private static SingleTool singleTool = new SingleTool();
    private UserInfoDao userInfoDao;
    private Context context;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    public static SingleTool getInstence(){
        if (singleTool==null){
            singleTool = new SingleTool();
        }
        return singleTool;
    }
    public void init(Context context){
        this.context = context;
        userInfoDao = new UserInfoDao(context);
        //开启全局监听
        eventListener = new EventListener(context);
    }

    public DBManager getDbManager(){
        return dbManager;
    }
    //获取全局的线程池
    public ExecutorService getGlobalExecutor(){
        return executorService;
    }

    //获取全局的用户信息操作类
    public UserInfoDao getCurrentAccountDao(){
        return userInfoDao;
    }
    public String getStringFromSp(String key,String defult){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String result = sp.getString(key,defult);
        return result;
    }
    public void putStringToSp(String key,String value){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    //用户登入成功
    public void loginSuccess(UserInfo user) {
        if (user==null){
            return;
        }else if (dbManager!=null){
            dbManager.close();
        }
        dbManager = new DBManager(context,user.getName());
    }

}
