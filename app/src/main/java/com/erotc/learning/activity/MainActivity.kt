package com.erotc.learning.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.fragment.*
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
        var titleResourceId = 0

        when (menuId) {
            R.id.nav_lecture -> {
                fragment = LectureFragment.newInstance()
                titleResourceId = R.string.nav_label_lecture
            }
            R.id.nav_tutorial -> {
                fragment = TutorialFragment.newInstance()
                titleResourceId = R.string.nav_label_tutorial
            }
            R.id.nav_assessment -> {
                fragment = AssessmentFragment.newInstance()
                titleResourceId = R.string.nav_label_assessment
            }
            R.id.nav_about -> {
                fragment = AboutFragment.newInstance()
                titleResourceId = R.string.nav_label_about
            }
            R.id.nav_leaderboard -> {
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)

                return
            }
        }
        currentFragmentId = menuId

        fragment?.let {
            currentFragment = it

            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container, it).commit()
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