package com.shoneworn.smartplug.manager;

import com.shoneworn.smartplug.bean.DeviceBean;
import com.shoneworn.smartplug.bean.TitleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/10 10:24.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class DeviceManager {
    public List<DeviceBean> deviceBeanList = new ArrayList<>();

    public List<DeviceBean> createDeviceItems(int num) {
        for (int i = 0; i < num; i++) {
            DeviceBean bean = null;
            //需要记录当前每个Item创建的时间，因为一分钟之后没有CDAB自动更新的Item将会被清除掉
            long createTime = System.currentTimeMillis() / 1000;
            // 初始化设备
            bean = new DeviceBean();
            bean.setDeviceId(i);
            bean.setDevName("Device plug " + (i + 1));
            bean.setOpen(false);
            bean.setCreateTime(createTime);
            bean.setPosition(i);
            bean.setStatus(0);
            deviceBeanList.add(bean);
        }
        return deviceBeanList;
    }

    public TitleBean getTitleNum() {
        int on = 0;
        int off = 0;
        int err = 0;
        TitleBean titleBean;
        for (DeviceBean deviceBean : deviceBeanList) {
            if (deviceBean.getStatus() == 0) {
                off++;
            } else if (deviceBean.getStatus() == 1) {
                on++;
            } else {
                err = 0;
            }
        }
        titleBean = new TitleBean(on, off, err);
        return titleBean;
    }


    public List<DeviceBean> getDeviceBeanList() {
        return deviceBeanList;
    }

    public void setDeviceBeanList(List<DeviceBean> deviceBeanList) {
        this.deviceBeanList = deviceBeanList;
    }


    public void updateDeviceStatusById(int id, int status) {
        for (DeviceBean deviceBean : deviceBeanList) {
            if (deviceBean.getDeviceId() == id) {
                deviceBean.setReflash(false);
                deviceBean.setStatus(status);
                break;
            }
        }
    }

    public void updateDeviceTimeById(int id) {
        for (DeviceBean deviceBean : deviceBeanList) {
            if (deviceBean.getDeviceId() == id) {
                deviceBean.setCreateTime(System.currentTimeMillis());
                break;
            }
        }
    }
}
