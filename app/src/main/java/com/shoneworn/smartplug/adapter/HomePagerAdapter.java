package com.shoneworn.smartplug.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by admin on 2018/8/30.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mlist;


    public HomePagerAdapter(FragmentManager fm, List<Fragment> mlist) {
        super(fm);
        this.mlist = mlist;
    }

    @Override
    public Fragment getItem(int position) {
        if(mlist==null||mlist.size()==0) return null;
        return mlist.get(position);
    }

    @Override
    public int getCount() {
        return mlist==null?0:mlist.size();
    }


}
