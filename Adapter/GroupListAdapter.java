package com.lihao.amusement.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.lihao.amusement.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/6/5.
 * 作者：李浩
 * 时间：2017/6/5
 * 类的作用：xxxxxx
 */

public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private List<EMGroup> emGroups = new ArrayList<>();

    public GroupListAdapter(Context context){
        this.context = context;
    }

    public void refresh(List<EMGroup> emGroups){
        this.emGroups = emGroups;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return emGroups==null?0:emGroups.size();
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
            convertView = View.inflate(context, R.layout.item_group_list,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(emGroups.get(position).getGroupName());
        return convertView;
    }
    class ViewHolder{
        private TextView tvName;
    }
}
