package com.shoneworn.smartplug.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoneworn.smartplug.BaseApplication;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/11 10:44.
 * <p/>
 * Description  :此处主要是针对CDAB  parse  后的data进行提取,然后将整体数据存储到本地
 * <p/>          此处是对要求的进一步改进：要求的是在Listview界面，也能对设备详情页面里的数据进行刷新
 * <p/>          已经通过测试
 * Revision history :
 * <p/>
 * =============================================================
 */
public class ParseCommand {

    private String parseCommand;

    public ParseCommand(String parseCommand) {
        this.parseCommand = parseCommand;
    }


    /**
     * 从命令中包含设备id的四位命令码中提取到int类型的设备id
     * @return
     */
    public int getCommandId() {
        String commandId = parseCommand.substring(0, 4);
        int id = -1;
        //0100
        //如果截取到的第一位为：0，则说明当前设备的id为个位数
        if (commandId.substring(0, 1).equals("0")) {
            id = Integer.parseInt(commandId.substring(1, 2));
        } else {
            //如果截取到的第一位不为零，则说明当前设备的id大于9，小于153.注：传过来的数字为大端模式，而且是十六进制数
            id = Integer.parseInt(commandId.substring(0, 2), 16);
        }
        return id;
    }

    //获取帧类型
    public String getCommandFram() {
        return parseCommand.substring(4, 8);
    }

    //获取开关的指令
    public String getCommandOnOff() {
        return parseCommand.substring(8, 12);
    }

    //获取设备剩余的时间
    public String getCommandRemainTime() {
        return parseCommand.substring(12, 16);
    }

    //获取设备OverLoad的指令
    public String getCommandOverLoad() {
        return parseCommand.substring(16, 20);
    }

    //获取参数的序号
    public String getCommandIndex() {
        return parseCommand.substring(20, 24);
    }

    //获取参数的值
    public String getCommandValue() {

        //获取到参数的八位
        String mSeqNumber = parseCommand.substring(24, 32);
        //因为基本上只用前四位，后四位为0.
        String mData = mSeqNumber.substring(0, 4);

        //因为采用的是大端模式，所以此处需要将头和尾进行调换
        String mStart = mData.substring(0, 2);
        String mEnd = mData.substring(2, 4);
        mData = mEnd + mStart;

        //将替换后的16进制字符串进行转换成10进制整数
        int value = Integer.parseInt(mData, 16);

        //将数值除以10然后再取一位小数
        String strValue = value / 10 + "." + value % 10;
        return strValue;

    }

    //将该值保存到本地中
    public void saveLocalAsJson(int deviceId, String detailIndex, String strValue) {

        BaseApplication baseApplication = new BaseApplication();

        //根据设备的编号从本地文件中获取对应的值
        SharedPreferences spdata = BaseApplication.getContext().getSharedPreferences("sp_local_data", MODE_PRIVATE);

        String data = spdata.getString(deviceId + "", null);

        if (deviceId < 0 || TextUtils.isEmpty(detailIndex) || TextUtils.isEmpty(strValue) || TextUtils.isEmpty(data))
            return;

        Gson gson = new Gson();
        HashMap<String, String> dataMap = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());

        if (detailIndex.equals("0000")) {
            //mReadPM
            dataMap.put("power", strValue);

        } else if (detailIndex.equals("0100")) {
            //readI
            dataMap.put("current", strValue);

        } else if (detailIndex.equals("0200")) {
            //readV

            dataMap.put("voltage", strValue);
        } else if (detailIndex.equals("0300")) {
            //readF
            dataMap.put("frequence", strValue);

        } else if (detailIndex.equals("0400")) {
            //readT
            dataMap.put("temperature", strValue);
        }
        //将更新后的dataMap再次转换成Json字符串
        String updateMapData = gson.toJson(dataMap);
        //将更新后的json字符串存入到本地文件中
        spdata.edit().putString(deviceId + "", updateMapData).apply();
    }

}
