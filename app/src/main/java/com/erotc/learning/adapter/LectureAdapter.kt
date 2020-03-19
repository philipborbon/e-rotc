package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.data.Lecture
import com.erotc.learning.data.Topic
import kotlinx.android.synthetic.main.layout_lecture.view.*
import kotlinx.android.synthetic.main.layout_topic.view.*

/**
 * Created on 11/12/2018.
 */
class LectureAdapter(private val onClick: (Lecture) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: List<Any?> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> LectureViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_lecture, parent, false))
            else -> TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_topic, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList[position] is Topic -> 0
            dataList[position] is Lecture -> 1
            else -> -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0 -> {
                val entry = dataList[position]
                (holder as TopicViewHolder).bindTo(entry as Topic)
            }

            1 -> {
                val entry = dataList[position]
                (holder as LectureViewHolder).bindTo(entry as Lecture, onClick)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(dataList: List<Any?>) {
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

    class TopicViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(topic: Topic) {
            view.text_label.text = topic.name
        }
    }
}