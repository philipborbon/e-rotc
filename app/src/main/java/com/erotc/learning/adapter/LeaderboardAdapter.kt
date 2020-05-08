package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.data.Leaderboard
import kotlinx.android.synthetic.main.layout_leaderboard.view.*

/**
 * Created on 5/8/2020.
 */
class LeaderboardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: List<Leaderboard> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LeaderboardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_leaderboard, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LeaderboardViewHolder).bindTo(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(dataList: List<Leaderboard>) {
        this.dataList = dataList
    }

    class LeaderboardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(leaderboard: Leaderboard, position: Int) {
            view.label_rank.text = view.context.getString(R.string.text_rank, position + 1)
            view.label_name.text = leaderboard.name
            view.label_score.text = "${leaderboard.score}"
        }
    }
}