package edu.wisdom.praycounter2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Chan Chu Man on 11/11/2016.
 */

public class PrayReportFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public PrayReportFragmentStatePagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                PrayListFragment prayListFragment = new PrayListFragment();
                return prayListFragment;
            case 1:
                PrayHistoryFragment prayHistoryFragment = new PrayHistoryFragment();
                return prayHistoryFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
