package com.lihao.amusement.Model.Bean;

/**
 * Created by hbm on 2017/6/6.
 * 作者：李浩
 * 时间：2017/6/6
 * 类的作用：联系人--群内
 */

public class PickUser {
    private UserInfo user;      // 联系人
    private boolean isChecked;  // 是否被选择的标记

    public PickUser(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "PickUser{" + "user=" + user + ", isChecked=" + isChecked + '}';
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
