package com.erotc.learning.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.erotc.learning.R
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.fragment.DetailFragment
import com.erotc.learning.repository.DictionaryRepository
import kotlinx.android.synthetic.main.activity_recent_result.*
import kotlinx.android.synthetic.main.content_recent_result.*

class RecentResultActivity : AppCompatActivity() {
    private var pagerAdapter: PagerAdapter? = null
    private var recentResults: List<DictionaryEntry>? = null

    private lateinit var dictionaryRepository: DictionaryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_result)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dictionaryRepository = DictionaryRepository.getInstance(this)
        recentResults = dictionaryRepository.getRecentSearchResults(true)

        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        
        pager.adapter = pagerAdapter
        pager.currentItem = recentResults?.size ?: 0 - 1
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val entry = recentResults?.get(position)
            return DetailFragment.newInstance(entry?.id ?: 0)
        }

        override fun getCount() = recentResults?.size ?: 0

        override fun getPageTitle(position: Int): CharSequence? {
            val entry = recentResults?.get(position)
            return entry?.tagalog
        }
    }

    companion object {
        private val LOG_TAG = RecentResultActivity::class.java.simpleName
    }
}