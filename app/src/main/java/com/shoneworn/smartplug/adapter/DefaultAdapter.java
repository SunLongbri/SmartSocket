package com.shoneworn.smartplug.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.RefreshToggle;
import com.shoneworn.smartplug.data.ToggleViewInfo;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.utils.ParseCommand;

import java.util.ArrayList;
import java.util.List;

import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/11 14:52.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class DefaultAdapter extends BaseAdapter {

    private List<DeviceBean> mlist;

    public DefaultAdapter(List list){
        this.mlist = list;
    }

    @Override
    public int getCount() {
        return mlist == null ? 0 : mlist.size();
    }

    @Override
    public Object getItem(int position) {
        if (mlist == null || mlist.size() == 0) return null;
        return mlist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


}
