package com.shoneworn.smartplug.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.bean.DeviceBean;
import com.shoneworn.smartplug.bean.RefreshToggle;
import com.shoneworn.smartplug.bean.ToggleViewInfo;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.command.SendCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;

/**
 * Created by admin on 2018/8/30.
 * <p>
 * Implements:对主页面listview的实现
 */

public class CommonListAdapter extends BaseAdapter implements Observer {
    private Context mContext;
    private List<DeviceBean> mlist;
    private HomeInterface homeInterface;
    private Handler handler = new Handler();
    private Boolean stopSend = false;
    private View mToogleView;
    private View viewAll;
    private boolean isChecked;
    private Boolean busyTime;
    private ToggleViewInfo toggleViewInfo;
    private ProgressBar mProgressBar;
    private List<ToggleViewInfo> mToggleViewList;
    //用来记录当前哪个设备处于异常状态
    private int mCurrentPosition;

    public CommonListAdapter(Context mContext, List list, HomeInterface homeInterface) {
        this.mContext = mContext;
        mlist = list;
        mToggleViewList = new ArrayList<>();
        this.homeInterface = homeInterface;
        NotifyManager.getNotifyManager().addObserver(this);
    }

    public List<DeviceBean> getList() {
        return mlist;
    }

    @Override
    public int getCount() {
        return mlist == null ? 0 : mlist.size();
    }

