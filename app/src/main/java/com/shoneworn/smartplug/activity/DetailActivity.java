package com.shoneworn.smartplug.activity;

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
import com.shoneworn.smartplug.service.TimerService;
import com.shoneworn.smartplug.bean.CommandInfo;
import com.shoneworn.smartplug.bean.DetailInfo;
import com.shoneworn.smartplug.bean.DetailTextView;
import com.shoneworn.smartplug.bean.DeviceBean;
import com.shoneworn.smartplug.utils.Constants;
import com.shoneworn.smartplug.command.ReceiveCommand;
import com.shoneworn.smartplug.command.SendCommand;
import com.shoneworn.smartplug.update.UpdateAllData;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;

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
        initLocalList();
        initView();
    }

    private void initLocalList() {

        spnum = getSharedPreferences("sp_local_num", MODE_PRIVATE);
        spdata = getSharedPreferences("sp_local_data", MODE_PRIVATE);

        String have = spnum.getString("key", "no");
        if (have.equals("have")) {
            return;
        }

        System.out.println("将所有的数据进行了一次重写!!!!");
        HashMap<String, String> detailMap = new HashMap<>();
        for (int i = 0; i < ONLINE_DEVICE; i++) {

            detailMap.put("index", i + "");
            detailMap.put("power", "2");
            detailMap.put("current", "1");
            detailMap.put("voltage", "220");
            detailMap.put("frequence", "60");
            detailMap.put("temperature", "26");

            Gson gson = new Gson();
            String detailData = gson.toJson(detailMap);
            spdata.edit().putString(i + "", detailData).apply();
        }
        spnum.edit().putString("key", "have").apply();

    }

    private void initView() {
        //根据设备的编号从本地文件中获取对应的值
        System.out.println("初始化数据中，从本地文件中取出的数据为:" + bean.getDeviceId());
        String data = spdata.getString(bean.getDeviceId(), null);
        System.out.println("初始化数据中，从本地文件中取出的数据内容为:" + data);
        Gson gson = new Gson();
        HashMap<String, String> hashData = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());

        System.out.println("初始化数据中，取出的hashData=" + hashData);

        String mPower = hashData.get("power");
        String mCurrent = hashData.get("current");
        String mVoltage = hashData.get("voltage");
        String mFrequence = hashData.get("frequence");
        String mTemperature = hashData.get("temperature");
        String mRemaintime = hashData.get("remaintime");

        mIvStatus = (ImageView) findViewById(R.id.iv_detail_status);
        detailTextView = new DetailTextView();
        //Power
        mTvPower = (TextView) findViewById(R.id.tv_detail_power);
        mTvPower.setText(mPower + "W");
        mTvPower.setOnClickListener(new myClickListener());
        detailTextView.setmTvPower(mTvPower);
        if (detailInfo.getmDetailPower() != null)
            mTvPower.setText(detailInfo.getmDetailPower());

        //Current
        mTvCurrent = (TextView) findViewById(R.id.tv_detail_current);
        mTvCurrent.setText(mCurrent + "A");
        mTvCurrent.setOnClickListener(new myClickListener());
        detailTextView.setmTvCurrent(mTvCurrent);
        if (detailInfo.getmDetailCurrent() != null)
            mTvCurrent.setText(detailInfo.getmDetailCurrent());

        //Voltage
        mTvVoltage = (TextView) findViewById(R.id.tv_detail_voltage);
        mTvVoltage.setText(mVoltage + "V");
        mTvVoltage.setOnClickListener(new myClickListener());
        detailTextView.setmTvVoltage(mTvVoltage);
        if (detailInfo.getmDetailVoltage() != null)
            mTvVoltage.setText(detailInfo.getmDetailVoltage());

        //Frequence
        mTvFrequence = (TextView) findViewById(R.id.tv_detail_frequence);
        mTvFrequence.setText(mFrequence + "HZ");
        mTvFrequence.setOnClickListener(new myClickListener());
        detailTextView.setmTvFrequence(mTvFrequence);
        if (detailInfo.getmDetailFrequence() != null)
            mTvFrequence.setText(detailInfo.getmDetailFrequence());

        //Temperature
        mTvTemperature = (TextView) findViewById(R.id.tv_detail_temperature);
        mTvTemperature.setText(mTemperature + "°C");
        mTvTemperature.setOnClickListener(new myClickListener());
        detailTextView.setmTvTemperature(mTvTemperature);
        if (detailInfo.getmDetailTemperature() != null)
            mTvTemperature.setText(detailInfo.getmDetailTemperature());

        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new myClickListener());

        mTvDevName = (TextView) findViewById(R.id.tv_device_name);
        mTvDevName.setText(TextUtils.isEmpty(bean.getDevName()) ? "设备详情" : bean.getDevName());

        mTvRemainTime = (TextView) findViewById(R.id.tv_remain_time);
        if(!TextUtils.isEmpty(mRemaintime)){
            mTvRemainTime.setText(mRemaintime+"  min");
        }

        setStatusImg(bean.getStatus());

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
            //更新详情页里面的数据
            updateDetailData(detailInfo);
        }
    }


    public void updateDetailData(DetailInfo detailInfo) {
        //设备编号
        int mIndex = detailInfo.getmPageIndex();

        //将数据写入到本地文件当中
        //将该对象的Json从本地文件中取出来
        String data = spdata.getString(mIndex + "", null);

        //如果接收到的指令不属于本页面，则不再刷新列表与更新本地文件。
        if(mIndex!=Integer.parseInt(bean.getDeviceId())){
            return;
        }
//       System.out.println("从本地文件中，取出对象的Json为:" + data);
        Gson gson = new Gson();
        //将取出的Json对象转换成可以使用的对象
        HashMap<String, String> dataMap = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());
