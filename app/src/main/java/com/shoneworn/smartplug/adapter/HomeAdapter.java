package com.shoneworn.smartplug.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.interfaces.ISwitchListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/9/14.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    private Context mContext;
    private List<DeviceBean> mlist =null;
    private ISwitchListener iSwitchListener;
//    private Map<Integer , Boolean> switchMap ;

    public HomeAdapter(Context context,List<DeviceBean> mlist,ISwitchListener iSwitchListener){
        this.mContext = context;
        this.mlist = mlist;
        this.iSwitchListener = iSwitchListener;
//        switchMap = new HashMap<>();
    }


    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_device,null);
        HomeViewHolder homeViewHolder = new HomeViewHolder(view);
        return homeViewHolder;
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.HomeViewHolder vh, final int position) {
       final DeviceBean bean = mlist.get(position);
       final boolean isOpen = bean.isOpen();
        changeSwitch(isOpen,vh.mSwitch);
//        switchMap.put(position,isOpen);
        if (mlist.get(position).getStatus() == 2) {
            setStatusImg(position, 2, vh.mImgStatus);
        } else {
            setStatusImg(position, isOpen ? 1 : 0, vh.mImgStatus);
        }

        vh.mTvDevName.setText(bean.getDevName());
        vh.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked =!isOpen;
//                switchMap.put(position,isChecked);
                iSwitchListener.onSwich(isChecked,position);

                if (mlist.get(position).getStatus() == 2) {
                    setStatusImg(position, 2, vh.mImgStatus);
                } else {
                    setStatusImg(position, isChecked ? 1 : 0, vh.mImgStatus);
                }
                changeSwitch(isChecked,vh.mSwitch);
            }
        });

    }


    private void changeSwitch(boolean isOpen,ImageView mSwitch){
        if (isOpen) {
            mSwitch.setBackgroundResource(R.mipmap.img_on);
        } else {
            mSwitch.setBackgroundResource(R.mipmap.img_off);
        }
    }

    private void setStatusImg(int position, int status, View view) {

        DeviceBean bean = mlist.get(position);
        bean.setStatus(status);
        mlist.set(position,bean);

        switch (status) {
            case 0:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_normal);
                break;
            case 1:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_open);
                break;
            case 2:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_error);
                break;
            default:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_normal);
                break;
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

class HomeViewHolder extends RecyclerView.ViewHolder{
    public ImageView mImgStatus;
    public ImageView mSwitch;
    public ImageView mImgSkip;
    public TextView mTvDevName;

    public HomeViewHolder(View itemView) {
        super(itemView);
        mImgStatus = (ImageView) itemView.findViewById(R.id.img_dev);
        //开关按钮
        mSwitch = (ImageView) itemView.findViewById(R.id.chk_dev_switch);
        mImgSkip = (ImageView) itemView.findViewById(R.id.img_skip);
        mTvDevName = (TextView) itemView.findViewById(R.id.tv_dev_name);
    }
}



}
