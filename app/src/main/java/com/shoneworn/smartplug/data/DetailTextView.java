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
 * Date of creation : 2018/9/9 20:12.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class DetailTextView {

    private TextView mTvPower;
    private TextView mTvCurrent;
    private TextView mTvVoltage;
    private TextView mTvFrequence;
    private TextView mTvTemperature;

    public TextView getmTvPower() {
        return mTvPower;
    }

    public TextView getmTvCurrent() {
        return mTvCurrent;
    }

    public TextView getmTvVoltage() {
        return mTvVoltage;
    }

    public TextView getmTvFrequence() {
        return mTvFrequence;
    }

    public TextView getmTvTemperature() {
        return mTvTemperature;
    }

    public void setmTvPower(TextView mTvPower) {
        this.mTvPower = mTvPower;
    }

    public void setmTvCurrent(TextView mTvCurrent) {
        this.mTvCurrent = mTvCurrent;
    }

    public void setmTvVoltage(TextView mTvVoltage) {
        this.mTvVoltage = mTvVoltage;
    }

    public void setmTvFrequence(TextView mTvFrequence) {
        this.mTvFrequence = mTvFrequence;
    }

    public void setmTvTemperature(TextView mTvTemperature) {
        this.mTvTemperature = mTvTemperature;
    }
}
