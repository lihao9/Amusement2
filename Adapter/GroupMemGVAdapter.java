package com.lihao.amusement.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihao.amusement.Model.Bean.UserInfo;
import com.lihao.amusement.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/6/7.
 * 作者：李浩
 * 时间：2017/6/7
 * 类的作用：群成员的适配器
 */

public class GroupMemGVAdapter extends BaseAdapter {

    private Context context;
    private boolean mIsDeleteModel;
    private OnGroupDetailListener mOnGroupDetailListener;
    // 你是群主 或者 你这个群是公开  你就可以添加和删除群成员
    private boolean isCanModify;
    private List<UserInfo> mUsers;
    public GroupMemGVAdapter(Context context, boolean isCanModify, OnGroupDetailListener
            mOnGroupDetailListener){
        this.context = context;
        this.mOnGroupDetailListener = mOnGroupDetailListener;
        this.isCanModify = isCanModify;
    }

    // 设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }
    // 获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }
    @Override
    public int getCount() {
        return mUsers==null?0:mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void initAnnDelect() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = View.inflate(context, R.layout.item_groupdetail,null);
            holder = new ViewHolder();
            holder.ivDelect = (ImageView) convertView.findViewById(R.id.iv_group_detail_delete);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_group_detail_name);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_group_detail_photo);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfo user = mUsers.get(position);
        //显示数据

        if (isCanModify){
            //群开放或者你是群主
            if (position==mUsers.size()-1){
                //显示减号
                if (mIsDeleteModel){
                    convertView.setVisibility(View.VISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.ivPhoto.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.ivDelect.setVisibility(View.GONE);
                    holder.tvName.setVisibility(View.INVISIBLE);
                }
            }else if (position==mUsers.size()-2){
                //加号显示
                if(mIsDeleteModel) {
                    convertView.setVisibility(View.INVISIBLE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    holder.ivPhoto.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.ivDelect.setVisibility(View.GONE);
                    holder.tvName.setVisibility(View.INVISIBLE);
                }
            }else {
                //群成员显示
                convertView.setVisibility(View.VISIBLE);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(user.getName());
                holder.ivPhoto.setImageResource(R.drawable.em_default_avatar);
                if(mIsDeleteModel) {
                    holder.ivDelect.setVisibility(View.VISIBLE);
                }else {
                    holder.ivDelect.setVisibility(View.GONE);
                }
            }
            //点击事件处理
            if (position==getCount()-1){
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteModel){
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if (position==mUsers.size()-2){
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //添加用户
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }else {
                //点击的是用户
                mOnGroupDetailListener.onDeleteMember(user);
            }
        }else {
            //普通用户
            if(position == getCount() - 1 || position == getCount() - 2) {
                convertView.setVisibility(View.GONE);
            }else {
                convertView.setVisibility(View.VISIBLE);

                // 名称
                holder.tvName.setText(user.getName());
                // 头像
                holder.ivPhoto.setImageResource(R.drawable.em_default_avatar);
                // 删除
                holder.ivDelect.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public void refresh(List<UserInfo> users) {
        mUsers = new ArrayList<>();
        if (users!=null&&users.size()>0){
            mUsers.clear();
            initAnnDelect();
            mUsers.addAll(0,users);
        }
        notifyDataSetChanged();
    }

    class ViewHolder{
        private ImageView ivDelect;
        private TextView tvName;
        private ImageView ivPhoto;
    }
    public interface OnGroupDetailListener{
        // 添加群成员方法
        void onAddMembers();

        // 删除群成员方法
        void onDeleteMember(UserInfo user);
    }
}
