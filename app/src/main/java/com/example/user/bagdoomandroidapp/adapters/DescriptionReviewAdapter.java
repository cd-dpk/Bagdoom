package com.example.user.bagdoomandroidapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.user.bagdoomandroidapp.fragments.DescriptionFragment;
import com.example.user.bagdoomandroidapp.fragments.ReviewFragment;

import java.util.List;

/**
 * Created by chandradasdipok on 4/12/2016.
 */
public class DescriptionReviewAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;

    public DescriptionReviewAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DescriptionFragment tab1 = new DescriptionFragment();
                return tab1;
            case 1:
                ReviewFragment tab2 = new ReviewFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
