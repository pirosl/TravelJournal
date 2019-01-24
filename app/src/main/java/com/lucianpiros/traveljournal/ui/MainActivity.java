package com.lucianpiros.traveljournal.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lucianpiros.traveljournal.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.mainactivity_viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        JournalFramentPagerAdapter adapter = new JournalFramentPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainactivity_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    class JournalFramentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public JournalFramentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            //if (position == 0) {
                JournalNotesFragment fragment = new JournalNotesFragment();

                return fragment;
            //}
            //return null;
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 4;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return "Journal Notes";//mContext.getString(R.string.category_usefulinfo);
                //case 1:
                //   return "Steps";//mContext.getString(R.string.category_places);
                default:
                    return null;
            }
        }
    }
}
