package com.shoneworn.smartplug.network;

/**
 * Created by Administrator on 2018/9/1.
 */

public interface RequestListener {

    void onSuccess(String msg);

    void onFailed(String errMsg);
}
