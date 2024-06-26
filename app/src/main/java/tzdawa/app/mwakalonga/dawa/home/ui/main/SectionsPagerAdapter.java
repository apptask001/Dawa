package tzdawa.app.mwakalonga.dawa.home.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.fragments.BrandsFragment;
import tzdawa.app.mwakalonga.dawa.fragments.DawaFragment;
import tzdawa.app.mwakalonga.dawa.fragments.HomeFragment;
import tzdawa.app.mwakalonga.dawa.fragments.PostsFragment;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_dawa_homepg,
            R.string.tab_brands_homepg, R.string.tabs_posts_homepg};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //Log.e("VIEW PAGER", String.valueOf(position) );
        // return PlaceholderFragment.newInstance(position + 1);

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DawaFragment();
                break;
            case 1:
                fragment = new BrandsFragment();
                break;
            case 2:
                fragment = new PostsFragment();
                break;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}