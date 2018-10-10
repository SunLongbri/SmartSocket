package com.shoneworn.smartplug.bean;

/**
 * Created by Administrator on 2018/9/1.
 */

public class DetailInfo {

    //电量
    private String mDetailPower;
    private String mDetailCurrent;
    private String mDetailVoltage;
    private String mDetailFrequence;
    private String mDetailTemperature;
    private int mRemainTime;
    private int mPageIndex;


    public int getmPageIndex() {
        return mPageIndex;
    }

    public void setmPageIndex(int mPageIndex) {
        this.mPageIndex = mPageIndex;
    }

    public int getmRemainTime() {
        return mRemainTime;
    }

    public void setmRemainTime(int mRemainTime) {
        this.mRemainTime = mRemainTime;
    }

    public String getmDetailPower() {
        return mDetailPower;
    }

    public String getmDetailCurrent() {
        return mDetailCurrent;
    }

    public String getmDetailVoltage() {
        return mDetailVoltage;
    }

    public String getmDetailFrequence() {
        return mDetailFrequence;
    }

    public String getmDetailTemperature() {
        return mDetailTemperature;
    }

    public void setmDetailPower(String mDetailPower) {
        this.mDetailPower = mDetailPower;
    }

    public void setmDetailCurrent(String mDetailCurrent) {
        this.mDetailCurrent = mDetailCurrent;
    }

    public void setmDetailVoltage(String mDetailVoltage) {
        this.mDetailVoltage = mDetailVoltage;
    }

    public void setmDetailFrequence(String mDetailFrequence) {
        this.mDetailFrequence = mDetailFrequence;
    }

    public void setmDetailTemperature(String mDetailTemperature) {
        this.mDetailTemperature = mDetailTemperature;
    }

    @Override
    public String toString() {
        return "DetailInfo{" +
                "mDetailPower='" + mDetailPower + '\'' +
                ", mDetailCurrent='" + mDetailCurrent + '\'' +
                ", mDetailVoltage='" + mDetailVoltage + '\'' +
                ", mDetailFrequence='" + mDetailFrequence + '\'' +
                ", mDetailTemperature='" + mDetailTemperature + '\'' +
                '}';
    }
}
