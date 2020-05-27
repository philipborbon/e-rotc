package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.data.Lecture
import kotlinx.android.synthetic.main.layout_lecture.view.*

/**
 * Created on 11/12/2018.
 */
class LectureAdapter(private val onClick: (Lecture) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: List<Lecture> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LectureViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_lecture, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lecture = dataList[position]
        (holder as LectureViewHolder).bindTo(lecture, onClick)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(dataList: List<Lecture>) {
        this.dataList = dataList
    }

    class LectureViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(lecture: Lecture, onClick: (Lecture) -> Unit) {
            view.text_title.text = lecture.title

            view.setOnClickListener {
                onClick(lecture)
            }
        }
    }
}