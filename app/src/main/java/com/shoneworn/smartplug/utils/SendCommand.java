package com.shoneworn.smartplug.utils;


import android.content.SharedPreferences;

import com.shoneworn.smartplug.network.TcpClientConnector;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/6 18:52.
 * <p/>
 * Description  :用来向蓝牙端发送各种信息的工具类
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class SendCommand {

//    public TcpClientConnector connector;

//    public void init() {
//
//        connector = TcpClientConnector.getInstance();
//        connector.creatConnect(Constants.TCP_DOMIE, Constants.TCP_PORT);
//    }

    //在对某一结点发送定时关闭的命令
    public void sendControlNode(int mNodeIndex, int mTime) {
        mNodeIndex = mNodeIndex - 1;
        final byte[] commandV = new byte[13];
        //包头
        //"EF081610010400" + mNodeIndex + "01" + mTime + "0123 ";
        commandV[0] = (byte) 0xEF;
        commandV[1] = (byte) 0x08;
        //命令码
        commandV[2] = (byte) 0x16;
        commandV[3] = (byte) 0x10;
        //包标识
        commandV[4] = (byte) 0x01;
        //数据长度
        commandV[5] = (byte) 0x04;
        commandV[6] = (byte) 0x000;
        //数据内容
        commandV[7] = (byte) mNodeIndex;
        commandV[8] = (byte) 0x01;
        commandV[9] = (byte) mTime;
        commandV[10] = (byte) 0x00;

        //CRC16
        commandV[11] = (byte) 0xFF;
        commandV[12] = (byte) 0xFF;
        System.out.println("发送sendControlNode的指令为:" + byte2HexStr(commandV));
        TcpClientConnector.send(commandV);
    }

    //清除当前所有的节点
    public void sendEraseNode() {
        final byte[] commandV = new byte[9];
        //EF08    1310   01    0000   CRC16
        commandV[0] = (byte) 0xEF;
        commandV[1] = (byte) 0x08;
        //命令码
        commandV[2] = (byte) 0x13;
        commandV[3] = (byte) 0x10;
        //包标识
        commandV[4] = (byte) 0x01;
        //数据长度
        commandV[5] = (byte) 0x00;
        commandV[6] = (byte) 0x00;
        //CRC16
        commandV[7] = (byte) 0xFF;
        commandV[8] = (byte) 0xFF;
        System.out.println("发送sendControlNode的指令为:" + byte2HexStr(commandV));
        TcpClientConnector.send(commandV);
    }

    //-----------------------进入到设备详情页的指令，发送蓝牙设备开关的指令-------------------------------
    ////读电压
    public void sendReadV() {
        //发送Read V
        final byte[] commandV = new byte[13];
        //包头
        commandV[0] = (byte) 0xEF;
        commandV[1] = (byte) 0x08;
        //命令码
        commandV[2] = (byte) 0x11;
        commandV[3] = (byte) 0x10;
        //包标识
        commandV[4] = (byte) 0x01;
        //数据长度
        commandV[5] = (byte) 0x04;
        commandV[6] = (byte) 0x00;
        //数据内容
        commandV[7] = (byte) 0x00;
        commandV[8] = (byte) 0x02;
        commandV[9] = (byte) 0x00;
        commandV[10] = (byte) 0x00;
        //CRC16
        commandV[11] = (byte) 0xFF;
        commandV[12] = (byte) 0xFF;
        System.out.println("发送ReadV的指令为:" + byte2HexStr(commandV));
        TcpClientConnector.send(commandV);

    }

    public void sendReadI() {
        //发送Read I
        final byte[] commandI = new byte[13];
        //包头
        commandI[0] = (byte) 0xEF;
        commandI[1] = (byte) 0x08;
        //命令码
        commandI[2] = (byte) 0x11;
        commandI[3] = (byte) 0x10;
        //包标识
        commandI[4] = (byte) 0x01;
        //数据长度
        commandI[5] = (byte) 0x04;
        commandI[6] = (byte) 0x00;
        //数据内容
        commandI[7] = (byte) 0x00;
        commandI[8] = (byte) 0x02;
        commandI[9] = (byte) 0x01;
        commandI[10] = (byte) 0x00;
        //CRC16
        commandI[11] = (byte) 0xFF;
        commandI[12] = (byte) 0xFF;
        System.out.println("发送ReadI的指令为:" + byte2HexStr(commandI));
        TcpClientConnector.send(commandI);
    }

    public void sendReadF() {
        //发送Read F
        final byte[] commandF = new byte[13];
        //包头
        commandF[0] = (byte) 0xEF;
        commandF[1] = (byte) 0x08;
        //命令码
        commandF[2] = (byte) 0x11;
        commandF[3] = (byte) 0x10;
        //包标识
        commandF[4] = (byte) 0x01;
        //数据长度
        commandF[5] = (byte) 0x04;
        commandF[6] = (byte) 0x00;
        //数据内容
        commandF[7] = (byte) 0x00;
        commandF[8] = (byte) 0x02;
        commandF[9] = (byte) 0x03;
        commandF[10] = (byte) 0x00;
        //CRC16
        commandF[11] = (byte) 0xFF;
        commandF[12] = (byte) 0xFF;
        System.out.println("发送ReadF的指令为:" + byte2HexStr(commandF));
        TcpClientConnector.send(commandF);
    }

    public void sendReadT() {
        //发送Read T
        final byte[] commandT = new byte[13];
        //包头
        commandT[0] = (byte) 0xEF;
        commandT[1] = (byte) 0x08;
        //命令码
        commandT[2] = (byte) 0x11;
        commandT[3] = (byte) 0x10;
        //包标识
        commandT[4] = (byte) 0x01;
        //数据长度
        commandT[5] = (byte) 0x04;
        commandT[6] = (byte) 0x00;
        //数据内容
        commandT[7] = (byte) 0x00;
        commandT[8] = (byte) 0x02;
        commandT[9] = (byte) 0x04;
        commandT[10] = (byte) 0x00;
        //CRC16
        commandT[11] = (byte) 0xFF;
        commandT[12] = (byte) 0xFF;
        System.out.println("发送ReadT的指令为:" + byte2HexStr(commandT));
        TcpClientConnector.send(commandT);
    }

    public void sendReadPM() {
        //发送Read PM
        final byte[] commandPM = new byte[13];
        //包头
        commandPM[0] = (byte) 0xEF;
        commandPM[1] = (byte) 0x08;
        //命令码
        commandPM[2] = (byte) 0x11;
        commandPM[3] = (byte) 0x10;
        //包标识
        commandPM[4] = (byte) 0x01;
        //数据长度
        commandPM[5] = (byte) 0x04;
        commandPM[6] = (byte) 0x00;
        //数据内容
        commandPM[7] = (byte) 0x00;
        commandPM[8] = (byte) 0x02;
        commandPM[9] = (byte) 0xff;
        commandPM[10] = (byte) 0x00;
        //CRC16
        commandPM[11] = (byte) 0xFF;
        commandPM[12] = (byte) 0xFF;
        System.out.println("发送ReadPM的指令为:" + byte2HexStr(commandPM));
        TcpClientConnector.send(commandPM);
    }

    //-----------------------进入到主页面时，向Serer端获取当前在线设备的指令-------------------------------
    //发送读取当前在线设备的命令
    public void sendReadOnLineDevice() {
        final byte[] commandAll = new byte[13];
        commandAll[0] = (byte) 0xEF;
        commandAll[1] = (byte) 0x08;

        commandAll[2] = (byte) 0x15;
        commandAll[3] = (byte) 0x10;

        commandAll[4] = (byte) 0x01;

        commandAll[5] = (byte) 0x04;
        commandAll[6] = (byte) 0x00;

        commandAll[7] = (byte) 0x00;
        commandAll[8] = (byte) 0x02;
        commandAll[9] = (byte) 0xFF;
        commandAll[10] = (byte) 0x00;

        commandAll[11] = (byte) 0xFF;
        commandAll[12] = (byte) 0xFF;
        System.out.println("发送读取当前在线设备的命令为:" + byte2HexStr(commandAll));
        TcpClientConnector.send(commandAll);
    }

    //发送读取单个蓝牙开关状态的命令
    public void sendReadOneDeviceState(int position) {
        final byte[] command = new byte[12];
        command[0] = (byte) 0xEF;
        command[1] = (byte) position;
        command[2] = (byte) 0x10;
        command[3] = (byte) 0x10;
        command[4] = (byte) 0x01;
        command[5] = (byte) 0x03;
        command[6] = (byte) 0x00;
        command[7] = (byte) 0x02;
        command[8] = (byte) 0x00;
        command[9] = (byte) 0x01;
        command[10] = (byte) 0xFF;
        command[11] = (byte) 0xFF;
        System.out.println("发送读取单个蓝牙开关状态的命令为:" + byte2HexStr(command));
        TcpClientConnector.send(command);
    }

    //向服务端发送蓝牙设备开状态的指令
    public void sendOpenState(int position) {

        final byte[] commandOn = new byte[13];
        //包头
        commandOn[0] = (byte) 0xEF;
        commandOn[1] = (byte) 0x08;
        //命令码
        commandOn[2] = (byte) 0x10;
        commandOn[3] = (byte) 0x10;
        //包标识
        commandOn[4] = (byte) 0x01;
        //数据长度
        commandOn[5] = (byte) 0x04;
        commandOn[6] = (byte) 0x00;
        //数据内容
        commandOn[7] = (byte) position;
        commandOn[8] = (byte) 0x00;
        commandOn[9] = (byte) 0x01;
        commandOn[10] = (byte) 0x00;
        //CRC16
        commandOn[11] = (byte) 0xFF;
        commandOn[12] = (byte) 0xFF;
        System.out.println("向服务端发送蓝牙设备开状态的指令为:" + byte2HexStr(commandOn));
        TcpClientConnector.send(commandOn);
    }

    //发送蓝牙设备关状态的指令
    public void sendCloseState(int position) {
        final byte[] commandOff = new byte[13];
        //包头
        commandOff[0] = (byte) 0xEF;
        commandOff[1] = (byte) 0x08;
        //命令码
        commandOff[2] = (byte) 0x10;
        commandOff[3] = (byte) 0x10;
        //包标识
        commandOff[4] = (byte) 0x01;
        //数据长度
        commandOff[5] = (byte) 0x04;
        commandOff[6] = (byte) 0x00;
        //数据内容
        commandOff[7] = (byte) position;
        commandOff[8] = (byte) 0x00;
        commandOff[9] = (byte) 0x00;
        commandOff[10] = (byte) 0x00;
        //CRC16
        commandOff[11] = (byte) 0xFF;
        commandOff[12] = (byte) 0xFF;
        System.out.println("发送蓝牙设备关状态的指令为:" + byte2HexStr(commandOff));
        TcpClientConnector.send(commandOff);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

}