//        System.out.println("从本地文件中，将Json字符串转化为dataMap=" + dataMap);
        //只有数据不为空的情况下才对数据进行刷新
        if (!TextUtils.isEmpty(detailInfo.getmDetailPower())) {
            String power = Integer.parseInt(detailInfo.getmDetailPower()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailPower()) % 10;
            mTvPower.setText(power + "W");
            dataMap.put("power", power);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailCurrent())) {
            String current = Integer.parseInt(detailInfo.getmDetailCurrent()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailCurrent()) % 10;
            mTvCurrent.setText(current + "A");
            dataMap.put("current", current);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailVoltage())) {
            String voltage = Integer.parseInt(detailInfo.getmDetailVoltage()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailVoltage()) % 10;
            mTvVoltage.setText(voltage + "V");
            dataMap.put("voltage", voltage);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailFrequence())) {
            String frequence = Integer.parseInt(detailInfo.getmDetailFrequence()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailFrequence()) % 10;
            mTvFrequence.setText(frequence + "HZ");
            dataMap.put("frequence", frequence);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailTemperature())) {
            String temperature = Integer.parseInt(detailInfo.getmDetailTemperature()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailTemperature()) % 10;
            mTvTemperature.setText(temperature + "°C");
            dataMap.put("temperature", temperature);
        }

        //解决的是第三个错误
        if (detailInfo.getmRemainTime() >= 0 && detailInfo.getmPageIndex() == Integer.parseInt(bean.getDeviceId())) {
            mTvRemainTime.setText(detailInfo.getmRemainTime() + "   min");
            dataMap.put("remaintime", detailInfo.getmRemainTime() + "");
        }
        //将更新后的dataMap再次转换成Json字符串
        String updateMapData = gson.toJson(dataMap);
//        System.out.println("数据更新后，需要存入到本地文件的Json对象为:" + updateMapData);
        //将更新后的json字符串存入到本地文件中
        spdata.edit().putString(mIndex + "", updateMapData).apply();

    }

    public void setStatusImg(int status) {
        switch (status) {
            case 0:
                mIvStatus.setImageResource(R.mipmap.item_dev_normal);
                break;
            case 1:
                mIvStatus.setImageResource(R.mipmap.item_dev_open);
                break;
            case 2:
                mIvStatus.setImageResource(R.mipmap.item_dev_error);
                break;
            default:
                mIvStatus.setImageResource(R.mipmap.item_dev_normal);
                break;
        }
    }
}
