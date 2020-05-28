package pers.xiaofeng.xintianyou.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * @author：廿柒
 * @description：fragment适配器
 * @date：2020/3/18
 */
public class MyAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mFragments;

    public MyAdapter(FragmentManager fm , ArrayList<Fragment> fragments) {
        super(fm);
        mFragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}

