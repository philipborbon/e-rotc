package com.erotc.learning.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.erotc.learning.R
import com.erotc.learning.adapter.LeaderboardAdapter
import com.erotc.learning.data.Leaderboard
import com.erotc.learning.repository.LearnRepository
import kotlinx.android.synthetic.main.activity_leaderboard.*

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var repository: LearnRepository

    private var leaderboardAdapter: LeaderboardAdapter? = null
    private var leaderboard: List<Leaderboard>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        repository = LearnRepository.getInstance(this)

        initRecyclerView()
        showLeaderboard()
    }

    private fun initRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter()

        recycler.adapter = leaderboardAdapter
        recycler.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("StaticFieldLeak")
    fun showLeaderboard() {
        val task: AsyncTask<Void, Void, Void> = object : AsyncTask<Void, Void, Void>() {
            override fun onPreExecute() {
                hideNoResults()
            }

            override fun doInBackground(vararg p0: Void?): Void? {
                leaderboard = repository.getLeaderboard()

                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                leaderboardAdapter?.setDataList(leaderboard ?: arrayListOf())
                leaderboardAdapter?.notifyDataSetChanged()

                if (leaderboard?.size ?: 0 == 0) {
                    showNoResults()
                } else {
                    hideNoResults()
                }
            }
        }
        task.execute()
    }

    private fun showNoResults() {
        label_no_record.visibility = View.VISIBLE
    }

    private fun hideNoResults() {
        label_no_record.visibility = View.GONE
    }

}
