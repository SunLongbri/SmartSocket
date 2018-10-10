package com.shoneworn.smartplug.data;

import java.io.Serializable;

/**
 * Created by admin on 2018/8/30.
 */

public class DeviceBean implements Serializable {

    private DetailInfo detailInfo;

    public DetailInfo getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(DetailInfo detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId ;

    private String img;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status ;   // 状态 0 表示关状态， 1 表示 打开状态   2 表示出错状态

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    private String devName;
    private boolean isOpen;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "deviceId='" + deviceId + '\'' +
                ", img='" + img + '\'' +
                ", status=" + status +
                ", devName='" + devName + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
