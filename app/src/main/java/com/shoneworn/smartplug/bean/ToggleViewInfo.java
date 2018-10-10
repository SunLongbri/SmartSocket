package com.shoneworn.smartplug.bean;

import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/11 08:26.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class ToggleViewInfo {
    private ImageView toggleImageView;
    private ImageView IconImageView;
    private int position;
    private ProgressBar mProgressBar;


    public ProgressBar getmProgressBar() {
        return mProgressBar;
    }

    public void setmProgressBar(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    public ImageView getIconImageView() {
        return IconImageView;
    }

    public void setIconImageView(ImageView iconImageView) {
        IconImageView = iconImageView;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageView getToggleImageView() {
        return toggleImageView;
    }

    public void setToggleImageView(ImageView toggleImageView) {
        this.toggleImageView = toggleImageView;
    }
}
