package com.lihao.amusement;

import com.lihao.amusement.Model.Bean.InviteInfo;

/**
 * Created by hbm on 2017/6/5.
 * 作者：李浩
 * 时间：2017/6/5
 * 类的作用：邀请信息被拒绝和接收的接口
 */

public interface InvitationAccAndRej {
    // 联系人接受按钮的点击事件
    void onAccept(InviteInfo invationInfo);

    // 联系人拒绝按钮的点击事件
    void onReject(InviteInfo invationInfo);

    // 接受邀请按钮处理
    void onInviteAccept(InviteInfo invationInfo);
    // 拒绝邀请按钮处理
    void onInviteReject(InviteInfo invationInfo);

    // 接受申请按钮处理
    void onApplicationAccept(InviteInfo invationInfo);
    // 拒绝申请按钮处理
    void onApplicationReject(InviteInfo invationInfo);
}
