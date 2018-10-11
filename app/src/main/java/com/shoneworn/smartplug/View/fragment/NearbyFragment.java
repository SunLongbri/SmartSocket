package com.shoneworn.smartplug.View.fragment;

import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.View.activity.DetailActivity;
import com.shoneworn.smartplug.adapter.CommonListAdapter;
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.CommandInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.HomeTitleBean;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.network.TcpClientConnector;
import com.shoneworn.smartplug.utils.CombineCommand;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.utils.ReceiveCommand;
import com.shoneworn.smartplug.utils.SendCommand;
import com.shoneworn.smartplug.utils.UpdateAllData;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;
import static com.shoneworn.smartplug.utils.Constants.TCP_DOMIE;

/**
 * Created by admin on 2018/8/30.
 * <p>
 * 这个是显示设备Listview的页面
 */

public class NearbyFragment extends Fragment implements AdapterView.OnItemClickListener, HomeInterface, Observer {

    private View view;
    private ListView mListView;
    private CommonListAdapter mAdapter;
    // 设备集合
    public List<DeviceBean> mlist = new ArrayList<>();
    public static List<DeviceBean> mReturnList = null;
    public static List<DeviceBean> mCurrentList = new ArrayList<>();
    private HomeTitleBean titleBean;
    private boolean isStopGetData = false;
    private Handler mHandler;
    private TextView mDesc;
    private ReceiveCommand receiveCommand;
    private CommandInfo commandInfo;
    private SendCommand sendCommand;
    TcpClientConnector tcpClientConnector;
    private CombineCommand combineCommand;
    private String command;
    private ProgressBar mPbWait;
    private SharedPreferences sp;
    private SharedPreferences sharedPreferences;
    private EditText mEtText;
    private List<DeviceBean> cachelist;
    private AlertDialog alertDialog;
    private EditText mTvTime;
    private String mNodeIndex;
    private TextView mNode1;
    private TextView mNode2;
    private TextView mNode3;
    private TextView mNode4;
    private TextView mNode5;
    private TextView mNode6;
    private TextView mNode7;
    private TextView mTvNode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_nearby, null);
        sharedPreferences = getContext().getSharedPreferences("sp_config", Context.MODE_PRIVATE);

        NotifyManager.getNotifyManager().addObserver(this);
        commandInfo = new CommandInfo();
        sendCommand = new SendCommand();
        receiveCommand = new ReceiveCommand();
        new UpdateAllData();
        initView();
        judgeWIFIState();
        initData();
        waitData();
        return view;
    }

    public static List<DeviceBean> getMlist() {
        return mReturnList;
    }

    public void judgeWIFIState() {
        Boolean wifiState = isWiFiActive();
        if (!wifiState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(getContext(), R.layout.open_wifi, null);
            dialog.setView(dialogView);
            Button mBtnWiFiOk = (Button) dialogView.findViewById(R.id.wifi_ok);
            mBtnWiFiOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    //判断当前WIFI的状态是否在开启的状态
    public boolean isWiFiActive() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private Boolean mPbWaitDate = false;

    public void waitData() {

        //每隔10秒向Server端请求一下当前在线的设备数量
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isStopGetData) {
                    mHandler.removeCallbacks(this);
                    return;
                }
                if (mPbWaitDate) return;

                if (ONLINE_DEVICE == 0) {
                    mPbWait.setVisibility(View.VISIBLE);
                    mPbWaitDate = false;
                    sendCommand.sendReadOnLineDevice();
                } else {
                    mPbWait.setVisibility(View.INVISIBLE);
                    mPbWaitDate = true;
                }
                mHandler.postDelayed(this, 5 * 1000);
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {

        sendCommand.sendReadOnLineDevice();
        receiveCommand.recevieServer();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                if (msg.what == 101) {
                    titleBean = (HomeTitleBean) msg.getData().getSerializable("titlebean");
                    mDesc.setText(String.format("total:%d, off:%d, on:%d, error:%d", titleBean.getTotal(), titleBean.getOn(), titleBean.getOff(), titleBean.getErr()));
                }
            }
        };
    }

    public Boolean getStateStop = false;
    public static int num = 1;
    public AlertDialog dialog;

    public void initView() {
        System.out.println("当前的ip:" + TCP_DOMIE);
        Button btIP = (Button) view.findViewById(R.id.bt_ip);
        mListView = (ListView) view.findViewById(R.id.lv_nearby);
        mAdapter = new CommonListAdapter(getContext(), mlist, this);
        mDesc = (TextView) view.findViewById(R.id.tv_nearby_desc);
        mPbWait = (ProgressBar) view.findViewById(R.id.pb_wait);

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        btIP.setOnClickListener(new View.OnClickListener() {

            private EditText mEtText;
            private ImageButton mBtOK;

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.update_ip, null);
                // dialog.setView(view);// 将自定义的布局文件设置给dialog
                dialog.setView(dialogView, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
                mEtText = (EditText) dialogView.findViewById(R.id.et_ip);
                mEtText.setHint(TCP_DOMIE);
                //node节点
                mTvNode = (TextView) dialogView.findViewById(R.id.tv_select_node);
                mTvTime = (EditText) dialogView.findViewById(R.id.tv_select_time);
                ImageButton mBtOK = (ImageButton) dialogView.findViewById(R.id.bt_ok);

                TextView mTvErase = (TextView) dialogView.findViewById(R.id.tv_erase);
                mTvErase.setOnClickListener(new myOnclickListener());

                mTvNode.setOnClickListener(new myOnclickListener());
                mTvTime.setOnClickListener(new myOnclickListener());
                ImageButton mIbOk = (ImageButton) dialogView.findViewById(R.id.ib_schecule_ok);
                mIbOk.setOnClickListener(new myOnclickListener());

                mBtOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mCurrentIP = mEtText.getText().toString();

                        //对用户填入的IP进行判断
                        if (!mCurrentIP.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))")) {
                            Toast.makeText(getActivity(), "IP地址输入有误！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TcpClientConnector.getInstance().disconnect();
                        sharedPreferences.edit().putString("ip", mCurrentIP).apply();
                        TcpClientConnector.getInstance().creatConnect(mCurrentIP, Constants.TCP_PORT);

                        dialog.dismiss();
                    }
                });

                dialog.show();  //必须show一下才能看到对话框，跟Toast一样的道理
            }
        });

    }

    class myOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_select_node:

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    alertDialog = builder.create();
                    View alertView = View.inflate(getContext(), R.layout.select_node_list, null);
                    mNode1 = (TextView) alertView.findViewById(R.id.tv_node1);
                    mNode2 = (TextView) alertView.findViewById(R.id.tv_node2);
                    mNode3 = (TextView) alertView.findViewById(R.id.tv_node3);
                    mNode4 = (TextView) alertView.findViewById(R.id.tv_node4);
                    mNode5 = (TextView) alertView.findViewById(R.id.tv_node5);
                    mNode6 = (TextView) alertView.findViewById(R.id.tv_node6);
                    mNode7 = (TextView) alertView.findViewById(R.id.tv_node7);

                    if (cachelist == null) {
                        Toast.makeText(getContext(), "There is no choices!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //需要填入的数据
                    for (int i = 0; i < cachelist.size(); i++) {
                        DeviceBean deviceBean = cachelist.get(i);
                        String mDeviceName = deviceBean.getDevName();
                        if (i == 0) {
                            mNode1.setText(mDeviceName);
                            mNode1.setOnClickListener(new nodeClickListener());
                        } else if (i == 1) {
                            mNode2.setText(mDeviceName);
                            mNode2.setOnClickListener(new nodeClickListener());
                        } else if (i == 2) {
                            mNode3.setText(mDeviceName);
                            mNode3.setOnClickListener(new nodeClickListener());
                        } else if (i == 3) {
                            mNode4.setText(mDeviceName);
                            mNode4.setOnClickListener(new nodeClickListener());
                        } else if (i == 4) {
                            mNode5.setText(mDeviceName);
                            mNode5.setOnClickListener(new nodeClickListener());
                        } else if (i == 5) {
                            mNode6.setText(mDeviceName);
                            mNode6.setOnClickListener(new nodeClickListener());
                        } else if (i == 6) {
                            mNode7.setText(mDeviceName);
                            mNode7.setOnClickListener(new nodeClickListener());
                        }

                    }

                    alertDialog.setView(alertView);
                    alertDialog.show();

                    break;
                case R.id.tv_select_time:
                    System.out.println("点击了Time选项。。。");

                    break;
                case R.id.tv_erase:
                    System.out.println("点击了Erase选项。。。");
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    final AlertDialog alarmDialog = builder1.create();
                    View alarmtView = View.inflate(getContext(), R.layout.alert_dialog, null);
                    alarmDialog.setView(alarmtView);
                    Button alarmNo = (Button) alarmtView.findViewById(R.id.btn_alert_no);
                    Button alarmYes = (Button) alarmtView.findViewById(R.id.btn_alert_yes);
                    alarmNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alarmDialog.dismiss();
                        }
                    });
                    alarmYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SendCommand sendEraseCommand = new SendCommand();
                            sendEraseCommand.sendEraseNode();
                            alarmDialog.dismiss();
                        }
                    });
                    alarmDialog.show();
                    break;
                case R.id.bt_ok:
                    System.out.println("用户点击了OK键，但此处程序并不想回应用户，并向用户抛出了一个Exception!");

                case R.id.ib_schecule_ok:

                    int mTime = Integer.parseInt(mTvTime.getText().toString());
                    int mTimeHex = mTime & 0xff;

                    SendCommand sendCommand = new SendCommand();

                    sendCommand.sendControlNode(mCurrentNode, mTimeHex);

                    dialog.dismiss();
                    break;

            }
        }
    }

    private int mCurrentNode;

    class nodeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_node1:
                    mCurrentNode = Integer.parseInt(mNode1.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode1.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node2:
                    mCurrentNode = Integer.parseInt(mNode2.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode2.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node3:
                    mCurrentNode = Integer.parseInt(mNode3.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode3.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node4:
                    mCurrentNode = Integer.parseInt(mNode4.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode4.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node5:
                    mCurrentNode = Integer.parseInt(mNode5.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode5.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node6:
                    mCurrentNode = Integer.parseInt(mNode6.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode6.getText(), mCurrentNode).show();
                    break;
                case R.id.tv_node7:
                    mCurrentNode = Integer.parseInt(mNode7.getText().subSequence(12, 13).toString());
                    Toast.makeText(getContext(), "当前用户点击:" + mNode7.getText(), mCurrentNode).show();
                    break;
            }
            mNodeIndex = "0" + mCurrentNode;
            mTvNode.setText("Device " + mCurrentNode);
            alertDialog.dismiss();
        }
    }

    /* */
    public void updateDeviceItem() {
        long currentTime = System.currentTimeMillis() / 1000;
        cachelist = new ArrayList<>();
        //直接从Adapter中获取一个cdabInfoList，而这个数据是当前最新的数据
        List<CDABInfo> cdabList = mAdapter.getCdabList();
        int size = cdabList.size();
        for (int i = 0; i < size; i++) {
            //从当前最新的数据集合中获取其中的一个设备的信息
            CDABInfo cdabInfo = mAdapter.getCdabList().get(i);

            if (currentTime - cdabInfo.getCreateTime() < 15) {
                //从初始化的数据中拿取其中的一个数据
                DeviceBean bean = mCurrentList.get(i);
                if (cdabInfo.isToggleStatus()) {
                    bean.setOpen(true);
                    bean.setStatus(1);
                } else if (!cdabInfo.isToggleStatus()) {
                    bean.setOpen(false);
                    bean.setStatus(0);
                }
                //将所有的数据添加到一个新的集合中
                cachelist.add(bean);
                //CDAB在一分钟内进行更新的设备
            }
        }
        mlist.clear();
        mlist.addAll(cachelist);
        mAdapter.notifyDataSetChanged();
        updateTitleNum();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStopGetData = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //当用户点击条目的时候，蓝牙设备不再发送请求状态的指令
        getStateStop = true;

        NotifyMsgEntity msgEntity = new NotifyMsgEntity();
        msgEntity.setCode(NotifyManager.TYPE_MAIN);
        msgEntity.setData(position);
        NotifyManager.getNotifyManager().notifyChange(msgEntity);
        List<DeviceBean> list = mAdapter.getList();

        Intent intent = new Intent(getContext(), DetailActivity.class);
        Bundle devBundle = new Bundle();
        DeviceBean bean = mlist.get(position);
        devBundle.putSerializable("dev", bean);
        intent.putExtra("device", devBundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onSwich(boolean boo) {

        if (boo) {
            titleBean.addon();
        } else {
            titleBean.addoff();
        }
        mDesc.setText(String.format("total:%d, on:%d, off:%d, error:%d", titleBean.getTotal(), titleBean.getOn(), titleBean.getOff(), titleBean.getErr()));
    }

    public DeviceBean bean;
    int on = 0;
    int off = 0;
    int err = 0;

    public List<CDABInfo> cdabInfoList = new ArrayList<>();
//    public CDABInfo cdabInfo = new CDABInfo();

    //构建数据  模拟:向服务器端拉取数据  ONLINE_DEVICE:代表当前在线的设备
    private void createData() {

        titleBean = new HomeTitleBean();
        titleBean.setTotal(ONLINE_DEVICE);
        mlist.clear();
        bean = null;
        for (int i = 0; i < ONLINE_DEVICE; i++) {

            //需要记录当前每个Item创建的时间，因为一分钟之后没有CDAB自动更新的Item将会被清除掉
            long createTime = System.currentTimeMillis() / 1000;
            CDABInfo cdabInfo = new CDABInfo();
            cdabInfo.setCreateTime(createTime);
            cdabInfo.setPosition(i);
            cdabInfoList.add(cdabInfo);


            // 初始化设备
            bean = new DeviceBean();
            bean.setDeviceId("" + i);
            bean.setDevName("Device plug " + (i + 1));
            bean.setOpen(false);
            bean.setStatus(0);
            mlist.add(bean);
            off++;
            updateTittleState(on, off, err);
        }

        mCurrentList.addAll(mlist);
        mReturnList = mlist;
        // 更新数据
        mAdapter.notifyDataSetChanged();
        mAdapter.setCdabList(cdabInfoList);


        //每隔10秒刷新一下当前在线的设备数量
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //更新数据
                updateDeviceItem();
                mHandler.postDelayed(this, 10 * 1000);
            }
        }, 3 * 1000);

    }

    public void updateTittleState(int on, int off, int err) {

        titleBean.setOn(on);
        titleBean.setOff(off);
        titleBean.setErr(err);
        titleBean.setTotal(mlist.size());
        Message msg = new Message();

        msg.what = 101;
        Bundle bun = new Bundle();
        bun.putSerializable("titlebean", titleBean);
        msg.setData(bun);
        mHandler.sendMessage(msg);

    }

    @Override
    public void update(Observable observable, Object data) {

        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }

        NotifyMsgEntity entity = (NotifyMsgEntity) data;
        int type = entity.getCode();
        if (Constants.NOTIFY_TO_ONLINENUM == type && ONLINE_DEVICE == 0) {

            int num = (int) entity.getData();
            ONLINE_DEVICE = num / 2;
            System.out.println("当前在线设备数:" + ONLINE_DEVICE + "个");
            createData();
        } else if (Constants.NOTIFY_TO_TOGGLESTATE == type) {
            updateTitleNum();
        } else if (Constants.NOTIFY_TO_ITEM == type) {
            command = (String) entity.getData();
            combineCommand = new CombineCommand();
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
            combineCommand.getCommand(command);
//                }
//            },1000);

        }

    }

    //更新标题栏的各个数目
    public void updateTitleNum() {
        on = 0;
        off = 0;
        err = 0;

        for (int i = 0; i < mlist.size(); i++) {
            bean = mlist.get(i);
            if (bean.getStatus() == 0) {
                on++;
            } else if (bean.getStatus() == 1) {
                off++;
            } else if (bean.getStatus() == 2) {
                err++;
            }
        }
        updateTittleState(on, off, err);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tcpClientConnector != null) {

            tcpClientConnector.disconnect();
        }
        mlist.clear();
        cachelist.clear();
        ONLINE_DEVICE = 0;
    }
}
