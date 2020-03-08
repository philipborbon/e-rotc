package com.erotc.learning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erotc.learning.R
import com.erotc.learning.adapter.ResultAdapter.ResultViewHolder
import com.erotc.learning.data.DictionaryEntry
import kotlinx.android.synthetic.main.layout_result.view.*

/**
 * Created on 11/12/2018.
 */
class ResultAdapter(private val onClickListener: OnClickListener?) : RecyclerView.Adapter<ResultViewHolder>() {
    private var resultList: List<DictionaryEntry> = arrayListOf()

    interface OnClickListener {
        fun onClick(view: View?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_result, parent, false)
        view.setOnClickListener { view -> onClickListener?.onClick(view) }
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val entry = resultList[position]
        holder.bindTo(entry)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    fun setResultList(resultList: List<DictionaryEntry>) {
        this.resultList = resultList
    }

    class ResultViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(entry: DictionaryEntry) {
            view.text_tagalog.text = entry.tagalog
            view.text_hiligaynon.text = entry.hiligaynon
            view.text_ilocano.text = entry.ilocano
        }

        fun clear() {
            view.text_tagalog.text = ""
            view.text_hiligaynon.text = ""
            view.text_ilocano.text = ""
        }
    }
}