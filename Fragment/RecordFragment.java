package com.lihao.amusement.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihao.amusement.R;

/**
 * Created by hbm on 2017/5/30.
 * 作者：李浩
 * 时间：2017/5/30
 * 类的作用：xxxxxx
 */

public class RecordFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        return view;
    }

}
