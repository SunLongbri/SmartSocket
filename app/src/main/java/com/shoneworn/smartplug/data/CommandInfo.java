package com.shoneworn.smartplug.data;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/9 13:26.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class CommandInfo {

    private String mCommandHead;
    private String mCommand;
    private String mCommandData;
    private String mCommandIndicator;
    private int mCommandLength;
    private String mCommandCRC16;

    public String getmCommandHead() {
        return mCommandHead;
    }

    public String getmCommand() {
        return mCommand;
    }

    public String getmCommandData() {
        return mCommandData;
    }

    public String getmCommandIndicator() {
        return mCommandIndicator;
    }

    public int getmCommandLength() {
        return mCommandLength;
    }

    public String getmCommandCRC16() {
        return mCommandCRC16;
    }

    public void setmCommandHead(String mCommandHead) {
        this.mCommandHead = mCommandHead;
    }

    public void setmCommand(String mCommand) {
        this.mCommand = mCommand;
    }

    public void setmCommandData(String mCommandData) {
        this.mCommandData = mCommandData;
    }

    public void setmCommandIndicator(String mCommandIndicator) {
        this.mCommandIndicator = mCommandIndicator;
    }

    public void setmCommandLength(int mCommandLength) {
        this.mCommandLength = mCommandLength;
    }

    public void setmCommandCRC16(String mCommandCRC16) {
        this.mCommandCRC16 = mCommandCRC16;
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "mCommandHead='" + mCommandHead + '\'' +
                ", mCommand='" + mCommand + '\'' +
                ", mCommandData='" + mCommandData + '\'' +
                ", mCommandIndicator='" + mCommandIndicator + '\'' +
                ", mCommandLength=" + mCommandLength +
                ", mCommandCRC16='" + mCommandCRC16 + '\'' +
                '}';
    }
}
