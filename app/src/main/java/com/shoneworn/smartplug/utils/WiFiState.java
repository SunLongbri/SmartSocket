package com.shoneworn.smartplug.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.shoneworn.smartplug.application.BaseApplication;
import com.shoneworn.smartplug.R;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/10 16:03.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class WiFiState {

    //判断当前WIFI的状态是否在开启的状态
    public boolean isWiFiActive() {
        ConnectivityManager connectivity = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void judgeWIFIState() {
        Boolean wifiState = isWiFiActive();
        if (!wifiState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseApplication.getContext());
            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(BaseApplication.getContext(), R.layout.open_wifi, null);
            dialog.setView(dialogView);
            Button mBtnWiFiOk = (Button) dialogView.findViewById(R.id.wifi_ok);
            mBtnWiFiOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    BaseApplication.getContext().startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
