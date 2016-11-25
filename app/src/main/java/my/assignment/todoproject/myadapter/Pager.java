package my.assignment.todoproject.myadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.assignment.todoproject.BrowseFragment;
import my.assignment.todoproject.ManageFragment;

/**
 * Created by root on 11/11/16.
 */

public class Pager extends FragmentPagerAdapter {
    //integer to count number of tabs
    int tabCount;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Map<Integer,String> mFragmentTags;
    private  FragmentManager mFragmentManager;
    private static int NUM_ITEMS = 2;

    public Pager(FragmentManager fm){
        super(fm);
        mFragmentManager=fm;
        mFragmentTags=new HashMap<Integer, String>() ;
    }



    public Pager(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount=tabCount;

    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {

        //return mFragmentList.get(position);
        switch (position){
            case 0:
                return new BrowseFragment();

            case 1:
                return new ManageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //return mFragmentList.size();
        return NUM_ITEMS;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Browse";
            case 1:
                return "Manage";
            default:return null;
        }
        //return mFragmentTitleList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }

}
