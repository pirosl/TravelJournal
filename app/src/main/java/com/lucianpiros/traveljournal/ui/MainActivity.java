package com.lucianpiros.traveljournal.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.material.tabs.TabLayout;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.LocationService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

/**
 * Application main activity
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainactivity_viewpager)
    ViewPager viewPager;
    @BindView(R.id.mainactivity_tabs)
    TabLayout tabLayout;

    // array of resource ids used as icons for the tab
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

        JournalFramentPagerAdapter adapter = new JournalFramentPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = getResources().getColorStateList(R.color.tab_tint_color, getTheme());
        } else {
            colors = getResources().getColorStateList(R.color.tab_tint_color);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            assert tab != null;

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

    /**
     * FragmentPageAdapter supporting tab creation.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    class JournalFramentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public JournalFramentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                JournalNotesFragment notesFragment = new JournalNotesFragment();

                return notesFragment;
            }
            if (position == 2) {
                return new MapFragment();
            }
            if (position == 3) {
                return new CalendarContainerFragment();
            }

            AdventuresFragment adventuresFragment = new AdventuresFragment();
            return adventuresFragment;
        }

        @Override
        public int getCount() {
            return tabIconRids.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // No title to be returned here - only icons on tabs
            return null;
        }
    }
}
