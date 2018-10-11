package com.shoneworn.smartplug.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/5 13:14.
 * <p/>
 * Description  :将接受到的数据进行解析，主要是根据长度进行解析，如果数据长度不满足要求，则一直等待，
 * 报证有一条完整的包命令.如果一条帧命令包含多条命令，则进行回调，报证每一条命令都得到处理。
 * <p/>
 * <p/>
 * Revision history :当接受到的命令为一个四分之一或者二分之一帧的时候，将这些帧命令组合起来，拼成一条
 * 完整的命令
 * <p/>
 * =============================================================
 */
public class CombineCommand {

    private int dataStart;
    private int dataEnd;
    private String parseData;
    private String commandCode;
    private String subData = "";
    private String completeCommand;
    private int dataLength;
    private Boolean isHead = false;
    private Boolean addHead = false;
    private String commandHead;
    private String commandMark;
    private int mLength;
    private String mCrc16;
    private int mCurrentNum = 0;

    //返回的是一条完整的命令
    public String getCommand(String data) {

        System.out.println("接收到的原始data:" + data);
        String comStr = "";
        String newData = data.replace(" ", "");
        comStr = newData;
        String command = "";
        try {
            command = newData.substring(4, 8);
        } catch (Exception e) {
            Log.d("command的错误为:", e.toString());
        }

        //注册机
        if (command.equals("1010") && newData.length() > 54) {
            newData = newData.substring(0, 54);
            String subCommand = comStr.substring(54, comStr.length());
            NotifyMsgEntity msgEntity = new NotifyMsgEntity();
            msgEntity.setCode(Constants.NOTIFY_TO_ITEM);
            msgEntity.setData(subCommand);
            NotifyManager.getNotifyManager().notifyChange(msgEntity);
        }
        if (command.equals("CDAB") && newData.length() > 50) {
            newData = newData.substring(0, 50);
            String subCommand = comStr.substring(50, comStr.length());

            NotifyMsgEntity msgEntity = new NotifyMsgEntity();
            msgEntity.setCode(Constants.NOTIFY_TO_ITEM);
            msgEntity.setData(subCommand);
            NotifyManager.getNotifyManager().notifyChange(msgEntity);

        } else if (command.equals("1110") && newData.length() > 54) {
            newData = newData.substring(0, 54);
        }

        if (isHead) {
            newData = "EF08" + newData;
            addHead = true;
        }
        //当接受到的命令可能包含包头的帧命令
        if (newData.contains("EF08")) {
            isHead = true;

            if (addHead && isHead) {
                int len = newData.length();
                newData = newData.substring(4, len);
            }
            try {

                dataLength = newData.length();
                if (dataLength <= 16) {
                    //不是一个完整包，就一直拼包
                    subData = subData + newData;

                } else {
                    subData = newData;
                    int start = subData.indexOf("EF08");
                    commandHead = "EF08";
//                    System.out.println("解析到的包头：" + commandHead);
                    //包头
                    commandCode = subData.substring(start + 4, start + 8);
//                    System.out.println("解析到的命令码:" + commandCode);

                    //包标识
                    commandMark = subData.substring(start + 8, start + 10);
//                    System.out.println("解析到的包标识:" + commandMark);

                    //解析到的数据长度
                    int lengStart = start + 10;
                    int lengEnd = start + 14;
                    String dataLength = subData.substring(lengStart, lengEnd);
//                    System.out.println("解析到的String型数据长度:" + dataLength);
                    //dataLength : 04 00  大端模式：高字节在后,低字节在前.
                    dataLength = subData.substring(lengStart, lengEnd - 2);

                    //解析到的数据长度
                    mLength = (int) Long.parseLong(dataLength, 16);

//                    System.out.println("解析到的Int型数据长度:" + mLength);
                    if (mLength > 0) {
                        //报数据的长度大于0
                        //解析到的包数据
                        dataStart = lengEnd;

                        dataEnd = lengEnd + mLength * 2;

//                        System.out.println("subData=" + subData);
//                        System.out.println("subData.length=" + subData.length());

                        //CRC16d的值为:4
                        if (subData.length() < dataEnd + 4) {
                            //如果数据长度不满足规定的长度，就一直拼包命令
                            completeCommand = completeCommand + subData;
                        } else if (subData.length() == dataEnd + 4) {
                            completeCommand = subData;
                            parseData = completeCommand.substring(dataStart, dataEnd);

//                            System.out.println("解析到的包数据:" + parseData);

                            //效验和
                            mCrc16 = completeCommand.substring(dataEnd, dataEnd + 4);
//                            System.out.println("效验和crc16=" + mCrc16);
                            //一个完整的包命令已经完成，则将subdata清空，等待下一次拼一个完整的包命令
                            subData = "";
                            completeCommand = "";
                            Handler handler = new Handler(Looper.getMainLooper());

                            String commandStr = commandHead + commandCode + commandMark + dataLength + parseData + mCrc16;
                            //发送相应的消息，代表当前的数据已经解析完毕了,已经解析完毕了

                            if (commandCode.equals("1110")) {
                                //代表详情页的数据已经解析完毕
                                //使用NotifyMsgEntity来进行唤醒，代替了Handler。
                                NotifyMsgEntity msgEntity = new NotifyMsgEntity();
                                msgEntity.setCode(Constants.NOTIFY_TO_DETAIL_SHOW);
                                msgEntity.setData(parseData);
                                NotifyManager.getNotifyManager().notifyChange(msgEntity);
                            } else if (commandCode.equals("1510")) {
                                //代表获取到当前在线设备总数的数据已经解析完毕
                                NotifyMsgEntity msgEntity = new NotifyMsgEntity();
                                msgEntity.setCode(Constants.NOTIFY_TO_ONLINENUM);
                                msgEntity.setData(mLength);
                                NotifyManager.getNotifyManager().notifyChange(msgEntity);
                            } else if (commandCode.equals("1010")) {
                                //发送控制蓝牙设备开关状态的指令后回传的指令
                                NotifyMsgEntity msgEntity = new NotifyMsgEntity();
                                msgEntity.setCode(Constants.NOTIFY_TO_ONOFF);
                                msgEntity.setData(parseData);
                                NotifyManager.getNotifyManager().notifyChange(msgEntity);
                            } else if (commandCode.equals("CDAB")) {
                                //Server自动向服务端发送需要更新的数据
                                NotifyMsgEntity msgEntity = new NotifyMsgEntity();
                                msgEntity.setCode(Constants.NOTIFY_TO_UPDATE_DATA);
                                msgEntity.setData(parseData);
                                NotifyManager.getNotifyManager().notifyChange(msgEntity);
                            }

                            subData = "";
                            newData = "";
                            return commandStr;
                        } else {
                            subData = newData;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //返回解析到的包头数据
    public String parseHead() {
        return commandHead;
    }

    //返回解析到的包命令码
    public String parseCommand() {
        return commandCode;
    }

    //返回解析到的包标识
    public String parseIndicator() {
        return commandMark;
    }

    //返回解析到的包长度
    public int parseDataLength() {
        return mLength;
    }

    //返回解析到的包数据
    public String parseData() {
        return parseData;
    }

    //返回解析到的效验和
    public String parseCRC16() {
        return mCrc16;
    }

}
