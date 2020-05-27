package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.data.Tutorial
import kotlinx.android.synthetic.main.layout_tutorial.view.*

/**
 * Created on 11/12/2018.
 */
class TutorialAdapter(private val onClick: (Tutorial) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: List<Tutorial> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TutorialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_tutorial, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = dataList[position]
        (holder as TutorialViewHolder).bindTo(entry, onClick)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(dataList: List<Tutorial>) {
        this.dataList = dataList
    }

    class TutorialViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(tutorial: Tutorial, onClick: (Tutorial) -> Unit) {
            view.text_title.text = tutorial.title

            view.setOnClickListener {
                onClick(tutorial)
            }
        }
    }
}