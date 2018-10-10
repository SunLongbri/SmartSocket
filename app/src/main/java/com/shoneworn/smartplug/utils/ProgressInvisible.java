package com.shoneworn.smartplug.utils;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shoneworn.smartplug.data.ProgressTimeInfo;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/26 10:30.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class ProgressInvisible {
    private Handler handler = new Handler();
    ProgressTimeInfo progressTimeInfo = null;
    ImageView toggleImage = null;
    ProgressBar progressBar = null;
    private boolean isStop = false;

    public ProgressInvisible(final ProgressTimeInfo progressTimeInfo, ImageView toggleImage, ProgressBar progressBar) {
        this.progressTimeInfo = progressTimeInfo;
        this.toggleImage = toggleImage;
        this.progressBar = progressBar;
    }

    public void whetherInvisible() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isStop) return;
                updateTime();
                handler.postDelayed(this, 1 * 1000);
            }
        });
    }

    public void updateTime() {
        long mCurrentTime = System.currentTimeMillis();
        long mCreatTime = progressTimeInfo.getmCreateTime();
        if (mCurrentTime - mCreatTime > 5000) {
            toggleImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
