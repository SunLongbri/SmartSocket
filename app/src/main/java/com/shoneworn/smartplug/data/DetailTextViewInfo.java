package com.shoneworn.smartplug.data;

import android.widget.TextView;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/11 14:33.
 * <p/>
 * Description  :这个是设备详情页面各个组件的对象
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class DetailTextViewInfo {

    private TextView mTvPower;
    private TextView mTvCurrent;
    private TextView mTvVoltage;
    private TextView mTvFrequence;
    private TextView mTvTemperature;
    private TextView mTvRemainTime;

    public DetailTextViewInfo(TextView mTvPower, TextView mTvCurrent, TextView mTvVoltage, TextView mTvFrequence, TextView mTvTemperature, TextView mTvRemainTime) {
        this.mTvPower = mTvPower;
        this.mTvCurrent = mTvCurrent;
        this.mTvVoltage = mTvVoltage;
        this.mTvFrequence = mTvFrequence;
        this.mTvTemperature = mTvTemperature;
        this.mTvRemainTime = mTvRemainTime;
    }

    public TextView getmTvPower() {
        return mTvPower;
    }

    public void setmTvPower(TextView mTvPower) {
        this.mTvPower = mTvPower;
    }

    public TextView getmTvCurrent() {
        return mTvCurrent;
    }

    public void setmTvCurrent(TextView mTvCurrent) {
        this.mTvCurrent = mTvCurrent;
    }

    public TextView getmTvVoltage() {
        return mTvVoltage;
    }

    public void setmTvVoltage(TextView mTvVoltage) {
        this.mTvVoltage = mTvVoltage;
    }

    public TextView getmTvFrequence() {
        return mTvFrequence;
    }

    public void setmTvFrequence(TextView mTvFrequence) {
        this.mTvFrequence = mTvFrequence;
    }

    public TextView getmTvTemperature() {
        return mTvTemperature;
    }

    public void setmTvTemperature(TextView mTvTemperature) {
        this.mTvTemperature = mTvTemperature;
    }

    public TextView getmTvRemainTime() {
        return mTvRemainTime;
    }

    public void setmTvRemainTime(TextView mTvRemainTime) {
        this.mTvRemainTime = mTvRemainTime;
    }
}
