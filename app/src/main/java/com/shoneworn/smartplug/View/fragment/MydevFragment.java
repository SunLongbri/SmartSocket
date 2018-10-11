package com.shoneworn.smartplug.View.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.shoneworn.smartplug.R;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by admin on 2018/8/30.
 */

public class MydevFragment extends Fragment implements Observer{

    private View view ;
    private TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mydev,null);
        tv = (TextView) view.findViewById(R.id.tv_text);
        NotifyManager.getNotifyManager().addObserver(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NotifyManager.getNotifyManager().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object data) {
        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }
        NotifyMsgEntity entity= (NotifyMsgEntity) data;
        int type =entity.getCode();
        if(NotifyManager.TYPE_MAIN==type){
            int code = (int)entity.getData();

            tv.setText(""+code);
        }
    }
}
