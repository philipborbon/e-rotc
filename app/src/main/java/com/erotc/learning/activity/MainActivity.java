package com.erotc.learning.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.erotc.learning.R;
import com.erotc.learning.fragment.AboutFragment;
import com.erotc.learning.fragment.DictionaryFragment;
import com.erotc.learning.fragment.GameFragment;
import com.erotc.learning.fragment.InitializeFragment;
import com.erotc.learning.util.ApplicationUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Fragment mCurrentFragment;
    private int mSelectedFragmentId;
    private int mCurrentFragmentId;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if ( mSelectedFragmentId != mCurrentFragmentId ){
                    showSelectedFragment(mSelectedFragmentId);
                }
            }
        };
        mDrawer.addDrawerListener(toggle);

        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if ( ApplicationUtil.shouldInitialize(this) ){
            initialize();
        } else {
            showSelectedFragment(R.id.nav_lecture);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if ( mCurrentFragment instanceof DictionaryFragment ){
                DictionaryFragment dictionaryFragment = (DictionaryFragment) mCurrentFragment;
                if ( dictionaryFragment.isSearchEmpty() ){
                    super.onBackPressed();
                } else {
                    dictionaryFragment.clearInputSearch();
                    dictionaryFragment.showRecentResults();
                }
            } else if (mCurrentFragment instanceof AboutFragment){
                showSelectedFragment(R.id.nav_lecture);
            } else if (mCurrentFragment instanceof GameFragment){
                showSelectedFragment(R.id.nav_lecture);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        mSelectedFragmentId = id;
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showSelectedFragment(int menuId){
        Fragment fragment = null;
        Class fragmentClass = null;
        int titleResourceId = 0;

        if (menuId == R.id.nav_lecture) {
            fragmentClass = DictionaryFragment.class;
            titleResourceId = R.string.nav_label_lecture;
        } else if (menuId == R.id.nav_assessment) {
            fragmentClass = GameFragment.class;
            titleResourceId = R.string.nav_label_assessment;
        } else if (menuId == R.id.nav_about) {
            fragmentClass = AboutFragment.class;
            titleResourceId = R.string.nav_label_about;
        }

        if ( fragmentClass == null ){
            return;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            mCurrentFragment = fragment;
        } catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG, Log.getStackTraceString(e));
        }

        mCurrentFragmentId = menuId;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        mNavigationView.setCheckedItem(menuId);
        setTitle(titleResourceId);
    }

    private void initialize(){
        InitializeFragment fragment = InitializeFragment.newInstance();

        fragment.setListener(new InitializeFragment.InitializeListener() {
            @Override
            public void onDone() {
                showSelectedFragment(R.id.nav_lecture);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