    @Override
    public Object getItem(int position) {
        if (mlist == null || mlist.size() == 0) return null;
        return mlist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //当有设备上线的时候，将所有的设备存放到cdabInfoList集合中，一共记录了两个值，一个是条目创建的时间，一个是条目所在的位置
    private List<DeviceBean> deviceBeanList;

    //在初始化数据的时候，将所有的数据都添加进来了
    public void setDeviceBeanList(List<DeviceBean> cabdList) {
        this.deviceBeanList = deviceBeanList;
    }

    public List<DeviceBean> getDeviceBeanList() {
        return deviceBeanList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            vh.mImgStatus = (ImageView) convertView.findViewById(R.id.img_dev);
            //开关按钮
            vh.mSwitch = (ImageView) convertView.findViewById(R.id.chk_dev_switch);
            vh.mImgSkip = (ImageView) convertView.findViewById(R.id.img_skip);
            vh.mTvDevName = (TextView) convertView.findViewById(R.id.tv_dev_name);
            vh.mPbLock = (ProgressBar) convertView.findViewById(R.id.pb_lock);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        DeviceBean bean = mlist.get(position);

        if (bean.isReflash()) {
            vh.mPbLock.setVisibility(View.VISIBLE);
            vh.mSwitch.setVisibility(View.GONE);
        } else {
            vh.mPbLock.setVisibility(View.GONE);
            vh.mSwitch.setVisibility(View.VISIBLE);
        }

        if (bean.isOpen()) {
            vh.mSwitch.setImageResource(R.mipmap.img_on);
        } else {
            vh.mSwitch.setImageResource(R.mipmap.img_off);
        }
        setStatusImg(bean, vh.mImgStatus);
        vh.mSwitch.setTag(bean);
        vh.mTvDevName.setText(bean.getDevName());
        //监听CheckBox状态的改变
        vh.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceBean bean = (DeviceBean) view.getTag();
                // 发切换开关的命令
                setSwitch(bean);
                System.out.println("当前点击的id:" + bean.getDeviceId());
                //  homeInterface.onSwich(isChecked);
            }
        });
        return convertView;
    }

    private SendCommand sendCommand = new SendCommand();

    public void setSwitch(DeviceBean deviceBean) {
        int id = deviceBean.getDeviceId();
        //数据初始化，建立TCP连接
        handler.post(new SwitchRunnable(deviceBean));
    }

    class SwitchRunnable implements Runnable {
        private DeviceBean deviceBean;
        private int deviceId;

        public SwitchRunnable(DeviceBean deviceBean) {
            this.deviceBean = deviceBean;
            this.deviceId = deviceBean.getDeviceId();
        }

        @Override
        public void run() {
            //发送指令
            if (!deviceBean.isOpen()) {   // 从关到开
                sendCommand.sendOpenState(deviceId);
            } else {                     // 从开到关
                sendCommand.sendCloseState(deviceId);
            }
            deviceBean.setReflash(true);
            notifyDataSetChanged();
            new Thread(new Runnable() {
                private int time;
                @Override
                public void run() {
                    while (deviceBean.isReflash()) {
                        try {
                            Thread.sleep(1000);
                            time += 1000;
                            if (time >= 5000) {
                                deviceBean.setReflash(false);
                                notifyDataSetChanged();
                                break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
/*
            ToggleViewInfo pb = mToggleViewList.get(deviceId);
            pb.getmProgressBar().setVisibility(View.VISIBLE);
            pb.getToggleImageView().setVisibility(View.INVISIBLE);
            //5s自动解锁
            ProgressTimeInfo progressTimeInfo = new ProgressTimeInfo();
            progressTimeInfo.setmClickIndex(deviceId);
            progressTimeInfo.setmCreateTime(System.currentTimeMillis());
            ProgressInvisible progressInvisible = new ProgressInvisible(progressTimeInfo, pb.getToggleImageView(), pb.getmProgressBar());
            progressInvisible.whetherInvisible();
//                handler.postDelayed(this, 3 * 1000);*/
        }
    }


    //设置单条目
    private void setStatusImg(DeviceBean bean, ImageView view) {
        int status = bean.getStatus();
        switch (status) {
            case 0:
                view.setImageResource(R.mipmap.item_dev_normal);
                break;
            case 1:
                view.setImageResource(R.mipmap.item_dev_open);
                break;
            case 2:
                view.setImageResource(R.mipmap.item_dev_error);
                break;
            default:
                view.setImageResource(R.mipmap.item_dev_normal);
                break;
        }
    }

    public static List<RefreshToggle> listToggleState = new ArrayList<>();
    public RefreshToggle toggleState;

    //设置单条目蓝牙设备名称前面的图标和开关的状态
    public void setToggleImg(int position, int status) {
        toggleState = new RefreshToggle();
        toggleState.setNum(position);

        toggleViewInfo = mToggleViewList.get(position);

        switch (status) {
            case 1:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_on);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_open);
                toggleState.setToggleState(1);
                break;
            case 0:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_off);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_normal);
                toggleState.setToggleState(0);
                break;
            case 2:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_error);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_error);
                toggleState.setToggleState(2);
                break;
            default:
                toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_off);
                toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_normal);
                break;
        }
        listToggleState.add(toggleState);
    }


    @Override
    public void update(Observable observable, Object data) {


    }





    public void updateToggleState(String sendToggleState) {
        String[] str = sendToggleState.split("_");
        String numFlash = str[0];
        //获取到的ABCD命令中的设备编号,第一个设备编号:00
        int num = Integer.parseInt(numFlash);
        int index = 0;

        //避免在二和四设备之间少三号设备的情况下，造成刷新错误的情况
        DeviceBean dev;
        //根据设备的名称进行刷新，防止因为设备掉线而出现该设备刷新成别的设备的状态
        for (int i = 0; i < mlist.size(); i++) {
            dev = mlist.get(i);
            int deviceNum = dev.getDeviceId();
            if (deviceNum == num) {
                //因为实际
                index = i;
                break;
            }
        }

        String toggleState = str[1];
        int status = 0;
        if (toggleState.equals("0100")) {
            //开状态
            status = 1;
        } else if (toggleState.equals("0000")) {
            //关状态
            status = 0;
        } else if (toggleState.equals("0200")) {
            //异常状态
            status = 2;
        }
        setToggleImg(index, status);
    }

    class ViewHolder {
        public ImageView mImgStatus;
        public ImageView mSwitch;
        public ImageView mImgSkip;
        public TextView mTvDevName;
        public ProgressBar mPbLock;
    }
}
