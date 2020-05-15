package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.data.Topic
import com.erotc.learning.data.Tutorial
import kotlinx.android.synthetic.main.layout_topic.view.*
import kotlinx.android.synthetic.main.layout_tutorial.view.*

/**
 * Created on 11/12/2018.
 */
class TutorialAdapter(private val onClick: (Tutorial) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: List<Any?> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> TutorialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_tutorial, parent, false))
            else -> TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_topic, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList[position] is Topic -> 0
            dataList[position] is Tutorial -> 1
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
                (holder as TutorialViewHolder).bindTo(entry as Tutorial, onClick)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(dataList: List<Any?>) {
        this.dataList = dataList
    }

    class TutorialViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(tutorial: Tutorial, onClick: (Tutorial) -> Unit) {
            view.text_title.text = tutorial.title
            view.text_description.text = tutorial.description

            view.setOnClickListener {
                onClick(tutorial)
            }
        }
    }

    class TopicViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(topic: Topic) {
            view.text_label.text = topic.name
        }
    }
}