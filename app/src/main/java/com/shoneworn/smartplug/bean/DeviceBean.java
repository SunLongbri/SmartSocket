package com.shoneworn.smartplug.bean;

import java.io.Serializable;

/**
 * Created by admin on 2018/8/30.
 */

public class DeviceBean implements Serializable {
    private boolean isReflash;
    private DetailInfo detailInfo;
    private int deviceId;
    private String img;
    private long createTime;
    private int position;
    private boolean toggleStatus = false;
    private String devName;
    private boolean isOpen;
    private int status;   // 状态 0 表示关状态， 1 表示 打开状态   2 表示出错状态

    public boolean isReflash() {
        return isReflash;
    }

    public void setReflash(boolean reflash) {
        isReflash = reflash;
    }

    public DetailInfo getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(DetailInfo detailInfo) {
        this.detailInfo = detailInfo;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

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

    public boolean isToggleStatus() {
        return toggleStatus;
    }

    public void setToggleStatus(boolean toggleStatus) {
        this.toggleStatus = toggleStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getPosition() {
        return position;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setPosition(int position) {
        this.position = position;
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
