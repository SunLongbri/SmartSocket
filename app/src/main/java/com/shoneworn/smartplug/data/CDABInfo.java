package com.shoneworn.smartplug.data;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/12 14:02.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class CDABInfo {

    private long createTime;
    private int position;

    public boolean isToggleStatus() {
        return toggleStatus;
    }

    public void setToggleStatus(boolean toggleStatus) {
        this.toggleStatus = toggleStatus;
    }

    private boolean toggleStatus =false;

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
}
