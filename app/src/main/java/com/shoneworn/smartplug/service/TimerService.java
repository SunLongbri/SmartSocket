package com.shoneworn.smartplug.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.shoneworn.smartplug.activity.DetailActivity;
import com.shoneworn.smartplug.bean.DetailInfo;
import com.shoneworn.smartplug.network.TcpClientConnector;
import com.shoneworn.smartplug.command.SendCommand;


/**
 * Created by admin on 2018/8/31.
 */

public class TimerService extends Service {
    public static TcpClientConnector connector;
    private Handler handler = new Handler();
    private boolean isStop = false;

    private boolean trueStop = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.post(new Runnable() {
            @Override
            public void run() {
//                getBlackList();
                //用户离开此页面，则不再发送指令
                if (isStop && trueStop) {
                    return;
                }
                //如果用户进入到设备详情页，在没有获取到服务器相应的情况下3秒一发送指令
                //如果已经获得到服务器的响应，则7秒一发送指令
                if (isStop) {

//                    handler.postDelayed(this, 7 * 1000);
                } else {
//                    handler.postDelayed(this, 3 * 1000);
                }

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DetailInfo info = null;
    private SendCommand sendCommand = new SendCommand();
    public void getBlackList() {
        //进入到设备详情页的指令，发送蓝牙设备开关的指令

        //ReadV
        sendCommand.sendReadV();

        //ReadI
        sendCommand.sendReadI();

        //ReadF
        sendCommand.sendReadF();

        //ReadT
        sendCommand.sendReadT();

        sendCommand.sendReadPM();

     //   ReceiveCommand receiveCommand = new ReceiveCommand();
       // receiveCommand.recevieServer();

        isStop = true;

        DetailActivity detailActivity = new DetailActivity();
        //对详情页需要进行更新的数据进行更新
//        detailActivity.parseDetailData();

//        info = new DetailInfo();
//
//        String comCode = ReceiveCommand.mCommandData;
//
//        connector.setOnConnectLinstener(new TcpClientConnector.ConnectLinstener() {
//            @Override
//            public void onReceiveData(String data) {
//                CombineCommand.getCommand(data);
//                String comCode = parseCommand();
//                if (comCode.equals("1110")) {
//                    //读数据
//                    //析到的详情页解Power数据
//                    String mPower = parseData();
//                    System.out.println("详情页的mPower：" + mPower);
//                    info.setmDetailPower(mPower);
//                } else if (comCode.equals("1012")) {
//                    //解析到详情页的Current数据
//                    String mCurrent = parseData();
//                    System.out.println("详情页的mCurrent：" + mCurrent);
//                    info.setmDetailCurrent(mCurrent);
//                } else if (comCode.equals("1013")) {
//                    //解析到详情页的Voltage数据
//                    String mVoltage = parseData();
//                    System.out.println("详情页的mVoltage：" + mVoltage);
//                    info.setmDetailFrequence(mVoltage);
//                } else if (comCode.equals("1104")) {
//                    //解析到详情页的Frequence数据
//                    String mFrequence = parseData();
//                    System.out.println("详情页的mFrequence：" + mFrequence);
//                    info.setmDetailFrequence(mFrequence);
//                } else if (comCode.equals("1105")) {
//                    //解析到详情页的Temperature数据
//                    String mTemperature = parseData();
//                    System.out.println("详情页的mTemperature:" + mTemperature);
//                    info.setmDetailTemperature(mTemperature);
//
//                }
//            }
//        });
//
//
//        NotifyMsgEntity msgEntity = new NotifyMsgEntity();
//        msgEntity.setCode(Constants.NOTIFY_TO_DETAIL);
//
//        msgEntity.setData(info);
//        NotifyManager.getNotifyManager().notifyChange(msgEntity);
//        System.out.println("设备详情页即将更新的数据:" + info.toString());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
        trueStop = true;
    }
}
