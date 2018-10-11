package com.shoneworn.smartplug.View.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.View.activity.DetailActivity;
import com.shoneworn.smartplug.adapter.CommonListAdapter;
import com.shoneworn.smartplug.adapter.HomeAdapter;
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.CommandInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.HomeTitleBean;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.interfaces.ISwitchListener;
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

/**
 * Created by admin on 2018/8/30.
 * <p>
 * 这个是显示设备Listview的页面
 */

public class HomeFragment extends Fragment implements ISwitchListener, Observer {

    private View view;
    private RecyclerView mListView;
    private HomeAdapter mAdapter;
    private List<DeviceBean> mlist = new ArrayList<>();
    private List<DeviceBean> mCurrentList = new ArrayList<>();
    private HomeTitleBean titleBean;
    private boolean isStopGetData = false;
    private Handler mHandler;
    private TextView mDesc;
    private ReceiveCommand receiveCommand;
    private CommandInfo commandInfo;
    private SendCommand sendCommand;
    TcpClientConnector tcpClientConnector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_nearby, null);
        NotifyManager.getNotifyManager().addObserver(this);
        commandInfo = new CommandInfo();
        sendCommand = new SendCommand();
        receiveCommand = new ReceiveCommand();
        new UpdateAllData();
        initView();
        initData();

        return view;
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
                    mDesc.setText(String.format("total:%d, on:%d, off:%d, error:%d", titleBean.getTotal(), titleBean.getOn(), titleBean.getOff(), titleBean.getErr()));
                }
            }
        };
        createData();
        //每隔10秒向Server端请求一下当前在线的设备数量
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                sendCommand.sendReadOnLineDevice();
//                mHandler.postDelayed(this, 10 * 1000);
            }
        });

    }


    public void initView() {
        mDesc = (TextView) view.findViewById(R.id.tv_nearby_desc);
        mListView = (RecyclerView) view.findViewById(R.id.lv_nearby);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HomeAdapter(getContext(), mlist, this);
        mListView.setAdapter(mAdapter);

    }

    public void updateDeviceItem() {
        CDABInfo cdabInfo;
        long currentTime = System.currentTimeMillis() / 1000;
        List<DeviceBean> cachelist = new ArrayList<>();
        for (int i = 0; i < cdabInfoList.size(); i++) {
            cdabInfo = cdabInfoList.get(i);
            if (currentTime - cdabInfo.getCreateTime() > 10) {
                //一分钟内CDAB没有进行更新的设备

            } else {
                DeviceBean bean = mCurrentList.get(i);

                if (cdabInfo.isToggleStatus()) {
                    bean.setOpen(true);
                    bean.setStatus(1);
                }
                cachelist.add(bean);
                //CDAB在一分钟内进行更新的设备

            }
        }

        System.out.println("mCurrentList.size():" + mCurrentList.size());

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
    public void onSwich(boolean boo, int position) {
        if (boo) {
            titleBean.addon();
        } else {
            titleBean.addoff();
        }
        DeviceBean bean = mlist.get(position);
        bean.setOpen(boo);
        mlist.set(position,bean);
        mAdapter.notifyItemChanged(position);
        mDesc.setText(String.format("total:%d, on:%d, off:%d, error:%d", titleBean.getTotal(), titleBean.getOn(), titleBean.getOff(), titleBean.getErr()));
    }

    public DeviceBean bean;
    int on = 0;
    int off = 0;
    int err = 0;

    public List<CDABInfo> cdabInfoList = new ArrayList<>();

    //构建数据  模拟:向服务器端拉取数据  ONLINE_DEVICE:代表当前在线的设备
    private void createData() {
        System.out.println("cxx createData");
        cdabInfoList.clear();
        mlist.clear();
        mCurrentList.clear();

        titleBean = new HomeTitleBean();
        titleBean.setTotal(ONLINE_DEVICE);
        bean = null;
        for (int i = 0; i < ONLINE_DEVICE; i++) {
            //需要记录当前每个Item创建的时间，因为一分钟之后没有CDAB自动更新的Item将会被清除掉
            long createTime = System.currentTimeMillis() / 1000;
            CDABInfo cdabInfo = new CDABInfo();
            cdabInfo.setCreateTime(createTime);
            cdabInfo.setPosition(i);
            cdabInfoList.add(cdabInfo);

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
        mAdapter.notifyDataSetChanged();
        //每隔10秒刷新一下当前在线的设备数量
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                updateDeviceItem();
//                mHandler.postDelayed(this, 20 * 1000);
//            }
//        }, 5 * 1000);

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
        if (Constants.NOTIFY_TO_ONLINENUM == type) {
            int num = (int) entity.getData();
            ONLINE_DEVICE = num / 2;
            System.out.println("当前在线设备数:" + ONLINE_DEVICE + "个");
            createData();
        } else if (Constants.NOTIFY_TO_TOGGLESTATE == type) {
            updateTitleNum();
        } else if (Constants.NOTIFY_TO_ITEM == type) {
            String command = (String) entity.getData();
            CombineCommand combineCommand = new CombineCommand();
            combineCommand.getCommand(command);
        }
    }

    //更新标题栏的各个数目
    public void updateTitleNum() {
        on = 0;
        off = 0;
        err = 0;

        for (int i = 0; i < mlist.size(); i++) {
            bean = mlist.get(i);
            System.out.println("bean.getStatus=" + bean.getStatus() + "deViceName：" + bean.getDevName());
            if (bean.getStatus() == 0) {
                on++;
            } else if (bean.getStatus() == 1) {
                off++;
            } else if (bean.getStatus() == 2) {
                err++;
            }
        }
        updateTittleState(on, off, err);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tcpClientConnector.disconnect();
    }
}
