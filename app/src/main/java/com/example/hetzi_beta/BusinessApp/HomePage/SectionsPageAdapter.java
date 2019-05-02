package com.example.hetzi_beta.BusinessApp.HomePage;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentsList = new ArrayList<>();
    private final List<String> mFragmentTitlesList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        mFragmentsList.add(fragment);
        mFragmentTitlesList.add(title);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitlesList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentsList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }
}
