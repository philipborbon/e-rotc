package com.erotc.learning.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.erotc.learning.R
import com.erotc.learning.activity.RecentResultActivity
import com.erotc.learning.adapter.ResultAdapter
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.data.RecentSearchResult
import com.erotc.learning.repository.DictionaryRepository
import kotlinx.android.synthetic.main.fragment_dictionary.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DictionaryFragment : Fragment() {
    private lateinit var dictionaryRepository: DictionaryRepository

    private var resultList: List<DictionaryEntry>? = null
    private var resultAdapter: ResultAdapter? = null
    private var previousTask: AsyncTask<*, *, *>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_search.setOnClickListener { onSearchClick() }
        input_search_key.setOnEditorActionListener(OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                onSearchClick()
                return@OnEditorActionListener true
            }
            false
        })
        input_search_key.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                search()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        val activity = activity ?: return

        dictionaryRepository = DictionaryRepository.getInstance(activity)
        initRecyclerView()
        hideNoResults()
        hideRecentLabel()
    }

    private fun initRecyclerView() {
        resultAdapter = ResultAdapter(object : ResultAdapter.OnClickListener {
            override fun onClick(view: View?) {
                view?.let {
                    val itemIndex = recycler_result_list.getChildLayoutPosition(view)

                    resultList?.get(itemIndex)?.let { dictionaryEntry ->
                        addAndShowRecent(dictionaryEntry)
                    }
                }
            }
        })
        recycler_result_list.adapter = resultAdapter
        recycler_result_list.layoutManager = LinearLayoutManager(activity)
    }

    val isSearchEmpty: Boolean
        get() = input_search_key.text.toString().isEmpty()

    fun clearInputSearch() {
        input_search_key.setText("")
        input_search_key.clearFocus()
    }

    fun onSearchClick() {
        hideKeyboard()
        input_search_key.clearFocus()

        resultList?.let {
            if (it.isNotEmpty()) {
                val indexOfMatch = DictionaryEntry.getIndexOfMatch(input_search_key.text.toString(), it)
                resultList?.get(indexOfMatch)?.let { dictionaryEntry ->
                    addAndShowRecent(dictionaryEntry)
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    fun showRecentResults() {
        if (!dictionaryRepository.isRecentResultEmpty) {
            showRecentLabel()
            hideNoResults()

            val task: AsyncTask<Void, Void, Void> = object : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg p0: Void?): Void? {
                    resultList = dictionaryRepository.getRecentSearchResults(false)

                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    resultAdapter?.setResultList(resultList ?: arrayListOf())
                    resultAdapter?.notifyDataSetChanged()
                    recycler_result_list.scrollToPosition(0)
                }
            }

            task.execute()
            previousTask = task
        } else {
            resultList = ArrayList()
            resultAdapter?.setResultList(resultList ?: arrayListOf())
            resultAdapter?.notifyDataSetChanged()
        }
    }

    private fun hideKeyboard() {
        val activity = activity ?: return
        val view = activity.currentFocus ?: return

        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("StaticFieldLeak")
    fun search() {
        hideRecentLabel()

        previousTask?.cancel(true)

        val keyword = input_search_key.text.toString()
        if (keyword.isEmpty()) {
            showRecentResults()
            return
        }

        val task: AsyncTask<Void, Void, Void> = object : AsyncTask<Void, Void, Void>() {
            override fun onPreExecute() {
                hideNoResults()
            }

            override fun doInBackground(vararg p0: Void?): Void? {
                resultList = dictionaryRepository.search(keyword)

                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                resultAdapter?.setResultList(resultList ?: arrayListOf())
                resultAdapter?.notifyDataSetChanged()

                if (resultList?.size ?: 0 == 0) {
                    showNoResults()
                } else {
                    recycler_result_list.scrollToPosition(0)
                }
            }
        }
        task.execute()
        previousTask = task
    }

    private fun showNoResults() {
        label_no_results.visibility = View.VISIBLE
    }

    private fun hideNoResults() {
        label_no_results.visibility = View.GONE
    }

    private fun showRecentLabel() {
        label_recent_result.visibility = View.VISIBLE
    }

    private fun hideRecentLabel() {
        label_recent_result.visibility = View.GONE
    }

    private fun addAndShowRecent(dictionaryEntry: DictionaryEntry) {
        val recent = RecentSearchResult()
        recent.entryId = dictionaryEntry.id

        dictionaryRepository.saveRecent(recent)

        val intent = Intent(activity, RecentResultActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val keyword = input_search_key.text.toString()
        if (keyword.isEmpty()) {
            showRecentResults()
        }
    }

    companion object {
        private val LOG_TAG = DictionaryFragment::class.java.simpleName
        fun newInstance(): DictionaryFragment {
            return DictionaryFragment()
        }
    }
}