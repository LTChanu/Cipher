package com.home.cipher;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class PageAdapter extends FragmentPagerAdapter {
    int numCount;
    FragmentCiph ciph = new FragmentCiph();
    FragmentMeet meet = new FragmentMeet();
    FragmentDone done = new FragmentDone();

    public PageAdapter(@NonNull FragmentManager fm, int numCount) {
        super(fm);
        this.numCount = numCount;
    }

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return ciph;
            case 1: return meet;
            case 2: return done;
        }
        return null;
    }

    @Override
    public int getCount() {
        return numCount;
    }
}
