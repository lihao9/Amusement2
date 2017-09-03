package com.lihao.amusement.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lihao.amusement.Model.Bean.PickUser;
import com.lihao.amusement.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/6/5.
 * 作者：李浩
 * 时间：2017/6/5
 * 类的作用：xxxxxx
 */

public class ContactListAdapter extends BaseAdapter {
    private Context context;
    private List<PickUser> pickUsers;
    private List<String> exisMembers;

    public ContactListAdapter(Context context,List<PickUser> pickUsers,List<String> exisMembers){
        this.context = context;
        this.pickUsers = pickUsers;
        this.exisMembers = exisMembers;
    }

//    public void refresh(List<EMGroup> emGroups){
//        notifyDataSetChanged();
//    }
    @Override
    public int getCount() {
        return pickUsers==null?0:pickUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = View.inflate(context, R.layout.item_pick,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_pick_name);
            holder.cbIsSelect = (CheckBox) convertView.findViewById(R.id.cb_pick);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(pickUsers.get(position).getUser().getName());
        holder.cbIsSelect.setChecked(pickUsers.get(position).isChecked());
        if (exisMembers.contains(pickUsers.get(position).getUser().getHxId())){
            holder.cbIsSelect.setChecked(true);
            pickUsers.get(position).setChecked(true);
        }
        return convertView;
    }

    //获取当前数据适配器中被选中的
    public List<String> getexisMemBers(){
        List<String> members = new ArrayList<>();
        for (PickUser pickUser:
             pickUsers) {
            if (pickUser.isChecked()){
                members.add(pickUser.getUser().getName());
            }
        }
        return members;
    }
    class ViewHolder{
        private TextView tvName;
        private CheckBox cbIsSelect;
    }
}
