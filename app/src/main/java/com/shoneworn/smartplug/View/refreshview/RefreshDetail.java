package com.shoneworn.smartplug.View.refreshview;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoneworn.smartplug.BaseApplication;
import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.data.DetailInfo;
import com.shoneworn.smartplug.data.DetailTextViewInfo;
import com.shoneworn.smartplug.data.DeviceBean;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.shoneworn.smartplug.utils.Constants.ONLINE_DEVICE;

/**
 * =============================================================
 * <p/>
 * Copyright  : Personal Creation All (c) 2018
 * <p/>
 * Author      : Heaven
 * <p/>
 * Version     : 1.0
 * Date of creation : 2018/10/11 14:00.
 * <p/>
 * Description  :这个刷新详情页面中的数据的一个类
 * <p/>
 * <p/>
 * Revision history :
 * <p/>
 * =============================================================
 */
public class RefreshDetail {

    //这个是更新详情页面中设备状态的一个图标
    public void setStatusImg(int status, ImageView mIvStatus) {
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

    public void initDetailData() {
        SharedPreferences spdata = BaseApplication.getContext().getSharedPreferences("sp_local_data", MODE_PRIVATE);
        System.out.println("将所有的数据进行了一次重写!!!!");
        HashMap<String, String> detailMap = new HashMap<>();
        //初始化数据时，将所有设备详情页面的数据初始化为默认数据
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

    }

    /**
     * 更新详情页面中的数据并将更新后的数据进行刷新
     *
     * @param detailInfo
     * @param detailTextViewInfo
     * @param bean
     */
    public void updateDetailDataAndSave(DetailInfo detailInfo, DetailTextViewInfo detailTextViewInfo, DeviceBean bean) {
        //设备编号
        int mIndex = detailInfo.getmPageIndex();
        SharedPreferences spdata = BaseApplication.getContext().getSharedPreferences("sp_local_data", MODE_PRIVATE);
        //将数据写入到本地文件当中
        //将该对象的Json从本地文件中取出来
        String data = spdata.getString(mIndex + "", null);

        //如果接收到的指令不属于本页面，则不再刷新列表与更新本地文件。
        if (mIndex != Integer.parseInt(bean.getDeviceId())) {
            return;
        }

        Gson gson = new Gson();
        //将取出的Json对象转换成可以使用的对象
        HashMap<String, String> dataMap = gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());

        //只有数据不为空的情况下才对数据进行刷新
        if (!TextUtils.isEmpty(detailInfo.getmDetailPower())) {
            String power = Integer.parseInt(detailInfo.getmDetailPower()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailPower()) % 10;
            detailTextViewInfo.getmTvPower().setText(power + "W");
            dataMap.put("power", power);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailCurrent())) {
            String current = Integer.parseInt(detailInfo.getmDetailCurrent()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailCurrent()) % 10;
            detailTextViewInfo.getmTvCurrent().setText(current + "A");
            dataMap.put("current", current);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailVoltage())) {
            String voltage = Integer.parseInt(detailInfo.getmDetailVoltage()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailVoltage()) % 10;
            detailTextViewInfo.getmTvVoltage().setText(voltage + "V");
            dataMap.put("voltage", voltage);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailFrequence())) {
            String frequence = Integer.parseInt(detailInfo.getmDetailFrequence()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailFrequence()) % 10;
            detailTextViewInfo.getmTvFrequence().setText(frequence + "HZ");
            dataMap.put("frequence", frequence);
        }

        if (!TextUtils.isEmpty(detailInfo.getmDetailTemperature())) {
            String temperature = Integer.parseInt(detailInfo.getmDetailTemperature()) / 10 + "." + Integer.parseInt(detailInfo.getmDetailTemperature()) % 10;
            detailTextViewInfo.getmTvTemperature().setText(temperature + "°C");
            dataMap.put("temperature", temperature);
        }

        //解决的是第三个错误
        if (detailInfo.getmRemainTime() >= 0 && detailInfo.getmPageIndex() == Integer.parseInt(bean.getDeviceId())) {
            detailTextViewInfo.getmTvRemainTime().setText(detailInfo.getmRemainTime() + "   min");
            dataMap.put("remaintime", detailInfo.getmRemainTime() + "");
        }
        //将更新后的dataMap再次转换成Json字符串
        String updateMapData = gson.toJson(dataMap);
        //将更新后的json字符串存入到本地文件中
        spdata.edit().putString(mIndex + "", updateMapData).apply();
    }
}
