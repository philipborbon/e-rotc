package com.erotc.learning.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.fragment.AboutFragment
import com.erotc.learning.fragment.AssessmentFragment
import com.erotc.learning.fragment.InitializeFragment
import com.erotc.learning.fragment.LectureFragment
import com.erotc.learning.util.ApplicationUtil
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var currentFragment: Fragment? = null
    private var selectedFragmentId = 0
    private var currentFragmentId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (selectedFragmentId != currentFragmentId) {
                    showSelectedFragment(selectedFragmentId)
                }
            }
        }

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (ApplicationUtil.shouldInitialize(this)) {
            initialize()
        } else {
            showSelectedFragment(R.id.nav_lecture)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (currentFragment is LectureFragment) {
                val lectureFragment = currentFragment as LectureFragment
                if (lectureFragment.isSearchEmpty) {
                    super.onBackPressed()
                } else {
                    lectureFragment.clearInputSearch()
                    lectureFragment.showAll()
                }
            } else if (currentFragment is AboutFragment) {
                showSelectedFragment(R.id.nav_lecture)
            } else if (currentFragment is AssessmentFragment) {
                showSelectedFragment(R.id.nav_lecture)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        selectedFragmentId = id
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSelectedFragment(menuId: Int) {
        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null
        var titleResourceId = 0

        when (menuId) {
            R.id.nav_lecture -> {
                fragmentClass = LectureFragment::class.java
                titleResourceId = R.string.nav_label_lecture
            }
            R.id.nav_assessment -> {
                fragmentClass = AssessmentFragment::class.java
                titleResourceId = R.string.nav_label_assessment
            }
            R.id.nav_about -> {
                fragmentClass = AboutFragment::class.java
                titleResourceId = R.string.nav_label_about
            }
        }

        if (fragmentClass == null) {
            return
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
            currentFragment = fragment
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(LOG_TAG, Log.getStackTraceString(e))
        }
        currentFragmentId = menuId

        fragment?.let {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            nav_view.setCheckedItem(menuId)
            setTitle(titleResourceId)
        }
    }

    private fun initialize() {
        val fragment = InitializeFragment.newInstance()
        fragment.setListener(object : InitializeFragment.InitializeListener {
            override fun onDone() {
                showSelectedFragment(R.id.nav_lecture)
            }
        })
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}