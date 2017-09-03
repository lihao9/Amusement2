package com.lihao.amusement.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;

import static com.lihao.amusement.Tools.SingleTool.getInstence;

public class LoginActivity extends AppCompatActivity {

    private Button mBtnRegist,mBtnLogin;
    private EditText mEtUserName,mEtUserPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
    }

    private void setListener() {
        mBtnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册用户
                regist();
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户登入
                login();
            }
        });
    }

    //用户登入
    private void login() {
        final String name = mEtUserName.getText().toString();
        final String password = mEtUserPassword.getText().toString();
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            Toast.makeText(this, "用户名或者密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog pb = new ProgressDialog(LoginActivity.this);
        pb.setMessage("正在登入中");
        pb.show();
        getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(name, password, new EMCallBack() {
                    //登入成功
                    @Override
                    public void onSuccess() {
                        //修改数据
                        getInstence().loginSuccess(new UserInfo(name));

                        //提示用户
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.cancel();
                                Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                //当前登入的用户信息到数据库中
                                getInstence().getCurrentAccountDao().addAccount(new UserInfo(name));

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    //登入失败
                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.cancel();
                                Toast.makeText(LoginActivity.this, "登入失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    //正在登入
                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    //用户注册
    private void regist() {
        final String name = mEtUserName.getText().toString();
        final String password = mEtUserPassword.getText().toString();
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            Toast.makeText(this, "用户名或者密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog pb = new ProgressDialog(LoginActivity.this);
        pb.setMessage("正在注册中");
        pb.show();

        getInstence().getGlobalExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(name,password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.cancel();
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void initView() {
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnRegist = (Button) findViewById(R.id.btn_log_up);
        mEtUserName = (EditText) findViewById(R.id.et_username);
        mEtUserPassword = (EditText) findViewById(R.id.et_password);
    }
}
