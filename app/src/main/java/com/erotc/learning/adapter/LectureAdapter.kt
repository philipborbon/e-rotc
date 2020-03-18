package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.adapter.LectureAdapter.ResultViewHolder
import com.erotc.learning.data.Lecture
import kotlinx.android.synthetic.main.layout_result.view.*

/**
 * Created on 11/12/2018.
 */
class LectureAdapter(private val onClick: (Lecture) -> Unit) : RecyclerView.Adapter<ResultViewHolder>() {
    private var resultList: List<Lecture> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_result, parent, false))
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val entry = resultList[position]
        holder.bindTo(entry, onClick)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    fun setResultList(resultList: List<Lecture>) {
        this.resultList = resultList
    }

    class ResultViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(lecture: Lecture, onClick: (Lecture) -> Unit) {
            view.text_title.text = lecture.title

            view.setOnClickListener {
                onClick(lecture)
            }
        }
    }
}