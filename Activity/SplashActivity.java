package com.lihao.amusement.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;

import com.lihao.amusement.Tools.SingleTool;

//引导页面
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()){
                return;
            }else {
                enterToLoginOrMain();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    private void enterToLoginOrMain() {
        SingleTool.getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                boolean mIsLogin = EMClient.getInstance().isLoggedInBefore();
                if (mIsLogin){
                    //已经登入--2秒后进入到主页面
                    //获取登入的用户信息
                    UserInfo userInfo = SingleTool.getInstence().
                            getCurrentAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    if (userInfo==null){
                        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        Log.i(TAG, "登入成功");
                        SingleTool.getInstence().loginSuccess(userInfo);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    //没有登入--2秒后进入登入界面
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
