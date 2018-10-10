package com.shoneworn.smartplug.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shoneworn.smartplug.R;
import com.shoneworn.smartplug.utils.Constants;

/**
 * Created by Administrator on 2018/9/1.
 */

public class SplashActivity extends Activity {
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("sp_config", Context.MODE_PRIVATE);
        String mCurrentIp = sharedPreferences.getString("ip", Constants.TCP_DOMIE).toString();
//        System.out.println("从文件中取到的数据为:" + mCurrentIp);
        Constants.TCP_DOMIE = mCurrentIp;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(params);
        iv.setBackgroundResource(R.mipmap.img_splash);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setContentView(iv);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                SplashActivity.this.finish();
            }
        },2*1000);

    }
}
