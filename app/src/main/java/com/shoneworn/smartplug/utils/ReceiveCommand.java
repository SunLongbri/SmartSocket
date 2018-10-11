package com.shoneworn.smartplug.utils;

import com.shoneworn.smartplug.data.CommandInfo;
import com.shoneworn.smartplug.network.TcpClientConnector;


/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/9/6 20:17.
 * <p/>
 * Description  :接收到服务端发送过来的指令
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class ReceiveCommand {
    CommandInfo commandInfo = null;
    CombineCommand combineCommand = null;
    SendCommand sendCommand = null;

    public CommandInfo recevieServer() {
        combineCommand = new CombineCommand();
        TcpClientConnector.getInstance().setOnConnectLinstener(new TcpClientConnector.ConnectLinstener() {

            @Override
            public void onReceiveData(String data) {
                commandInfo = new CommandInfo();
                String command = combineCommand.getCommand(data);
                if (command != null) {

                    //包头
                    commandInfo.setmCommandHead(combineCommand.parseHead());

                    //命令码
                    commandInfo.setmCommand(combineCommand.parseCommand());

                    //包标识
                    commandInfo.setmCommandIndicator(combineCommand.parseIndicator());

                    //包的数据长度
                    commandInfo.setmCommandLength(combineCommand.parseDataLength());

                    //包数据
                    commandInfo.setmCommandData(combineCommand.parseData());

                    //包的效验和
                    commandInfo.setmCommandCRC16(combineCommand.parseCRC16());

                }
            }
        });
        return commandInfo;
    }

}
