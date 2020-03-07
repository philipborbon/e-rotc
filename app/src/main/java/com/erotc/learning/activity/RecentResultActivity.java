package com.erotc.learning.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.erotc.learning.R;
import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.fragment.DetailFragment;
import com.erotc.learning.repository.DictionaryRepository;

import java.sql.SQLException;
import java.util.List;

public class RecentResultActivity extends AppCompatActivity {
    private static final String LOG_TAG = RecentResultActivity.class.getSimpleName();

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private List<DictionaryEntry> mRecentResults;
    private DictionaryRepository mDictionaryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDictionaryRepository = DictionaryRepository.getInstance(this);

        try {
            mRecentResults = mDictionaryRepository.getRecentSearchResults(true);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setCurrentItem((mRecentResults.size() - 1));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DictionaryEntry entry = mRecentResults.get(position);

            return DetailFragment.newInstance(entry.getId());
        }

        @Override
        public int getCount() {
            return mRecentResults.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            DictionaryEntry entry = mRecentResults.get(position);
            return entry.getTagalog();
        }
    }
}
