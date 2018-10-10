package com.shoneworn.smartplug.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
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
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.ProgressTimeInfo;
import com.shoneworn.smartplug.data.RefreshToggle;
import com.shoneworn.smartplug.data.ToggleViewInfo;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.utils.ProgressInvisible;
import com.shoneworn.smartplug.utils.ReceiveCommand;
import com.shoneworn.smartplug.utils.SendCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.shoneworn.smartplug.View.fragment.NearbyFragment.mCurrentList;
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
    private View toogleView;
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
    private List<CDABInfo> cdabInfoList;

    //在初始化数据的时候，将所有的数据都添加进来了
    public void setCdabList(List<CDABInfo> cabdList) {
        this.cdabInfoList = cabdList;
    }

    public List<CDABInfo> getCdabList() {
        return cdabInfoList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DeviceBean deviceBean = mCurrentList.get(position);

        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            vh.mImgStatus = (ImageView) convertView.findViewById(R.id.img_dev);
            //开关按钮
            vh.mSwitch = (ImageView) convertView.findViewById(R.id.chk_dev_switch);
            toogleView = (ImageView) convertView.findViewById(R.id.chk_dev_switch);
            vh.mImgSkip = (ImageView) convertView.findViewById(R.id.img_skip);
            vh.mTvDevName = (TextView) convertView.findViewById(R.id.tv_dev_name);
            vh.mPbLock = (ProgressBar) convertView.findViewById(R.id.pb_lock);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        //记录当前页面显示的所有开关的ImageView
        toggleViewInfo = new ToggleViewInfo();
        toggleViewInfo.setPosition(position);
        toggleViewInfo.setIconImageView(vh.mImgStatus);
        toggleViewInfo.setToggleImageView(vh.mSwitch);
        toggleViewInfo.setmProgressBar(vh.mPbLock);
        if (mToggleViewList.size() > position) {
            mToggleViewList.set(position, toggleViewInfo);
        } else {
            mToggleViewList.add(toggleViewInfo);
        }


        final DeviceBean bean = mlist.get(position);

        boolean isOpen = bean.isOpen();
        if (isOpen) {
            vh.mSwitch.setImageResource(R.mipmap.img_on);
        } else {
            vh.mSwitch.setImageResource(R.mipmap.img_off);
        }

        final ImageView img = vh.mImgStatus;

        //避免在二和四设备之间少三号设备的情况下，造成刷新错误的情况
        DeviceBean dev = mlist.get(position);
        int numFlash = Integer.parseInt(dev.getDeviceId());

        if (dev.getStatus() == 2) {
            setStatusImg(position, 2, vh.mImgStatus);
        } else {
            setStatusImg(position, isOpen ? 1 : 0, vh.mImgStatus);
        }

        vh.mTvDevName.setText(bean.getDevName());
        //监听CheckBox状态的改变
        vh.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch(position);
                mCurrentPosition = position;
                System.out.println("当前点击的position:" + mCurrentPosition);
                isChecked = !mlist.get(position).isOpen();

                if (isChecked) {
                    ((ImageView) view).setImageResource(R.mipmap.img_on);
                } else {
                    ((ImageView) view).setImageResource(R.mipmap.img_off);
                }
                if (mlist.get(position).getStatus() != 2) {
                    setStatusImg(position, isChecked ? 1 : 0, img);
                    homeInterface.onSwich(isChecked);
                }
                bean.setOpen(isChecked);
                mlist.set(position, bean);
            }
        });

        return convertView;
    }

    private SendCommand sendCommand = null;
    private ReceiveCommand receiveCommand = null;

    public void setSwitch(final int pos) {
        stopSend = false;
        //数据初始化，建立TCP连接
        sendCommand = new SendCommand();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (stopSend) return;
                DeviceBean dev = mlist.get(pos);

                String deviceId = dev.getDeviceId();
                int num = Integer.parseInt(deviceId);

                //发送指令
                if (isChecked) {   //
                    sendCommand.sendOpenState(num);
                    ToggleViewInfo pb = mToggleViewList.get(pos);
                    pb.getToggleImageView().setVisibility(View.INVISIBLE);
                    pb.getmProgressBar().setVisibility(View.VISIBLE);

                    //第二点:5s自动解锁
                    ProgressTimeInfo progressTimeInfo = new ProgressTimeInfo();
                    progressTimeInfo.setmClickIndex(num);
                    progressTimeInfo.setmCreateTime(System.currentTimeMillis());
                    //比较时间
                    ProgressInvisible progressInvisible = new ProgressInvisible(progressTimeInfo, pb.getToggleImageView(), pb.getmProgressBar());
                    progressInvisible.whetherInvisible();

                } else {
                    sendCommand.sendCloseState(num);
                    ToggleViewInfo pb = mToggleViewList.get(pos);
                    pb.getmProgressBar().setVisibility(View.VISIBLE);
                    pb.getToggleImageView().setVisibility(View.INVISIBLE);

                    //5s自动解锁
                    ProgressTimeInfo progressTimeInfo = new ProgressTimeInfo();
                    progressTimeInfo.setmClickIndex(num);
                    progressTimeInfo.setmCreateTime(System.currentTimeMillis());
                    ProgressInvisible progressInvisible = new ProgressInvisible(progressTimeInfo, pb.getToggleImageView(), pb.getmProgressBar());
                    progressInvisible.whetherInvisible();
                }

//                handler.postDelayed(this, 3 * 1000);
            }
        });

    }

    //设置单条目
    private void setStatusImg(int position, int status, View view) {

        DeviceBean bean = mlist.get(position);
        bean.setStatus(status);
        switch (status) {
            case 0:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_normal);
                break;
            case 1:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_open);
                break;
            case 2:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_error);
                break;
            default:
                ((ImageView) view).setImageResource(R.mipmap.item_dev_normal);
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
        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }
        NotifyMsgEntity entity = (NotifyMsgEntity) data;
        int type = entity.getCode();

        if (Constants.NOTIFY_TO_ONOFF == type) {
            String parseData = (String) entity.getData();

            if (parseData.length() == 8) {
                //繁忙状态下的按钮
                String busy = parseData.substring(0, 2);
                if (busy.equals("01")) {
                    //繁忙状态需要更改
                    toggleState = new RefreshToggle();
                    toggleState.setNum(mCurrentPosition);
                    toggleViewInfo = mToggleViewList.get(mCurrentPosition);
                    toggleViewInfo.getToggleImageView().setImageResource(R.mipmap.img_error);
                    toggleViewInfo.getIconImageView().setImageResource(R.mipmap.item_dev_error);
                }
                //对下发应答命令1010进行判断
            } else if (parseData.length() == 36) {

                //只有在正常状态下记录在线时间，异常状态下的设备不记录
                remaindOnLineTime(parseData);
                System.out.println("对控制蓝牙开关的下发应答的指令:" + parseData);
                //这里有一个坑，仅限0-9号设备试用：0600   0000   0200   0000   0000   0000   0100    8C000000
                //从返回的命令中抽出出来的设备编号
                String index = parseData.substring(1, 2);
                String correctResponse = parseData.substring(4, 8);

                //根据设备的id来查找当前设备在哪个Item的条目下
                int indexPosition = 0;
                for (int i = 0; i < mlist.size(); i++) {
                    DeviceBean deviceBean = mlist.get(i);
                    if (index.equals(deviceBean.getDeviceId())) {
                        indexPosition = i;
                        break;
                    }
                }
                String toggleState = parseData.substring(12, 16);

                int state = 0;
                if (toggleState.equals("0100")) {
                    state = 1;
                } else if (toggleState.equals("0000")) {
                    state = 0;
                } else if (toggleState.equals("0200")) {
                    state = 2;
                }
                //第一点:收到正确应答再解锁
                if (correctResponse.equals("0000")) {
                    ToggleViewInfo pb = mToggleViewList.get(indexPosition);
                    pb.getmProgressBar().setVisibility(View.INVISIBLE);
                    pb.getToggleImageView().setVisibility(View.VISIBLE);
                    setToggleImg(indexPosition, state);
                }
            }

        } else if (Constants.NOTIFY_TO_TOGGLESTATE == type) {
            String sendToggleState = (String) entity.getData();
            updateToggleState(sendToggleState);
        } else if (Constants.NOTIFY_TO_UPDATE_DATA == type) {
            String parseData = (String) entity.getData();
            remaindOnLineTime(parseData);
        }

    }

    //记录每个设备没有CDAB需要更新设备的时间
    public void remaindOnLineTime(String parseData) {

        String deviceNum = parseData.substring(0, 2);
        String num = "";
        //有一个坑，当是十个设备的时候，会出现一个问题
        if (deviceNum.contains("0")) {
            num = deviceNum.substring(1, 2);
        } else {
            num = deviceNum;
        }

        //根据设备的id来查找当前设备在哪个Item的条目下
        int index = 0;
        for (int i = 0; i < mlist.size(); i++) {
            DeviceBean deviceBean = mlist.get(i);
            if (num.equals(deviceBean.getDeviceId())) {
                index = i;
                break;
            }
        }

        String state = "";
        if (parseData.length() == 36) {
            state = parseData.substring(12, 16);
        } else if (parseData.length() == 32) {
            state = parseData.substring(8, 12);
        }

        Boolean flage = true;
        int position = Integer.parseInt(num);

        if (state.equals("0100")) {
            setToggleImg(index, 1);
            flage = true;
        } else if (state.equals("0000")) {
            setToggleImg(index, 0);
            flage = false;
        } else if (state.equals("0200")) {
            setToggleImg(index, 2);
            //阻塞状态:
            flage = false;
        }

//        ToggleViewInfo pb = mToggleViewList.get(position);
//        pb.getmProgressBar().setVisibility(View.INVISIBLE);
//        pb.getToggleImageView().setVisibility(View.VISIBLE);

        //将自动上传的CDAB的设备时间进行刷新
        for (int i = 0; i < ONLINE_DEVICE; i++) {
            if (position == i) {
                CDABInfo cdabInfo;
                //从createDate中创造的数据，提取其中的一个
                cdabInfo = cdabInfoList.get(position);
                //设置时间和状态并进行存储
                cdabInfo.setToggleStatus(flage);
                //如果有CDAB更新了该设备，则将时间置为0
                cdabInfo.setCreateTime(System.currentTimeMillis() / 1000);

                cdabInfoList.set(position, cdabInfo);
            }
        }
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
            int deviceNum = Integer.parseInt(dev.getDeviceId());
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
