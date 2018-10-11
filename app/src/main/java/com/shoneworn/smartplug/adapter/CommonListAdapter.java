package com.shoneworn.smartplug.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoneworn.smartplug.BaseApplication;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.data.CDABInfo;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.data.ProgressTimeInfo;
import com.shoneworn.smartplug.data.RefreshToggle;
import com.shoneworn.smartplug.data.ToggleViewInfo;
import com.shoneworn.smartplug.interfaces.HomeInterface;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.utils.ParseCommand;
import com.shoneworn.smartplug.utils.ProgressInvisible;
import com.shoneworn.smartplug.utils.ReceiveCommand;
import com.shoneworn.smartplug.utils.SendCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.MODE_PRIVATE;
import static com.shoneworn.smartplug.View.fragment.NearbyFragment.mCurrentList;
import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;


/**
 * Created by admin on 2018/8/30.
 * <p>
 * Implements:对主页面listview的实现
 */

public class CommonListAdapter extends DefaultAdapter implements Observer {

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
    private ParseCommand parseCommand;
    private final RefreshList refreshList;

    public CommonListAdapter(Context mContext, List list, HomeInterface homeInterface) {
        super(list);
        this.mContext = mContext;
        mlist = list;
        mToggleViewList = new ArrayList<>();
        this.homeInterface = homeInterface;

        refreshList = new RefreshList(mlist);
        NotifyManager.getNotifyManager().addObserver(this);

    }

    public List<DeviceBean> getList() {
        return mlist;
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
            refreshList.setStatusImg(position, 2, vh.mImgStatus);
        } else {
            refreshList.setStatusImg(position, isOpen ? 1 : 0, vh.mImgStatus);
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
                    refreshList.setStatusImg(position, isChecked ? 1 : 0, img);
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
            }
        });
    }

    public RefreshToggle toggleState;

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
                String correctResponse = parseData.substring(4, 8);

                String index = parseCommand.getCommandId()+"";

                int indexPosition = refreshList.getindexPosition(index);
                int state = refreshList.getState(parseData);
                //第一点:收到正确应答再解锁
                if (correctResponse.equals("0000")) {
                    ToggleViewInfo pb = mToggleViewList.get(indexPosition);
                    pb.getmProgressBar().setVisibility(View.INVISIBLE);
                    pb.getToggleImageView().setVisibility(View.VISIBLE);
                    refreshList.setToggleImg(indexPosition, state, toggleViewInfo, mToggleViewList);
                }
            }

        } else if (Constants.NOTIFY_TO_TOGGLESTATE == type) {
            String sendToggleState = (String) entity.getData();
            updateToggleState(sendToggleState);
        } else if (Constants.NOTIFY_TO_UPDATE_DATA == type) {
            String parseData = (String) entity.getData();
            parseCommand = new ParseCommand(parseData);
            remaindOnLineTime(parseData);
            //将接收到的数据，解析到温度值等，将他们存储到本地

            int deviceId = parseCommand.getCommandId();

            //获取Id后参数的序号，以此知道当前刷新的是详情页那个参数的值
            String detailIndex = parseCommand.getCommandIndex();

            //获取该参数的值
            String strValue = parseCommand.getCommandValue();

            //将需要更新的值以Json的格式存储到本地文件中
            parseCommand.saveLocalAsJson(deviceId, detailIndex, strValue);
        }

    }

    //记录每个设备没有CDAB需要更新设备的时间
    public void remaindOnLineTime(String parseData) {
        refreshList.refreshOnLineTime(parseData,refreshList,toggleViewInfo,mToggleViewList,cdabInfoList);
    }

    public void updateToggleState(String sendToggleState) {

        refreshList.refreshToggle(sendToggleState,mlist, toggleViewInfo, mToggleViewList);
    }

    class ViewHolder {
        public ImageView mImgStatus;
        public ImageView mSwitch;
        public ImageView mImgSkip;
        public TextView mTvDevName;
        public ProgressBar mPbLock;
    }
}
