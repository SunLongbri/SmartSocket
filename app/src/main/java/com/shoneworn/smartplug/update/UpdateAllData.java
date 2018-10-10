package com.shoneworn.smartplug.update;

import android.widget.ImageView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.shoneworn.smartplug.adapter.CommonListAdapter;
import com.shoneworn.smartplug.bean.DetailInfo;
import com.shoneworn.smartplug.bean.DetailTextView;
import com.shoneworn.smartplug.bean.DeviceBean;
import com.shoneworn.smartplug.interfaces.DetailTextviewInterface;
import com.shoneworn.smartplug.interfaces.UpdateData;
import com.shoneworn.smartplug.utils.Constants;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.shoneworn.smartplug.fragment.NearbyFragment.getMlist;
/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/9 17:46.
 * <p/>
 * Description  :
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class UpdateAllData implements Observer, UpdateData, DetailTextviewInterface {

    private DetailInfo detailInfo;
    private DetailTextView detailTextView;

    private CommonListAdapter mAdapter;
    private String mRemainTime;

    public UpdateAllData() {
        NotifyManager.getNotifyManager().addObserver(this);
    }

    @Override
    public void updateToggle(ImageView view) {

    }

    @Override
    public void detailTextView(DetailTextView detailTextView) {
        this.detailTextView = detailTextView;
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }
        NotifyMsgEntity entity = (NotifyMsgEntity) data;

        int type = entity.getCode();
        if (Constants.NOTIFY_TO_DETAIL_SHOW == type) {
            //解析详情页收到的数据
            String command = (String) entity.getData();
            parseDetailData(command);
        }

        if (Constants.NOTIFY_TO_UPDATE_DATA == type) {
            //更新从server端传来的CDAB数据
            String parseData = (String) entity.getData();
            parseDetailData(parseData);
        }
    }


    public DeviceBean bean;

    //更新Server 返回到客户端的CDAB数据
    public void parseDetailData(String parseData) {

        //更新按钮的状态
        updataToggleState(parseData);
        //更新详情页里面的数据
        updataDetailData(parseData);

    }

    public void updataDetailData(String parseData) {

        detailInfo = new DetailInfo();
        //截取到的设备编号
        String mDeviceIndex = parseData.substring(0, 4);
        //截取到五个值的参数序号
        String mSeqNumber = "";
        String mData = "";
        int mPageIndex = Integer.parseInt(parseData.substring(1, 2));
        detailInfo.setmPageIndex(mPageIndex);
        //主动上报和下发应答
        if (parseData.length() == 32) {
            mSeqNumber = parseData.substring(20, 24);
            mRemainTime = parseData.substring(12, 16);
            //截取出来的十六进制字符串
            mData = parseData.substring(24, 28);
        } else if (parseData.length() == 36) {
            mRemainTime = parseData.substring(16, 20);
            mSeqNumber = parseData.substring(24, 28);
            //截取出来的十六进制字符串
            mData = parseData.substring(28, 32);
        }

        //将mRemainTime进行大端与小端的调换
        String mRemainStart = mRemainTime.substring(0, 2);
        String mRemainEnd = mRemainTime.substring(2, 4);
        mRemainTime = mRemainEnd + mRemainStart;
        int mTime = (int) Long.parseLong(mRemainTime, 16);
        detailInfo.setmRemainTime(mTime);

        //因为采用的是大端模式，所以此处需要将头和尾进行调换
        String mStart = mData.substring(0, 2);
        String mEnd = mData.substring(2, 4);
        mData = mEnd + mStart;

        if (mSeqNumber.equals("0000")) {
            int mReadPM = Integer.parseInt(mData, 16);
            detailInfo.setmDetailVoltage(mReadPM + "");

        } else if (mSeqNumber.equals("0100")) {
            //readI
            //将其转换成十六进制
            int mReadI = Integer.parseInt(mData, 16);
            detailInfo.setmDetailCurrent(mReadI + "");

        } else if (mSeqNumber.equals("0200")) {

            //readV
            //将一个十六进制的数转换成一个十进制的数
            int mReadV = Integer.parseInt(mData, 16);
            detailInfo.setmDetailPower(mReadV + "");

        } else if (mSeqNumber.equals("0300")) {
            //readF
            //将其转换成十六进制
            int mReadF = Integer.parseInt(mData, 16);
            detailInfo.setmDetailFrequence(mReadF + "");

        } else if (mSeqNumber.equals("0400")) {
            //readT
            //将其转换成十六进制
            int mReadT = Integer.parseInt(mData, 16);
            detailInfo.setmDetailTemperature(mReadT + "");

        }

        NotifyMsgEntity mssgEntity = new NotifyMsgEntity();
        mssgEntity.setCode(Constants.NOTIFY_TO_DETAIL);

        mssgEntity.setData(detailInfo);
        NotifyManager.getNotifyManager().notifyChange(mssgEntity);
    }


    public void updataToggleState(String parseData) {

        //更新Toggle的状态
        String num = parseData.substring(0, 2);
        int len = Integer.parseInt(num, 16);
        //开关状态的命令
        String mToogleState = parseData.substring(8, 12);
        String sendToggleState = "";

        //mToogleState是否对设备的开启或者关闭是否执行成功
//        System.out.println("mToogleState:" + mToogleState);
        int mIndexPosition = Integer.parseInt(parseData.substring(1, 2));

        List<DeviceBean> mlist = getMlist();
        if (mlist.size() == 0) {
            return;
        }
        bean = mlist.get(mIndexPosition);
        //开关的按钮保持用户点击按钮后的状态,开关的按钮返回用户点击前的状态
        if (mToogleState.equals("0100")) {
            //正常状态
//            System.out.println("正常状态:" + mToogleState);
            sendToggleState = "0100";
            bean.setStatus(1);
        } else if (mToogleState.equals("0000")) {
            //关闭状态
            sendToggleState = "0000";
            bean.setStatus(0);
        } else if (mToogleState.equals("0200")) {
            //错误状态
            sendToggleState = "0000";
            bean.setStatus(2);
        }
        mlist.set(mIndexPosition, bean);
        sendToggleState = num + "_" + sendToggleState;

        NotifyMsgEntity msgEntity = new NotifyMsgEntity();
        msgEntity.setCode(Constants.NOTIFY_TO_TOGGLESTATE);

        msgEntity.setData(sendToggleState);
        NotifyManager.getNotifyManager().notifyChange(msgEntity);

    }
}
