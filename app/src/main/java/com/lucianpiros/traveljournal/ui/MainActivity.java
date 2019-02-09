package com.lucianpiros.traveljournal.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.LocationService;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainactivity_viewpager)
    ViewPager viewPager;
    @BindView(R.id.mainactivity_tabs)
    TabLayout tabLayout;

    final private static int[] tabIconRids = {R.drawable.ic_notes, R.drawable.ic_adventures, R.drawable.ic_map, R.drawable.ic_calendar};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        ButterKnife.bind(this);

        // Create an adapter that knows which fragment should be shown on each page
        JournalFramentPagerAdapter adapter = new JournalFramentPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = getResources().getColorStateList(R.color.tab_tint_color, getTheme());
        }
        else {
            colors = getResources().getColorStateList(R.color.tab_tint_color);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(tabIconRids[i]);
            Drawable icon = tab.getIcon();

            if (icon != null) {
                icon = DrawableCompat.wrap(icon);
                DrawableCompat.setTintList(icon, colors);
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        LocationService.getInstance().setActivity(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        }
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
            if (position == 0) {
                JournalNotesFragment notesFragment = new JournalNotesFragment();

                return notesFragment;
            }

            AdventuresFragment adventuresFragment = new AdventuresFragment();
            return adventuresFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            /*switch (position) {
                case 0:
                    return mContext.getString(R.string.tab_notes);
                case 1:
                    return mContext.getString(R.string.tab_adventures);
                case 2:
                    return mContext.getString(R.string.tab_map);
                case 3:
                    return mContext.getString(R.string.tab_calendar);
                default:
                    return null;
            }*/
            return null;
        }
    }
}
