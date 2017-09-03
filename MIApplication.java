package com.lihao.amusement;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.lihao.amusement.Tools.SingleTool;

/**
 * Created by hbm on 2017/5/29.
 * 作者：李浩
 * 时间：2017/5/29
 * 类的作用：xxxxxx
 */

public class MIApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions option = new EMOptions();
        option.setAutoAcceptGroupInvitation(false);
        option.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this,option);
        SingleTool.getInstence().init(this);
        context = this;
    }

    public static Context getGlobalContext() {
        return context;
    }
}
