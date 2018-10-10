package com.shoneworn.smartplug.activity;

/**
 * 主函数的入口，
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.fragment.NearbyFragment;
import com.shoneworn.smartplug.adapter.HomePagerAdapter;
import com.shoneworn.smartplug.network.TcpClientConnector;
import com.shoneworn.smartplug.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private RadioGroup mRgHead;
    private ViewPager mViewPager;
    private HomePagerAdapter mAdapter;
    private List<Fragment> mlist = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        //获取权限
        requestPermission();
    }

    private void initData() {
        mlist.clear();
        mlist.add(new NearbyFragment());
//        mlist.add(new MydevFragment());
        FragmentManager fm = getSupportFragmentManager();
        //主页面:MainAcitivity中存放了两个Fragment，其实可以存放许多Fragment的
        mAdapter = new HomePagerAdapter(fm, mlist);
        mViewPager.setAdapter(mAdapter);
        //设置的默认页面
        mViewPager.setCurrentItem(0);
        TcpClientConnector.getInstance().creatConnect(Constants.TCP_DOMIE,Constants.TCP_PORT);
    }

    private void initView() {
        mRgHead = (RadioGroup) findViewById(R.id.rg_head);
        mViewPager = (ViewPager) findViewById(R.id.vp_home);
        //对主页面上的两个菜单按钮进行监听
        mRgHead.setOnCheckedChangeListener(this);
        //对主页面菜单栏下面的页面变化进行监听
        mViewPager.setOnPageChangeListener(this);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                //已经有网络权限，不做任何处理
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_WIFI_STATE}, 1);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mRgHead.check(R.id.rb_tab1);
                break;
            case 1:
                mRgHead.check(R.id.rb_tab2);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_tab1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_tab2:
                mViewPager.setCurrentItem(1);
                break;

        }
    }
}
