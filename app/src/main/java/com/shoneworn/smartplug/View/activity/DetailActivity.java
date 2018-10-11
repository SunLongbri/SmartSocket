package com.shoneworn.smartplug.View.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.View.refreshview.RefreshDetail;
import com.shoneworn.smartplug.View.service.TimerService;
import com.shoneworn.smartplug.data.CommandInfo;
import com.shoneworn.smartplug.data.DetailInfo;
import com.shoneworn.smartplug.data.DetailTextViewInfo;
import com.shoneworn.smartplug.data.DetailTextView;
import com.shoneworn.smartplug.data.DeviceBean;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.utils.ReceiveCommand;
import com.shoneworn.smartplug.utils.SendCommand;
import com.shoneworn.smartplug.utils.UpdateAllData;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by admin on 2018/8/31.
 * <p>
 * 设备详情页：刚进入该页面，发送三条相应的指令。
 */

public class DetailActivity extends Activity implements Observer {
    private static DetailInfo detailInfo;
    private TextView mTvDevName;
    private TextView mTvFrequence;
    private TextView mTvTemperature;
    private TextView mTvCurrent;
    private TextView mTvVoltage;
    private TextView mTvPower;
    private TextView mTvName;
    private ImageView mIvBack;
    private ImageView mIvStatus;
    private DeviceBean bean;
    private ReceiveCommand receiveCommand;
    private CommandInfo commandInfo;
    private DetailTextView detailTextView;
    private TextView mTvRemainTime;
    private List<DetailInfo> detaillist;
    private static SharedPreferences spdata;
    private SharedPreferences spnum;
    private RefreshDetail refreshDetail;
    private DetailTextViewInfo detailTextViewInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        new UpdateAllData();
        NotifyManager.getNotifyManager().addObserver(this);
        startService(new Intent(this, TimerService.class));
        Intent intent = getIntent();
        Bundle devBundle = intent.getBundleExtra("device");
        bean = (DeviceBean) devBundle.getSerializable("dev");
        detailInfo = new DetailInfo();
        commandInfo = new CommandInfo();
        receiveCommand = new ReceiveCommand();
        //当第一次进入设备详情页时，更新详情页面里的数据
        initLocalList();
        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    private void initLocalList() {
        refreshDetail = new RefreshDetail();
        spnum = getSharedPreferences("sp_local_num", MODE_PRIVATE);
        spdata = getSharedPreferences("sp_local_data", MODE_PRIVATE);
        String have = spnum.getString("key", "no");
        if (have.equals("have")) {
            return;
        }
        //将所有设备的详细信息进行初始化
        refreshDetail.initDetailData();
        spnum.edit().putString("key", "have").apply();

    }

    public void initView() {
        mIvStatus = (ImageView) findViewById(R.id.iv_detail_status);
        mTvPower = (TextView) findViewById(R.id.tv_detail_power);
        mTvCurrent = (TextView) findViewById(R.id.tv_detail_current);
        mTvVoltage = (TextView) findViewById(R.id.tv_detail_voltage);
        mTvFrequence = (TextView) findViewById(R.id.tv_detail_frequence);
        mTvTemperature = (TextView) findViewById(R.id.tv_detail_temperature);


        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvDevName = (TextView) findViewById(R.id.tv_device_name);
        mTvRemainTime = (TextView) findViewById(R.id.tv_remain_time);

        mIvBack.setOnClickListener(new myClickListener());
        mTvPower.setOnClickListener(new myClickListener());
        mTvCurrent.setOnClickListener(new myClickListener());
        mTvVoltage.setOnClickListener(new myClickListener());
        mTvFrequence.setOnClickListener(new myClickListener());
        mTvTemperature.setOnClickListener(new myClickListener());

        detailTextViewInfo = new DetailTextViewInfo(mTvPower, mTvCurrent, mTvVoltage, mTvFrequence, mTvTemperature, mTvRemainTime);
    }

    private void initData() {
        //根据设备的编号从本地文件中获取对应的值
        String data = spdata.getString(bean.getDeviceId(), null);
        Gson gson = new Gson();
        HashMap<String, String> hashData = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());

        String mPower = hashData.get("power");
        String mCurrent = hashData.get("current");
        String mVoltage = hashData.get("voltage");
        String mFrequence = hashData.get("frequence");
        String mTemperature = hashData.get("temperature");
        String mRemaintime = hashData.get("remaintime");

        detailTextView = new DetailTextView();
        //Power
        mTvPower.setText(mPower + "W");

        detailTextView.setmTvPower(mTvPower);
        if (detailInfo.getmDetailPower() != null)
            mTvPower.setText(detailInfo.getmDetailPower());

        //Current
        mTvCurrent.setText(mCurrent + "A");

        detailTextView.setmTvCurrent(mTvCurrent);
        if (detailInfo.getmDetailCurrent() != null)
            mTvCurrent.setText(detailInfo.getmDetailCurrent());

        //Voltage
        mTvVoltage.setText(mVoltage + "V");

        detailTextView.setmTvVoltage(mTvVoltage);
        if (detailInfo.getmDetailVoltage() != null)
            mTvVoltage.setText(detailInfo.getmDetailVoltage());

        //Frequence
        mTvFrequence.setText(mFrequence + "HZ");

        detailTextView.setmTvFrequence(mTvFrequence);
        if (detailInfo.getmDetailFrequence() != null)
            mTvFrequence.setText(detailInfo.getmDetailFrequence());

        //Temperature
        mTvTemperature.setText(mTemperature + "°C");

        detailTextView.setmTvTemperature(mTvTemperature);
        if (detailInfo.getmDetailTemperature() != null)
            mTvTemperature.setText(detailInfo.getmDetailTemperature());

        mTvDevName.setText(TextUtils.isEmpty(bean.getDevName()) ? "设备详情" : bean.getDevName());

        if (!TextUtils.isEmpty(mRemaintime)) {
            mTvRemainTime.setText(mRemaintime + "  min");
        }

        refreshDetail = new RefreshDetail();
        //更新设备详情页面中设备状态的图标
        refreshDetail.setStatusImg(bean.getStatus(), mIvStatus);

    }

    class myClickListener implements View.OnClickListener {

        SendCommand sendCommand = new SendCommand();

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.tv_detail_power:
                    //发送数据
                    sendCommand.sendReadV();
                    break;
                case R.id.tv_detail_current:
                    sendCommand.sendReadI();
                    break;
                case R.id.tv_detail_voltage:
                    sendCommand.sendReadF();
                    break;
                case R.id.tv_detail_frequence:
                    sendCommand.sendReadT();
                    break;
                case R.id.tv_detail_temperature:
                    sendCommand.sendReadPM();
                    break;
                case R.id.iv_back:
                    onStop();
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, TimerService.class));
    }

    @Override
    public void update(Observable o, Object data) {

        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }
        NotifyMsgEntity entity = (NotifyMsgEntity) data;

        int type = entity.getCode();
        if (Constants.NOTIFY_TO_DETAIL == type) {
            detailInfo = (DetailInfo) entity.getData();
            //更新详情页里面的数据，并将更新后的数据保存到本地
            refreshDetail.updateDetailDataAndSave(detailInfo, detailTextViewInfo, bean);
        }
    }
}
