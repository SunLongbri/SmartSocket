package com.shoneworn.smartplug.network;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClientConnector {
    public static TcpClientConnector mTcpClientConnector;
    public static Socket mClient;
    private ConnectLinstener mListener;
    private Thread mConnectThread;

    public interface ConnectLinstener {
        void onReceiveData(String data);
    }

    public void setOnConnectLinstener(ConnectLinstener linstener) {
        //if(this.mListener==null)
        this.mListener = linstener;
    }

    public static TcpClientConnector getInstance() {
        if (mTcpClientConnector == null)
            mTcpClientConnector = new TcpClientConnector();
        return mTcpClientConnector;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (mListener != null) {
                        mListener.onReceiveData(msg.getData().getString("data"));
                    }
                    break;
            }
        }
    };

    public void creatConnect(final String mSerIP, final int mSerPort) {
//        if (mConnectThread == null) {
        mConnectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect(mSerIP, mSerPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mConnectThread.start();
//        }
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */
    private void connect(String mSerIP, int mSerPort) throws IOException {
        System.out.println("---------------------------------->connect");
        if (mClient == null) {
            mClient = new Socket(mSerIP, mSerPort);
        }
        InputStream inputStream = mClient.getInputStream();
        //直接将读到的数据转换成16进制字符串
        byte[] bytes = new byte[1024]; // 假设发送的字节数不超过 1024 个
        int size = -1;

        while ((size = inputStream.read(bytes)) != -1) {
            Log.i("=== htg size ===", size + "");
            String str = ConvertUtils.bytes2HexString(bytes);
            Log.i("=== 01 ===", str);

            String hex = bytesToHex(bytes, 0, size);
            Log.i("=== 02 ===", hex);

            Message message = new Message();
            message.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("data", hex);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

    }

    /**
     * 将 byte 数组转化为十六进制字符串
     *
     * @param bytes byte[] 数组
     * @param begin 起始位置
     * @param end   结束位置
     * @return byte 数组的十六进制字符串表示
     */
    private String bytesToHex(byte[] bytes, int begin, int end) {
        StringBuilder hexBuilder = new StringBuilder(2 * (end - begin));
        for (int i = begin; i < end; i++) {
            hexBuilder.append(Character.forDigit((bytes[i] & 0xF0) >> 4, 16)); // 转化高四位
            hexBuilder.append(Character.forDigit((bytes[i] & 0x0F), 16)); // 转化低四位
            hexBuilder.append(' '); // 加一个空格将每个字节分隔开
        }
        return hexBuilder.toString().toUpperCase();
    }



    /**
     * 发送数据
     *
     * @param command 需要发送的内容
     */
    public static void send(final byte[] command) {
        System.out.println("------------------------------------------->send");
        new Thread() {
            @Override
            public void run() {
                super.run();
                OutputStream outputStream = null;
                try {
                    if (mClient == null) {
                        return;
                    }
                    outputStream = mClient.getOutputStream();

                    outputStream.write(command);

                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() {
        try {
            if (mClient != null) {
                mClient.close();
                mClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

