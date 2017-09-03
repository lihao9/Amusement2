package com.lihao.amusement.Model.Bean;

/**
 * Created by hbm on 2017/5/31.
 * 作者：李浩
 * 时间：2017/5/31
 * 类的作用：群信息类
 */

public class GroupInfo {
        private String groupName;       // 群名称
        private String groupId;         // 群id
        private String invitePerson;     // 邀请人

        public GroupInfo() {
        }

        public GroupInfo(String groupName, String groupId, String invitePerson) {
            this.groupName = groupName;
            this.groupId = groupId;
            this.invitePerson = invitePerson;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getInvitePerson() {
            return invitePerson;
        }

        public void setInvitePerson(String invitePerson) {
            this.invitePerson = invitePerson;
        }

        @Override
        public String toString() {
            return "GroupInfo{" +
                    "groupName='" + groupName + '\'' +
                    ", groupId='" + groupId + '\'' +
                    ", invitePerson='" + invitePerson + '\'' +
                    '}';
        }


}
