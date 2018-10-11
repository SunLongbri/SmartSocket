package com.shoneworn.smartplug.adapter;

import android.view.View;
import android.widget.ImageView;

import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.RefreshToggle;
import com.shoneworn.smartplug.data.ToggleViewInfo;
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
 * Date of creation : 2018/10/11 15:17.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class RefreshList {

    private List<DeviceBean> mlist;
    private ParseCommand parseCommand;

    public RefreshList(List list) {
        this.mlist = list;

    }

    public void refreshToggle(String sendToggleState, List<DeviceBean> mlist, ToggleViewInfo toggleViewInfo, List<ToggleViewInfo> mToggleViewList) {
        String[] str = sendToggleState.split("_");
        String numFlash = str[0];
        //获取到的ABCD命令中的设备编号,第一个设备编号:00
        int num = Integer.parseInt(numFlash);
        int index = 0;

        //避免在二和四设备之间少三号设备的情况下，造成刷新错误的情况
        DeviceBean dev;
        //根据设备的名称进行刷新，防止因为设备掉线而出现该设备刷新成别的设备的状态
        for (int i = 0; i < mlist.size(); i++) {
            dev = mlist.get(i);
            int deviceNum = Integer.parseInt(dev.getDeviceId());
            if (deviceNum == num) {
                //因为实际
                index = i;
                break;
            }
        }

        String toggleState = str[1];
        int status = 0;
        if (toggleState.equals("0100")) {
            //开状态
            status = 1;
        } else if (toggleState.equals("0000")) {
            //关状态
            status = 0;
        } else if (toggleState.equals("0200")) {
            //异常状态
            status = 2;
        }
        setToggleImg(index, status, toggleViewInfo, mToggleViewList);
    }


    public static List<RefreshToggle> listToggleState = new ArrayList<>();
    public RefreshToggle toggleState;

    //设置单条目蓝牙设备名称前面的图标和开关的状态
    public void setToggleImg(int position, int status, ToggleViewInfo toggleViewInfo, List<ToggleViewInfo> mToggleViewList) {
        toggleState = new RefreshToggle();
        toggleState.setNum(position);

        toggleViewInfo = mToggleViewList.get(position);

        switch (status) {
            case 1:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_on);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_open);
                toggleState.setToggleState(1);
                break;
            case 0:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_off);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_normal);
                toggleState.setToggleState(0);
                break;
            case 2:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_error);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_error);
                toggleState.setToggleState(2);
                break;
            default:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_off);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_normal);
                break;
        }
        listToggleState.add(toggleState);
    }

    //设置单条目
    public void setStatusImg(int position, int status, View view) {

        DeviceBean bean = mlist.get(position);
        bean.setStatus(status);
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

    public void refreshOnLineTime(String parseData, RefreshList refreshList, ToggleViewInfo toggleViewInfo, List<ToggleViewInfo> mToggleViewList, List<CDABInfo> cdabInfoList) {
        parseCommand = new ParseCommand(parseData);
        String num = parseCommand.getCommandId() + "";

        //根据设备的id来查找当前设备在哪个Item的条目下
        int index = 0;
        for (int i = 0; i < mlist.size(); i++) {
            DeviceBean deviceBean = mlist.get(i);
            if (num.equals(deviceBean.getDeviceId())) {
                index = i;
                break;
            }
        }

        String state = "";
        if (parseData.length() == 36) {
            state = parseData.substring(12, 16);
        } else if (parseData.length() == 32) {
            state = parseData.substring(8, 12);
        }

        Boolean flage = true;
        int position = Integer.parseInt(num);

        if (state.equals("0100")) {
            refreshList.setToggleImg(index, 1, toggleViewInfo, mToggleViewList);
            flage = true;
        } else if (state.equals("0000")) {
            refreshList.setToggleImg(index, 0, toggleViewInfo, mToggleViewList);
            flage = false;
        } else if (state.equals("0200")) {
            refreshList.setToggleImg(index, 2, toggleViewInfo, mToggleViewList);
            //阻塞状态:
            flage = false;
        }

        //将自动上传的CDAB的设备时间进行刷新
        for (int i = 0; i < ONLINE_DEVICE; i++) {
            if (position == i) {
                CDABInfo cdabInfo;
                //从createDate中创造的数据，提取其中的一个
                cdabInfo = cdabInfoList.get(position);
                //设置时间和状态并进行存储
                cdabInfo.setToggleStatus(flage);
                //如果有CDAB更新了该设备，则将时间置为0
                cdabInfo.setCreateTime(System.currentTimeMillis() / 1000);

                cdabInfoList.set(position, cdabInfo);
            }
        }
    }

    public int getState(String parseData) {
        //从返回的命令中抽出出来的设备编号
        ParseCommand parseCommand = new ParseCommand(parseData);
        String index = parseCommand.getCommandId() + "";

        String toggleState = parseData.substring(12, 16);

        int state = 0;
        if (toggleState.equals("0100")) {
            state = 1;
        } else if (toggleState.equals("0000")) {
            state = 0;
        } else if (toggleState.equals("0200")) {
            state = 2;
        }
        return state;
    }

    public int getindexPosition(String index){
        //根据设备的id来查找当前设备在哪个Item的条目下
        int indexPosition = 0;
        for (int i = 0; i < mlist.size(); i++) {
            DeviceBean deviceBean = mlist.get(i);
            if (index.equals(deviceBean.getDeviceId())) {
                indexPosition = i;
                break;
            }
        }
        return indexPosition;
    }

}
